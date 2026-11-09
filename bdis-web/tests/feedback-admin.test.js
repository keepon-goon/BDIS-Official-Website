import test from 'node:test'
import assert from 'node:assert/strict'
import { readFile } from 'node:fs/promises'
import vm from 'node:vm'
import { ref, computed } from 'vue'
import { parse } from '@vue/compiler-sfc'
import { parse as parseJavaScript } from '@babel/parser'
import axios from 'axios'
import { feedbackApi, feedbackCategories, feedbackStatuses } from '../src/utils/feedbackApi.js'

const source = await readFile(new URL('../src/views/admin/FeedbackManage.vue', import.meta.url), 'utf8')
const script = parse(source).descriptor.scriptSetup.content
const executable = parseJavaScript(script, { sourceType: 'module' }).program.body
  .filter(node => node.type !== 'ImportDeclaration').map(node => script.slice(node.start, node.end)).join('\n')
const answered = { id: 'question-1', replyRequested: true, publicReply: true, repliedAt: '2026-11-09T20:00:00', replyContent: '已保存的回复', published: true }

function setup(publication) {
  const messages = []
  const context = vm.createContext({
    ref, computed, onMounted: () => {}, onBeforeUnmount: () => {},
    feedbackApi: { publication }, feedbackCategories, feedbackStatuses,
    ElMessage: { success: value => messages.push(value), error: value => messages.push(value) }
  })
  vm.runInContext(executable, context)
  const state = vm.runInContext('({ records, page, status, category, publicationSaving, canChangePublication, togglePublication })', context)
  state.records.value = [{ ...answered }]
  return { state, messages }
}

test('只为同意公开且已有保存回复的条目提供展示操作', () => {
  const { state } = setup()
  assert.equal(state.canChangePublication(answered), true)
  assert.equal(state.canChangePublication({ ...answered, published: false }), true)
  for (const fields of [{ replyRequested: false }, { publicReply: false }, { replyContent: '  ' }, { replyContent: null }, { repliedAt: null }]) {
    assert.equal(state.canChangePublication({ ...answered, ...fields }), false)
  }
})

test('撤回和重新展示后更新列表，同时保留当前分页及筛选', async () => {
  const calls = []
  const { state, messages } = setup(async (id, published) => {
    calls.push([id, published])
    return { ...answered, published }
  })
  state.page.value = 3
  state.status.value = 'PROCESSING'
  state.category.value = 'website'
  await state.togglePublication(state.records.value[0])
  assert.equal(state.records.value[0].published, false)
  await state.togglePublication(state.records.value[0])
  assert.equal(state.records.value[0].published, true)
  assert.deepEqual(calls, [['question-1', false], ['question-1', true]])
  assert.equal(state.page.value, 3)
  assert.equal(state.status.value, 'PROCESSING')
  assert.equal(state.category.value, 'website')
  assert.equal(state.records.value[0].replyContent, answered.replyContent)
  assert.ok(messages.includes('已撤回官网 Q&A 展示'))
  assert.ok(messages.includes('已重新展示到官网 Q&A'))
})

test('请求期间阻止重复切换，失败时保留原展示状态', async () => {
  let reject
  let calls = 0
  const { state, messages } = setup(() => {
    calls++
    return new Promise((resolve, fail) => { reject = fail })
  })
  const pending = state.togglePublication(state.records.value[0])
  await state.togglePublication(state.records.value[0])
  assert.equal(calls, 1)
  assert.equal(state.publicationSaving.value, answered.id)
  reject(new Error('展示状态保存失败'))
  await pending
  assert.equal(state.records.value[0].published, true)
  assert.equal(state.publicationSaving.value, '')
  assert.ok(messages.includes('展示状态保存失败'))
  await state.togglePublication({ ...answered, publicReply: false })
  assert.equal(calls, 1)
})

test('展示请求只提交展示标记，避免覆盖已有回复', async () => {
  const previous = axios.defaults.adapter
  const calls = []
  axios.defaults.adapter = async config => {
    calls.push(config)
    return { status: 200, statusText: 'OK', headers: {}, config, data: { code: 0, data: { ...answered, published: false } } }
  }
  try {
    await feedbackApi.publication('question/1', false)
    assert.equal(calls[0].method, 'put')
    assert.equal(calls[0].url, '/api/admin/feedback/question%2F1/publication')
    assert.deepEqual(JSON.parse(calls[0].data), { published: false })
  } finally { axios.defaults.adapter = previous }
})
