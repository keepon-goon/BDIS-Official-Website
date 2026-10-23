$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path $PSScriptRoot -Parent
$taskState = Join-Path $taskRoot '.local/resume-processes.json'
if (!(Test-Path -LiteralPath $taskState)) { Write-Output '没有本任务启动的进程记录'; exit }
$taskProcesses = Get-Content -LiteralPath $taskState -Raw -Encoding UTF8 | ConvertFrom-Json
foreach ($taskName in @('frontend', 'backend')) {
    $taskRecord = $taskProcesses.$taskName
    $taskProcess = Get-Process -Id $taskRecord.id -ErrorAction SilentlyContinue
    if ($taskProcess -and $taskProcess.StartTime.ToUniversalTime().Ticks -eq ([datetime]$taskRecord.started).ToUniversalTime().Ticks) {
        Stop-Process -Id $taskRecord.id
        $taskProcess.WaitForExit(10000) | Out-Null
        Write-Output "$taskName 已停止"
    } elseif ($taskProcess) {
        Write-Output "$taskName 的进程标识已变化，保留该进程，请检查端口"
    }
}
