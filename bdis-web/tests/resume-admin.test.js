import test from 'node:test'
import assert from 'node:assert/strict'
import { readFile } from 'node:fs/promises'
import vm from 'node:vm'
import { ref, reactive } from 'vue'
import { parse } from '@vue/compiler-sfc'
import { parse as parseJavaScript } from '@babel/parser'
import axios from 'axios'
import { resumeApi } from '../src/utils/resumeApi.js'

const source = await readFile(new URL('../src/views/admin/ResumeManage.vue', import.meta.url), 'utf8')
const script = parse(source).descriptor.scriptSetup.content
const executable = parseJavaScript(script, { sourceType: 'module' }).program.body
  .filter(node => node.type !== 'ImportDeclaration').map(node => script.slice(node.start, node.end)).join('\n')

function setup(overrides = {}) {
  const messages = []
  const api = {
    list: async () => ({ list: [], total: 0 }),
    archive: async () => {},
    detail: async id => ({ id }),
    mailLogs: async () => ({ list: [], total: 0 }),
    ...overrides
  }
  const context = vm.createContext({
    ref, reactive, onMounted: () => {}, onBeforeUnmount: () => {}, resumeApi: api,
    ElMessage: { success: value => messages.push(value), error: value => messages.push(value) }
  })
  vm.runInContext(executable, context)
  const state = vm.runInContext('({ filters, page, rows, total, loading, archiving, detail, detailVisible, mailLogs, mailLogsLoading, mailLogError, mailLogPage, mailLogTotal, loadList, search, toggleArchive, showDetail, loadMailLogs })', context)
  return { state, messages }
}

test('默认查询未归档记录，改变筛选时回到第一页', async () => {
  const calls = []
  const { state } = setup({ list: async params => { calls.push(params); return { list: [], total: 0 } } })
  await state.loadList()
  assert.equal(calls[0].archiveStatus, 'UNARCHIVED')
  state.page.value = 4
  state.filters.archiveStatus = 'ARCHIVED'
  await state.search()
  assert.equal(calls[1].archiveStatus, 'ARCHIVED')
  assert.equal(calls[1].page, 1)
})

test('归档后当前页清空时回到有效页并刷新列表', async () => {
  const calls = []
  const updates = []
  const { state, messages } = setup({
    archive: async (id, archived) => { updates.push([id, archived]) },
    list: async params => {
      calls.push(params.page)
      return { total: 10, list: params.page === 2 ? [] : Array.from({ length: 10 }, (_, i) => ({ id: String(i + 1) })) }
    }
  })
  state.page.value = 2
  await state.toggleArchive({ id: '11', archived: false })
  assert.deepEqual(updates, [['11', true]])
  assert.deepEqual(calls, [2, 1])
  assert.equal(state.page.value, 1)
  assert.equal(state.rows.value.length, 10)
  assert.equal(state.archiving.value, '')
  assert.ok(messages.includes('归档成功'))
})

test('已归档记录可恢复，失败时保留当前记录并提示原因', async () => {
  let value
  const restored = setup({ archive: async (_id, archived) => { value = archived } })
  restored.state.filters.archiveStatus = 'ARCHIVED'
  await restored.state.toggleArchive({ id: '1', archived: true })
  assert.equal(value, false)
  assert.ok(restored.messages.includes('已恢复为未归档'))
  const failed = setup({ archive: async () => { throw new Error('状态保存失败') } })
  failed.state.rows.value = [{ id: '1', archived: false }]
  await failed.state.toggleArchive(failed.state.rows.value[0])
  assert.equal(failed.state.rows.value.length, 1)
  assert.equal(failed.state.rows.value[0].archived, false)
  assert.equal(failed.state.archiving.value, '')
  assert.ok(failed.messages.includes('状态保存失败'))
})

test('归档请求进行中时阻止重复点击', async () => {
  let finish
  let calls = 0
  const { state } = setup({ archive: () => { calls++; return new Promise(resolve => { finish = resolve }) } })
  const first = state.toggleArchive({ id: '1', archived: false })
  await state.toggleArchive({ id: '1', archived: false })
  assert.equal(calls, 1)
  assert.equal(state.archiving.value, '1')
  finish()
  await first
  assert.equal(state.archiving.value, '')
})

test('快速切换归档筛选时忽略旧查询的迟到响应', async () => {
  const pending = []
  const { state } = setup({ list: () => new Promise(resolve => { pending.push(resolve) }) })
  const old = state.loadList()
  state.filters.archiveStatus = 'ARCHIVED'
  const current = state.search()
  pending[1]({ list: [{ id: 'archived' }], total: 1 })
  await current
  pending[0]({ list: [{ id: 'old' }], total: 1 })
  await old
  assert.equal(state.rows.value[0].id, 'archived')
  assert.equal(state.loading.value, false)
})

test('详情展示独立发送历史，分页失败时保留详情并给出日志错误', async () => {
  const calls = []
  const { state } = setup({ mailLogs: async (id, params) => {
    calls.push([id, params.page])
    if (params.page === 2) throw new Error('日志读取失败')
    return { list: [{ id: '2', status: 'ACCEPTED' }, { id: '1', status: 'FAILED' }], total: 12 }
  } })
  await state.showDetail({ id: '10' })
  assert.equal(state.detail.value.id, '10')
  assert.equal(state.detailVisible.value, true)
  assert.deepEqual(Array.from(state.mailLogs.value, row => row.status), ['ACCEPTED', 'FAILED'])
  assert.equal(state.mailLogTotal.value, 12)
  state.mailLogPage.value = 2
  await state.loadMailLogs()
  assert.deepEqual(calls, [['10', 1], ['10', 2]])
  assert.equal(state.mailLogError.value, '日志读取失败')
  assert.equal(state.mailLogsLoading.value, false)
  assert.equal(state.detail.value.id, '10')
  assert.equal(state.mailLogs.value.length, 0)
})

test('归档及日志请求使用正确的方法、路径和字段', async () => {
  const previous = axios.defaults.adapter
  const calls = []
  axios.defaults.adapter = async config => {
    calls.push(config)
    return { status: 200, statusText: 'OK', headers: {}, config, data: { code: 0, data: {} } }
  }
  try {
    await resumeApi.archive('10', true)
    await resumeApi.archive('10', false)
    await resumeApi.mailLogs('10', { page: 2, pageSize: 10 })
    assert.equal(calls[0].method, 'put')
    assert.equal(calls[0].url, '/api/admin/resumes/10/archive')
    assert.equal(JSON.parse(calls[0].data).archived, true)
    assert.equal(JSON.parse(calls[1].data).archived, false)
    assert.equal(calls[2].method, 'get')
    assert.equal(calls[2].url, '/api/admin/resumes/10/mail-logs')
    assert.equal(calls[2].params.page, 2)
  } finally { axios.defaults.adapter = previous }
})
