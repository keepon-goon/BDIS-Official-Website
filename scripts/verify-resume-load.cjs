const fs = require('node:fs')
const path = require('node:path')
const assert = require('node:assert/strict')
const root = path.resolve(__dirname, '..')
const configuration = JSON.parse(fs.readFileSync(path.join(root, '.local/resume-dev.json'), 'utf8'))
const output = path.join(root, '.local/qa/load')
fs.mkdirSync(output, { recursive: true })
const base = process.env.BDIS_TEST_BACKEND || 'http://127.0.0.1:8081'

async function main() {
  const loginResponse = await fetch(base + '/api/admin/login', { method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: configuration.adminUsername, password: configuration.adminPassword }) })
  const login = await loginResponse.json()
  assert.equal(login.code, 0)
  const authorization = { Authorization: 'Bearer ' + login.data.token }
  const before = await (await fetch(base + '/api/admin/resumes', { headers: authorization })).json()
  const sample = JSON.parse(fs.readFileSync(path.join(root, 'bdis-website/src/test/resources/resume-form.json'), 'utf8'))
  const names = ['并发甲', '并发乙', '并发丙', '额外丁']
  const suffix = Date.now().toString().slice(-8)
  const pending = names.map(async (name, index) => {
    const form = { ...sample, name, studentNo: '000' + suffix + index }
    if (index === 2) form.intro = '项目经历：参与接口设计与数据库练习。'.repeat(35)
    const start = performance.now()
    const response = await fetch(base + '/api/resume/previews', { method: 'POST',
      headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form), signal: AbortSignal.timeout(125000) })
    const result = await response.json()
    const elapsedMs = Math.round(performance.now() - start)
    if (response.status !== 200) {
      assert.equal(response.status, 429)
      return { name, studentNo: form.studentNo, status: response.status, elapsedMs }
    }
    assert.ok(result.data.wordFilename.startsWith(name + '-' + form.studentNo + '-'))
    const pdf = await fetch(base + '/api/resume/previews/pdf', { headers: { 'X-Resume-Preview-Token': result.data.previewToken } })
    assert.equal(pdf.status, 200)
    const bytes = Buffer.from(await pdf.arrayBuffer())
    assert.ok(bytes.subarray(0, 5).equals(Buffer.from('%PDF-')))
    const filename = `resume-${index}.pdf`
    fs.writeFileSync(path.join(output, filename), bytes)
    return { name, studentNo: form.studentNo, status: 200, elapsedMs, pdf: filename }
  })
  const start = performance.now()
  const template = await fetch(base + '/api/resume/template')
  assert.equal(template.status, 200)
  await template.arrayBuffer()
  const templateMs = Math.round(performance.now() - start)
  const requests = await Promise.all(pending)
  assert.deepEqual(requests.map(item => item.status).sort(), [200, 200, 200, 429])
  const after = await (await fetch(base + '/api/admin/resumes', { headers: authorization })).json()
  assert.equal(after.data.total, before.data.total)
  const evidence = { requests, templateMs, formalRecordsBefore: before.data.total, formalRecordsAfter: after.data.total }
  fs.writeFileSync(path.join(output, 'evidence.json'), JSON.stringify(evidence, null, 2))
  console.log(JSON.stringify(evidence, null, 2))
}
main().catch(error => { console.error(error.message); process.exitCode = 1 })
