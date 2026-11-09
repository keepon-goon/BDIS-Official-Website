import test from 'node:test'
import assert from 'node:assert/strict'
import { readFile } from 'node:fs/promises'
import { setImmediate } from 'node:timers/promises'
import vm from 'node:vm'
import { ref, reactive, watch, nextTick } from 'vue'
import { parse } from '@vue/compiler-sfc'
import { parse as parseJavaScript } from '@babel/parser'

const source = await readFile(new URL('../src/views/NewsDetail.vue', import.meta.url), 'utf8')
const script = parse(source).descriptor.scriptSetup.content
const ast = parseJavaScript(script, { sourceType: 'module' })
const executable = ast.program.body.filter(node => node.type !== 'ImportDeclaration')
  .map(node => script.slice(node.start, node.end)).join('\n')

function startDetail(get) {
  const route = reactive({ params: { id: '1' } })
  const props = reactive({ contentType: 'news' })
  const context = vm.createContext({ ref, watch, useRoute: () => route, defineProps: () => props, axios: { get }, console })
  const stop = vm.runInContext(executable, context)
  return { route, props, stop, state: vm.runInContext('({ item, loading })', context) }
}

async function settle() {
  await nextTick()
  await setImmediate()
}

test('详情页根据内容类型和编号读取正确接口', async () => {
  const urls = []
  const detail = startDetail(async url => {
    urls.push(url)
    return { data: { code: 0, data: { title: url } } }
  })
  try {
    await settle()
    assert.equal(detail.state.item.value.title, '/api/news/1')
    detail.route.params.id = '2'
    await settle()
    assert.equal(detail.state.item.value.title, '/api/news/2')
    detail.props.contentType = 'announcement'
    detail.route.params.id = '1'
    await settle()
    assert.equal(detail.state.item.value.title, '/api/announcements/1')
    assert.deepEqual(urls, ['/api/news/1', '/api/news/2', '/api/announcements/1'])
  } finally {
    detail.stop()
  }
})

test('切换详情后忽略较早请求的迟到响应', async () => {
  const pending = []
  const detail = startDetail(url => new Promise(resolve => { pending.push({ url, resolve }) }))
  try {
    detail.props.contentType = 'announcement'
    await nextTick()
    pending[1].resolve({ data: { code: 0, data: { title: '正确公告' } } })
    await settle()
    assert.equal(detail.state.item.value.title, '正确公告')
    pending[0].resolve({ data: { code: 0, data: { title: '过期新闻' } } })
    await settle()
    assert.equal(detail.state.item.value.title, '正确公告')
    assert.equal(detail.state.loading.value, false)
  } finally {
    detail.stop()
  }
})

test('公告详情路由传入公告类型并保留新闻路由', async () => {
  const source = await readFile(new URL('../src/router/index.js', import.meta.url), 'utf8')
  const ast = parseJavaScript(source, { sourceType: 'module' })
  const routes = ast.program.body.find(node => node.type === 'VariableDeclaration' && node.declarations[0].id.name === 'routes')
    .declarations[0].init
  const values = vm.runInNewContext(`(${source.slice(routes.start, routes.end)})`, { HomeView: {}, InfoLayout: {}, UnderConstruction: {} })
  const children = values.find(route => route.path === '/info').children
  assert.equal(children.find(route => route.path === 'announcements/:id').props.contentType, 'announcement')
  assert.ok(children.some(route => route.path === 'news/:id'))
})
