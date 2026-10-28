<template>
  <section class="public-qa" aria-labelledby="feedback-qa-title" :aria-busy="loading">
    <h2 id="feedback-qa-title">实验室朋友的 Q&A</h2>
    <p class="qa-intro">一些已经收到回答的问题，也许正好解开你的疑惑。</p>
    <p v-if="error" class="qa-error" role="alert">{{ error }} <button type="button" @click="load">重新加载</button></p>
    <p v-else-if="loading" role="status">正在翻阅大家的问答…</p>
    <p v-else-if="!records.length" class="qa-empty">还没有公开的问答，期待收到你的第一个问题。</p>
    <article v-for="item in records" :key="item.id" class="qa-card">
      <div class="qa-meta"><span>{{ item.displayName }}</span><time>{{ formatTime(item.repliedAt) }}</time></div>
      <h3>Q · 想问的话</h3><p>{{ item.question || '这是一条图片提问，附件仅供管理员查看。' }}</p>
      <h3>A · 实验室朋友的回答</h3><p>{{ item.answer }}</p>
    </article>
    <nav v-if="total > pageSize" class="qa-pagination" aria-label="公开问答分页">
      <button type="button" :disabled="loading || page === 1" @click="changePage(-1)">上一页</button>
      <span>{{ page }} / {{ Math.ceil(total / pageSize) }}</span>
      <button type="button" :disabled="loading || page * pageSize >= total" @click="changePage(1)">下一页</button>
    </nav>
  </section>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { feedbackApi } from '@/utils/feedbackApi'

const records = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 6
const loading = ref(false)
const error = ref('')
const formatTime = value => value ? value.replace('T', ' ').slice(0, 16) : ''
let request

async function load() {
  request?.abort()
  const controller = new AbortController()
  request = controller
  loading.value = true
  error.value = ''
  records.value = []
  try {
    const result = await feedbackApi.qa({ page: page.value, pageSize }, controller.signal)
    if (controller.signal.aborted) return
    records.value = result.list
    total.value = result.total
  } catch (failure) { if (!controller.signal.aborted) error.value = failure.message }
  finally { if (request === controller) loading.value = false }
}

function changePage(step) { page.value += step; load() }
onMounted(load)
onBeforeUnmount(() => request?.abort())
</script>

<style scoped>
.public-qa { margin-bottom: 28px; padding: 26px; border: 1px solid #dce7f2; border-radius: 16px; background: #fff; }
.public-qa h2 { margin: 0 0 10px; color: #0c3d70; font-size: 22px; }
.qa-intro, .qa-empty { color: #6d8296; font-size: 14px; line-height: 1.8; }
.qa-card { padding: 22px 0; border-top: 1px solid #e9eff6; }
.qa-meta { display: flex; justify-content: space-between; flex-wrap: wrap; gap: 8px; color: #6d8296; font-size: 12px; }
.qa-card h3 { margin: 18px 0 8px; color: #0c3d70; font-size: 14px; }
.qa-card p { margin: 0; white-space: pre-wrap; overflow-wrap: anywhere; color: #34516e; font-size: 14px; line-height: 1.9; }
.qa-pagination { display: flex; justify-content: center; align-items: center; gap: 16px; font-size: 13px; }
.public-qa button { padding: 8px 12px; border: 1px solid #bdd0e3; border-radius: 7px; background: #edf4fb; color: #0c3d70; }
.public-qa button:disabled { opacity: .55; cursor: not-allowed; }
.qa-error { color: #ba303c; font-size: 14px; }
@media (max-width: 560px) { .public-qa { padding: 22px 16px; } }
</style>
