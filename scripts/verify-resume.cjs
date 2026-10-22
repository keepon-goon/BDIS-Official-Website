const fs = require('node:fs')
const path = require('node:path')
const assert = require('node:assert/strict')
const crypto = require('node:crypto')
const root = path.resolve(__dirname, '..')
const configuration = JSON.parse(fs.readFileSync(path.join(root, '.local/resume-dev.json'), 'utf8'))
const output = path.join(root, '.local/qa/e2e')
fs.mkdirSync(output, { recursive: true })
const backend = process.env.BDIS_TEST_BACKEND || 'http://127.0.0.1:8081'
const frontend = process.env.BDIS_TEST_FRONTEND || 'http://127.0.0.1:5173'
const sha = bytes => crypto.createHash('sha256').update(bytes).digest('hex')

async function jsonRequest(url, method = 'GET', data, token) {
  const response = await fetch(backend + url, {
    method, headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}) },
    ...(data === undefined ? {} : { body: JSON.stringify(data) })
  })
  assert.equal(response.status, 200, `接口应成功：${method} ${url}`)
  const body = await response.json()
  assert.equal(body.code, 0, `业务应成功：${method} ${url}`)
  return body.data
}

async function main() {
  const login = await jsonRequest('/api/admin/login', 'POST', { username: configuration.adminUsername, password: configuration.adminPassword })
  const token = login.token
  const evidencePath = path.join(output, 'evidence.json')
  if (process.argv.includes('--restart-check')) {
    const evidence = JSON.parse(fs.readFileSync(evidencePath, 'utf8'))
    for (const id of evidence.submissionIds) {
      const response = await fetch(backend + `/api/admin/resumes/${id}/word`, { headers: { Authorization: 'Bearer ' + token } })
      assert.equal(response.status, 200)
      assert.equal(sha(Buffer.from(await response.arrayBuffer())), evidence.photoWordSha256)
    }
    const settings = await jsonRequest('/api/admin/resumes/settings', 'GET', undefined, token)
    assert.equal(settings.sendMode, 'MANUAL')
    assert.equal(settings.recipient, 'qa@example.com')
    console.log('重启后正式Word、数据库记录和邮件设置持久性验证通过')
    return
  }
  const { chromium } = require(process.env.BDIS_PLAYWRIGHT_MODULE || 'playwright')
  const browser = await chromium.launch({ channel: 'chrome', headless: true })
  const context = await browser.newContext({ viewport: { width: 1365, height: 1000 }, acceptDownloads: true })
  const page = await context.newPage()
  page.setDefaultTimeout(20000)
  const errors = []
  page.on('pageerror', error => errors.push(error.message))
  page.on('console', message => { if (message.type() === 'error') console.error('浏览器：' + message.text()) })
  page.on('requestfailed', request => console.error('请求失败：' + request.url().split('?')[0] + ' ' + request.failure()?.errorText))
  let originalSettings
  try {
    const anonymous = await fetch(backend + '/api/admin/resumes')
    assert.equal(anonymous.status, 401)
    originalSettings = await jsonRequest('/api/admin/resumes/settings', 'GET', undefined, token)
    await jsonRequest('/api/admin/resumes/settings', 'PUT', { sendMode: 'MANUAL', recipient: 'qa@example.com' }, token)
    const form = JSON.parse(fs.readFileSync(path.join(root, 'bdis-website/src/test/resources/resume-form.json'), 'utf8'))
    form.name = '联调同学'
    form.studentNo = '0000' + Date.now().toString().slice(-8)
    form.gender = '沃尔玛购物袋'
    await page.goto(frontend + '/resume/preview')
    await page.getByRole('heading', { name: '请先生成报名表预览' }).waitFor()
    await page.waitForLoadState('networkidle')
    await page.goto(frontend + '/resume/apply')
    await page.locator('#resume-major').click()
    await page.getByText('请先选择学院', { exact: true }).first().waitFor()
    await page.keyboard.press('Escape')
    await page.locator('#resume-birthMonth').click()
    assert.ok((await page.locator('.el-picker-panel:visible').innerText()).includes(String(new Date().getFullYear() - 18)))
    await page.keyboard.press('Escape')
    async function choose(key, value) {
      const input = page.locator('#resume-' + key)
      await input.click()
      const controls = await input.getAttribute('aria-controls')
      const menu = page.locator('[id="' + controls + '"]')
      await menu.getByRole('option', { name: value, exact: true }).click()
      await menu.waitFor({ state: 'hidden' })
    }
    await choose('gender', '武装直升机')
    await choose('gender', '沃尔玛购物袋')
    await choose('gender', form.gender)
    for (const key of ['ethnicity', 'college']) {
      await choose(key, '其他')
      await page.locator('#resume-' + key + '-other').fill('测试' + key)
    }
    await choose('major', '其他')
    await page.locator('#resume-major-other').fill('自填测试专业')
    await choose('ethnicity', '汉族')
    await choose('college', '两江人工智能学院')
    assert.equal(await page.locator('#resume-major').inputValue(), '')
    await choose('major', '人工智能')
    await choose('college', '计算机科学与工程学院')
    assert.equal(await page.locator('#resume-major').inputValue(), '')
    await page.locator('#resume-major').click()
    assert.equal(await page.locator('.el-select-dropdown:visible .el-select-dropdown__item').filter({ hasText: /^人工智能$/ }).count(), 0)
    await page.keyboard.press('Escape')
    await choose('major', '大数据管理与应用')
    form.college = '计算机科学与工程学院'
    form.major = '大数据管理与应用'
    await page.getByRole('button', { name: '生成预览', exact: true }).scrollIntoViewIfNeeded()
    await page.screenshot({ path: path.join(output, 'choice-initial.png'), fullPage: true })
    const awards = page.locator('.resume-choice').nth(0)
    await awards.getByRole('button', { name: '无', exact: true }).click()
    await awards.getByText('让我们一起谱写自己的篇章').waitFor()
    assert.equal(await page.locator('#resume-awards').inputValue(), '无')
    await page.screenshot({ path: path.join(output, 'awards-no.png'), fullPage: true })
    await awards.getByRole('button', { name: '重新选择' }).click()
    await awards.getByRole('button', { name: '有', exact: true }).click()
    await page.locator('#resume-awards').fill(form.awards)
    assert.equal(await page.locator('#resume-awards').getAttribute('placeholder'), '让我们欣赏你的事迹')
    const showcase = page.locator('.resume-choice').nth(1)
    await showcase.getByRole('button', { name: '不告诉你', exact: true }).click()
    await showcase.getByText('伤心了...(ㄒoㄒ)').waitFor()
    await page.screenshot({ path: path.join(output, 'showcase-no.png'), fullPage: true })
    await showcase.getByRole('button', { name: '重新选择' }).click()
    await showcase.getByRole('button', { name: '小露一手', exact: true }).click()
    await page.locator('#resume-showcase').fill(form.showcase)
    assert.equal(await page.locator('#resume-showcase').getAttribute('placeholder'), 'can can need')
    assert.equal(await page.locator('#resume-intro').getAttribute('placeholder'), '让我们听听你的故事')
    for (const key of ['name', 'studentNo', 'phone', 'specialties', 'gpa', 'intro']) {
      await page.locator('#resume-' + key).fill(form[key])
    }
    await page.getByText('否', { exact: true }).click()
    await page.locator('#resume-phone').fill('123')
    await page.getByRole('button', { name: '生成预览', exact: true }).click()
    await page.getByText('请填写有效的手机号或带区号的固定电话', { exact: true }).waitFor()
    assert.ok(page.url().endsWith('/resume/apply'))
    await page.locator('#resume-phone').fill(form.phone)
    await page.screenshot({ path: path.join(output, 'apply-desktop.png'), fullPage: true })
    const generated = page.waitForResponse(response => response.url().endsWith('/api/resume/previews') && response.request().method() === 'POST', { timeout: 130000 })
    await page.getByRole('button', { name: '生成预览', exact: true }).click()
    const preview = (await (await generated).json()).data
    console.log('在线预览接口已成功，正在进入预览页')
    await page.waitForURL('**/resume/preview', { timeout: 130000 })
    const confirm = page.getByRole('button', { name: '确认当前版本并提交' })
    await confirm.click({ trial: true, timeout: 130000 })
    console.log('两页PDF已在浏览器完成绘制')
    assert.equal(await page.locator('.resume-pdf canvas').count(), 2)
    await page.getByText('如果无法显示,请检查是否开启了IDM插件,关掉它', { exact: true }).waitFor()
    const noPhotoDownload = page.waitForEvent('download')
    await page.getByRole('button', { name: '下载当前 Word', exact: true }).click()
    const noPhoto = await noPhotoDownload
    assert.equal(noPhoto.suggestedFilename(), preview.wordFilename)
    assert.ok(preview.wordFilename.startsWith(form.name + '-' + form.studentNo + '-'))
    assert.ok(!preview.wordFilename.includes('202X'))
    await noPhoto.saveAs(path.join(output, 'no-photo.docx'))
    async function picture(filename, width, height, color) {
      const data = await page.evaluate(({ width, height, color }) => {
        const canvas = document.createElement('canvas')
        canvas.width = width
        canvas.height = height
        const graphics = canvas.getContext('2d')
        graphics.fillStyle = color
        graphics.fillRect(0, 0, width, height)
        return canvas.toDataURL('image/png').split(',')[1]
      }, { width, height, color })
      const file = path.join(output, filename)
      fs.writeFileSync(file, Buffer.from(data, 'base64'))
      return file
    }
    const square = await picture('square.png', 400, 400, '#555555')
    const red = await picture('red.png', 400, 600, '#ce3947')
    const blue = await picture('blue.png', 400, 600, '#275c9a')
    const photoInput = page.getByTestId('resume-photo-input')
    await photoInput.setInputFiles({ name: 'invalid.png', mimeType: 'image/png', buffer: Buffer.from([1, 2, 3]) })
    await page.getByRole('alert').filter({ hasText: '照片无法读取' }).waitFor()
    await confirm.click({ trial: true })
    const largePhoto = path.join(output, 'photo-over-five-mb.png')
    fs.writeFileSync(largePhoto, Buffer.concat([fs.readFileSync(red), Buffer.alloc(6 * 1024 * 1024)]))
    let current = preview
    for (const file of [square, largePhoto, blue]) {
      const response = page.waitForResponse(item => item.url().endsWith('/api/resume/previews/photo') && item.request().method() === 'POST', { timeout: 130000 })
      await photoInput.setInputFiles(file)
      const result = await response
      assert.equal(result.status(), 200)
      const next = (await result.json()).data
      const superseded = await fetch(backend + '/api/resume/previews/pdf', { headers: { 'X-Resume-Preview-Token': current.previewToken } })
      assert.equal(superseded.status, 410)
      current = next
      await confirm.click({ trial: true, timeout: 130000 })
    }
    await photoInput.setInputFiles({ name: 'oversize.png', mimeType: 'image/png', buffer: Buffer.alloc(10 * 1024 * 1024 + 1) })
    await page.getByRole('alert').filter({ hasText: '单张照片不能超过10MB' }).waitFor()
    await confirm.click({ trial: true })
    const photoDownload = page.waitForEvent('download')
    await page.getByRole('button', { name: '下载当前 Word', exact: true }).click()
    const downloaded = await photoDownload
    assert.equal(downloaded.suggestedFilename(), preview.wordFilename)
    const photoWordPath = path.join(output, 'photo.docx')
    await downloaded.saveAs(photoWordPath)
    const photoHash = sha(fs.readFileSync(photoWordPath))
    assert.notEqual(photoHash, sha(fs.readFileSync(path.join(output, 'no-photo.docx'))))
    await page.screenshot({ path: path.join(output, 'preview-desktop.png'), fullPage: true })
    const submitted = page.waitForResponse(response => response.url().endsWith('/api/resume/confirm') && response.request().method() === 'POST')
    await confirm.click()
    const online = (await (await submitted).json()).data
    assert.equal(online.mailStatus, 0)
    await page.getByText('报名已保存', { exact: true }).waitFor()
    await page.goto(frontend + '/resume/upload')
    for (const key of ['name', 'studentNo', 'phone']) await page.locator('#upload-' + key).fill(form[key])
    await page.getByTestId('resume-word-input').setInputFiles(photoWordPath)
    const uploaded = page.waitForResponse(response => response.url().endsWith('/api/resume/submit') && response.request().method() === 'POST', { timeout: 130000 })
    await page.getByRole('button', { name: '上传并生成预览' }).click()
    assert.equal((await (await uploaded).json()).data.sourceType, 'UPLOAD')
    await page.waitForURL('**/resume/preview', { timeout: 130000 })
    await confirm.click({ trial: true, timeout: 130000 })
    assert.equal(await page.getByTestId('resume-photo-input').count(), 0)
    const uploadSubmission = page.waitForResponse(response => response.url().endsWith('/api/resume/confirm') && response.request().method() === 'POST')
    await confirm.click()
    const offline = (await (await uploadSubmission).json()).data
    console.log('在线及普通Word提交已完成')
    for (const id of [online.submissionId, offline.submissionId]) {
      const response = await fetch(backend + `/api/admin/resumes/${id}/word`, { headers: { Authorization: 'Bearer ' + token } })
      assert.equal(response.status, 200)
      assert.equal(sha(Buffer.from(await response.arrayBuffer())), photoHash)
    }
    for (const [file, pages] of [['uploaded-6-pages.docx', 6], ['uploaded-large.docx', 1]]) {
      console.log('正在验证上传样本：' + file)
      await page.goto(frontend + '/resume/upload')
      for (const key of ['name', 'studentNo', 'phone']) await page.locator('#upload-' + key).fill(form[key])
      const artifact = path.join(root, 'bdis-website/target/test-artifacts', file)
      assert.ok(fs.existsSync(artifact), '先运行真实转换集成测试以准备Word样本')
      await page.getByTestId('resume-word-input').setInputFiles(artifact)
      const upload = page.waitForResponse(response => response.url().endsWith('/api/resume/submit') && response.request().method() === 'POST', { timeout: 130000 })
      await page.getByRole('button', { name: '上传并生成预览' }).click()
      assert.equal((await upload).status(), 200)
      await page.waitForURL('**/resume/preview', { timeout: 130000 })
      await confirm.click({ trial: true, timeout: 130000 })
      assert.equal(await page.locator('.resume-pdf canvas').count(), pages)
      await page.screenshot({ path: path.join(output, 'preview-' + pages + '-pages.png'), fullPage: true })
    }
    await page.goto(frontend + '/resume/upload')
    for (const key of ['name', 'studentNo', 'phone']) await page.locator('#upload-' + key).fill(form[key])
    const oversizeWord = path.join(output, 'oversize.docx')
    const descriptor = fs.openSync(oversizeWord, 'w')
    try { fs.ftruncateSync(descriptor, 50 * 1024 * 1024 + 1) } finally { fs.closeSync(descriptor) }
    await page.getByTestId('resume-word-input').setInputFiles(oversizeWord)
    await page.getByRole('alert').filter({ hasText: 'Word文档不能超过50MB' }).waitFor()
    const tooLarge = new FormData()
    tooLarge.append('resume', await fs.openAsBlob(oversizeWord), 'oversize.docx')
    for (const key of ['name', 'studentNo', 'phone']) tooLarge.append(key, form[key])
    assert.equal((await fetch(backend + '/api/resume/submit', { method: 'POST', body: tooLarge })).status, 413)
    await page.getByTestId('resume-word-input').setInputFiles(path.join(root, 'bdis-website/target/test-artifacts/uploaded-7-pages.docx'))
    const rejected = page.waitForResponse(response => response.url().endsWith('/api/resume/submit') && response.request().method() === 'POST', { timeout: 130000 })
    await page.getByRole('button', { name: '上传并生成预览' }).click()
    assert.equal((await rejected).status(), 422)
    await page.getByRole('alert').filter({ hasText: '1到6页' }).waitFor()
    fs.unlinkSync(oversizeWord)
    await page.goto(frontend + '/admin/resumes')
    await page.waitForURL('**/admin/login')
    await page.evaluate(token => { localStorage.setItem('adminToken', token); localStorage.setItem('adminNickname', '本地开发管理员') }, token)
    await page.goto(frontend + '/admin/resumes')
    await page.locator('.el-table__body-wrapper').getByText(form.studentNo).first().waitFor()
    const firstRow = page.locator('.el-table__body-wrapper tr').filter({ hasText: form.studentNo }).first()
    await firstRow.getByRole('button', { name: '详情', exact: true }).click()
    await page.locator('.el-dialog:visible').getByText(form.studentNo, { exact: true }).waitFor()
    await page.locator('.el-dialog:visible .el-dialog__headerbtn').click()
    await firstRow.getByRole('button', { name: 'PDF', exact: true }).click()
    await page.locator('.el-dialog:visible canvas').first().waitFor()
    await page.locator('.el-dialog:visible .el-dialog__headerbtn').click()
    const adminDownload = page.waitForEvent('download')
    await firstRow.getByRole('button', { name: 'Word', exact: true }).click()
    const adminWord = await adminDownload
    assert.equal(adminWord.suggestedFilename(), preview.wordFilename)
    await adminWord.saveAs(path.join(output, 'admin-word.docx'))
    assert.equal(sha(fs.readFileSync(path.join(output, 'admin-word.docx'))), photoHash)
    await page.screenshot({ path: path.join(output, 'admin-desktop.png'), fullPage: true })
    const mobile = await browser.newContext({ viewport: { width: 390, height: 844 }, isMobile: true, deviceScaleFactor: 1 })
    const mobilePage = await mobile.newPage()
    await mobilePage.goto(frontend + '/resume/apply')
    await mobilePage.getByRole('heading', { name: '在线填写招新报名表' }).waitFor()
    await mobilePage.locator('.resume-choice').first().waitFor()
    assert.ok(await mobilePage.evaluate(() => document.documentElement.scrollWidth <= 392), '390像素手机视口不应被桌面导航撑宽')
    await mobilePage.screenshot({ path: path.join(output, 'apply-mobile.png'), fullPage: true })
    await mobilePage.emulateMedia({ reducedMotion: 'reduce' })
    await mobilePage.getByRole('button', { name: '有', exact: true }).click()
    await mobilePage.locator('#resume-awards').fill('减少动态效果测试')
    await mobilePage.getByRole('button', { name: '不告诉你', exact: true }).click()
    await mobilePage.getByText('伤心了...(ㄒoㄒ)').waitFor()
    await mobilePage.goto(frontend + '/resume/preview')
    await mobilePage.getByRole('heading', { name: '请先生成报名表预览' }).waitFor()
    await mobile.close()
    assert.deepEqual(errors, [], '页面不应有运行时异常')
    const evidence = { submissionIds: [online.submissionId, offline.submissionId], studentNo: form.studentNo,
      photoWordSha256: photoHash, filename: preview.wordFilename, runtimeErrors: errors, createdAt: new Date().toISOString() }
    fs.writeFileSync(evidencePath, JSON.stringify(evidence, null, 2))
    console.log('浏览器流程和真实MySQL联调通过；测试报名编号：' + evidence.submissionIds.join('、'))
    console.log('菜单联动、日历、电话、双按钮动画、10MB照片、六页/大Word、后台文件一致性和手机减弱动画通过')
    if (process.env.BDIS_KEEP_TEST_SETTINGS !== 'true')
      await jsonRequest('/api/admin/resumes/settings', 'PUT', { sendMode: originalSettings.sendMode, recipient: originalSettings.recipient }, token)
    originalSettings = null
  } catch (error) {
    await page.screenshot({ path: path.join(output, 'failed-state.png'), fullPage: true }).catch(() => {})
    console.error('失败页面：' + page.url())
    throw error
  } finally {
    if (originalSettings) await jsonRequest('/api/admin/resumes/settings', 'PUT', { sendMode: originalSettings.sendMode, recipient: originalSettings.recipient }, token).catch(() => {})
    await browser.close()
  }
}

main().catch(error => { console.error(error.message); process.exitCode = 1 })
