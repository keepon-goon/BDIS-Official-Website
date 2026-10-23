param(
    [switch]$SkipBuild,
    [switch]$Wait
)

$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path $PSScriptRoot -Parent
$taskLocal = Join-Path $taskRoot '.local'
$taskConfiguration = Join-Path $taskLocal 'resume-dev.json'
if (!(Test-Path -LiteralPath $taskConfiguration)) {
    throw '缺少.local/resume-dev.json，请按本地运行说明准备开发数据库配置'
}
$taskSettings = Get-Content -LiteralPath $taskConfiguration -Raw -Encoding UTF8 | ConvertFrom-Json
foreach ($taskPort in @(8081, 5173)) {
    if (Get-NetTCPConnection -LocalPort $taskPort -State Listen -ErrorAction SilentlyContinue) {
        throw "端口${taskPort}已被占用，请先检查现有进程；本脚本不会关闭其他程序"
    }
}
if (!$SkipBuild) {
    Push-Location (Join-Path $taskRoot 'bdis-website')
    try {
        & mvn.cmd -q -DskipTests package
        if ($LASTEXITCODE -ne 0) { throw '后端构建失败' }
    } finally { Pop-Location }
}
$env:BDIS_DB_URL = 'jdbc:mysql://127.0.0.1:3306/bdis?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8'
$env:BDIS_DB_USERNAME = [string]$taskSettings.username
$env:BDIS_DB_PASSWORD = [string]$taskSettings.password
$env:BDIS_JWT_SECRET = [string]$taskSettings.jwtSecret
$env:BDIS_RESUME_STORAGE_ROOT = Join-Path $taskLocal 'private-resumes'
$env:BDIS_RESUME_CONVERTER = 'docker'
$env:DOCKER_HOST = 'npipe:////./pipe/dockerDesktopLinuxEngine'
$env:BDIS_MAIL_USERNAME = [string]$taskSettings.mailUsername
$env:BDIS_MAIL_PASSWORD = [string]$taskSettings.mailPassword
$env:SERVER_ADDRESS = '127.0.0.1'
$taskJar = Join-Path $taskRoot 'bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar'
$taskBackend = Start-Process -FilePath (Get-Command java.exe).Source -ArgumentList @('-Xms128m', '-Xmx512m', '-jar', "`"$taskJar`"") -WorkingDirectory (Join-Path $taskRoot 'bdis-website') -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $taskLocal 'backend.log') -RedirectStandardError (Join-Path $taskLocal 'backend-error.log')
$taskVite = Join-Path $taskRoot 'bdis-web/node_modules/vite/bin/vite.js'
if (!(Test-Path -LiteralPath $taskVite)) {
    Stop-Process -Id $taskBackend.Id
    throw '缺少前端依赖，请先在bdis-web目录执行npm ci'
}
$taskFrontend = Start-Process -FilePath (Get-Command node.exe).Source -ArgumentList @("`"$taskVite`"", '--host', '127.0.0.1', '--port', '5173', '--strictPort') -WorkingDirectory (Join-Path $taskRoot 'bdis-web') -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $taskLocal 'frontend.log') -RedirectStandardError (Join-Path $taskLocal 'frontend-error.log')
$taskProcesses = @{
    backend = @{ id = $taskBackend.Id; started = $taskBackend.StartTime.ToUniversalTime().ToString('o') }
    frontend = @{ id = $taskFrontend.Id; started = $taskFrontend.StartTime.ToUniversalTime().ToString('o') }
}
[IO.File]::WriteAllText((Join-Path $taskLocal 'resume-processes.json'), ($taskProcesses | ConvertTo-Json -Depth 3), [Text.UTF8Encoding]::new($false))
$taskReady = $false
$taskDeadline = (Get-Date).AddSeconds(45)
while ((Get-Date) -lt $taskDeadline) {
    try {
        $taskStatus = (& curl.exe -s -o NUL -w "%{http_code}" http://127.0.0.1:8081/api/resume/template)
        if ($taskStatus -eq '200') {
            $taskReady = $true
            break
        }
    } catch { }
    try {
        if ((Invoke-WebRequest 'http://127.0.0.1:8081/api/resume/template' -UseBasicParsing -TimeoutSec 2).StatusCode -eq 200) {
            $taskReady = $true
            break
        }
    } catch { }
    Start-Sleep -Milliseconds 500
}
if (!$taskReady) { throw '后端未在45秒内就绪，请查看.local/backend.log及backend-error.log' }
Write-Output '前端：http://127.0.0.1:5173/resume/apply'
Write-Output '后台：http://127.0.0.1:5173/admin/resumes'
Write-Output '日志保存在.local；邮件发件配置从本地私有配置读取。'
if ($Wait) {
    Wait-Process -Id $taskBackend.Id, $taskFrontend.Id
}
