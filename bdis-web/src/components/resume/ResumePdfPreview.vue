<template>
  <div ref="container" v-show="url" class="resume-pdf">
    <div class="resume-actions">
      <a :href="url" target="_blank" rel="noopener">新窗口查看 PDF</a>
      <a :href="url" :download="filename">下载 PDF</a>
    </div>
    <p class="resume-note">{{ loading ? '正在绘制PDF预览…' : `共${pageCount}页，请滚动查看并核对全部内容。` }}</p>
    <el-alert v-if="errorText" :title="errorText" type="error" :closable="false" />
    <div class="resume-pdf-pages" aria-label="报名表 PDF 预览" :aria-busy="loading">
      <div v-for="index in pageCount" :key="`${url}-${index}`" class="resume-pdf-page">
        <canvas :ref="element => canvases[index - 1] = element" :aria-label="`报名表第${index}页`" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch, onMounted, onBeforeUnmount } from 'vue'
import { getDocument, GlobalWorkerOptions } from 'pdfjs-dist'
import workerUrl from 'pdfjs-dist/build/pdf.worker.min.mjs?url'

GlobalWorkerOptions.workerSrc = workerUrl
const props = defineProps({ url: { type: String, default: '' }, filename: { type: String, default: '报名表.pdf' } })
const emit = defineEmits(['ready', 'error'])
const container = ref()
const pageCount = ref(0)
const loading = ref(false)
const errorText = ref('')
const canvases = []
let loadingTask = null
let renderTasks = []
let generation = 0
let observer
let resizeTimer
let lastWidth = 0

function cancel() {
  renderTasks.forEach(task => task.cancel())
  renderTasks = []
  if (loadingTask) loadingTask.destroy().catch(() => {})
  loadingTask = null
}

async function render() {
  const version = ++generation
  cancel()
  pageCount.value = 0
  errorText.value = ''
  const url = props.url
  if (!url) return
  loading.value = true
  try {
    const task = getDocument({ url })
    loadingTask = task
    const pdf = await task.promise
    if (version !== generation) return
    if (pdf.numPages < 1 || pdf.numPages > 2) throw new Error('预览页数异常')
    pageCount.value = pdf.numPages
    await nextTick()
    const width = Math.max(200, Math.min(850, container.value.clientWidth - 36))
    const pixelRatio = Math.min(2, window.devicePixelRatio || 1)
    for (let index = 1; index <= pdf.numPages; index++) {
      const page = await pdf.getPage(index)
      if (version !== generation) return
      const base = page.getViewport({ scale: 1 })
      const scale = width / base.width
      const viewport = page.getViewport({ scale: scale * pixelRatio })
      const canvas = canvases[index - 1]
      if (!canvas) return
      canvas.width = Math.floor(viewport.width)
      canvas.height = Math.floor(viewport.height)
      canvas.style.width = Math.floor(width) + 'px'
      canvas.style.height = Math.floor(viewport.height / pixelRatio) + 'px'
      const rendering = page.render({ canvas, viewport })
      renderTasks.push(rendering)
      await rendering.promise
    }
    if (version === generation) emit('ready', url)
  } catch (error) {
    if (version !== generation) return
    if (import.meta.env.DEV) console.error('PDF预览绘制失败', error)
    errorText.value = 'PDF预览暂无法显示，请重新加载，或下载文件后核对。'
    emit('error', errorText.value)
  } finally {
    if (version === generation) loading.value = false
  }
}

watch(() => props.url, render, { flush: 'post' })
onMounted(() => {
  observer = new ResizeObserver(entries => {
    const width = Math.round(entries[0].contentRect.width)
    if (width <= 0 || Math.abs(width - lastWidth) < 2) return
    lastWidth = width
    clearTimeout(resizeTimer)
    resizeTimer = setTimeout(() => { if (props.url) render() }, 150)
  })
  observer.observe(container.value)
  if (props.url) render()
})
onBeforeUnmount(() => {
  generation++
  clearTimeout(resizeTimer)
  observer?.disconnect()
  cancel()
})
</script>

<style scoped>
.resume-pdf-pages { max-height: 900px; overflow: auto; padding: 16px; border: 1px solid #e0e6ee; border-radius: 8px; background: #edf0f4; }
.resume-pdf-page { margin: 0 auto 16px; width: fit-content; max-width: 100%; background: white; box-shadow: 0 1px 5px #0002; }
.resume-pdf-page:last-child { margin-bottom: 0; }
.resume-pdf-page canvas { display: block; max-width: 100%; }
.resume-pdf a { color: #0c3d70; font-size: 14px; }
@media (max-width: 700px) { .resume-pdf-pages { max-height: 620px; padding: 10px; } }
</style>
