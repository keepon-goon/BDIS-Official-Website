import test from 'node:test'
import assert from 'node:assert/strict'
import { readFile } from 'node:fs/promises'
import vm from 'node:vm'
import { parse } from '@vue/compiler-sfc'
import { parse as parseJavaScript } from '@babel/parser'
import axios from 'axios'
import { resumeApi } from '../src/utils/resumeApi.js'

test('上传页面应发送真实文件、完整个人信息和原预览凭证', async () => {
  const source = await readFile(new URL('../src/views/ResumeUploadView.vue', import.meta.url), 'utf8')
  const script = parse(source).descriptor.scriptSetup.content
  const ast = parseJavaScript(script, { sourceType: 'module' })
  const upload = ast.program.body.find(node => node.type === 'FunctionDeclaration' && node.id.name === 'upload')
  const previousAdapter = axios.defaults.adapter
  const file = new File(['测试文档'], '报名表.docx')
  const index = { name: '测试同学', studentNo: 'TEST001', phone: '13800000000' }
  let captured
  let destination
  let savedPreview
  const preview = { previewToken: 'next-preview-token' }

  axios.defaults.adapter = async config => {
    captured = config
    return { status: 200, statusText: 'OK', headers: {}, config, data: { code: 0, data: preview } }
  }

  try {
    const context = vm.createContext({
      busy: { value: false },
      formRef: { value: { validate: async () => true } },
      file: { value: file },
      index,
      errorText: { value: '' },
      resumeDraft: { version: 0, preview: { previewToken: 'original-preview-token' } },
      active: true,
      resumeApi,
      setResumePreview: value => { savedPreview = value },
      router: { push: async value => { destination = value } },
      showAlert: message => assert.fail(message)
    })

    await vm.runInContext(`(${script.slice(upload.start, upload.end)})()`, context)

    assert.equal(captured.url, '/api/resume/submit')
    assert.equal(captured.method, 'post')
    assert.deepEqual([...captured.data.keys()].sort(), ['name', 'phone', 'resume', 'studentNo'])
    assert.equal(captured.data.get('resume').name, file.name)
    assert.equal(await captured.data.get('resume').text(), await file.text())
    for (const [key, value] of Object.entries(index)) assert.equal(captured.data.get(key), value)
    assert.equal(captured.headers.get('X-Resume-Preview-Token'), 'original-preview-token')
    assert.equal(savedPreview, preview)
    assert.equal(destination, '/resume/preview')
    assert.equal(context.busy.value, false)
    assert.equal(context.errorText.value, '')
  } finally {
    axios.defaults.adapter = previousAdapter
  }
})
