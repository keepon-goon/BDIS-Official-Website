<template>
  <section class="feedback-page">
    <div class="feedback-container">
      <header class="feedback-intro">
        <div>
          <p class="feedback-eyebrow">BDIS · 匿名信箱</p>
          <h1>有什么想说的，写在这里</h1>
          <p class="feedback-lead">欢迎实验室成员，也欢迎正在了解工作室的新朋友和网站访客。<br />关于工作室、网站体验、交流活动或加入建议，都可以在这里留言。</p>
          <span class="feedback-privacy"><el-icon><Lock /></el-icon>匿名投递 · 我们绝不知道您是谁，毕竟我们也没有登录系统</span>
        </div>
      </header>

      <div v-if="sent" class="feedback-success" role="status" tabindex="-1" ref="successPanel">
        <el-icon class="success-icon"><CircleCheck /></el-icon>
        <h2>意见已送达</h2>
        <p>感谢这份认真。工作室管理员会查看并整理这条意见。</p>
        <div class="success-actions">
          <button type="button" class="feedback-submit" @click="sent = false">继续投递</button>
          <router-link to="/" class="feedback-home">返回首页</router-link>
        </div>
      </div>

      <div v-else class="feedback-layout">
        <form class="feedback-form" @submit.prevent="submit">
          <div class="form-heading"><h2>写下这条意见</h2><span>文字或图片，至少提交一项</span></div>
          <fieldset class="feedback-field category-field" :disabled="busy">
            <legend>这条意见关于 <span class="field-optional">可选</span></legend>
            <div class="feedback-categories">
              <button v-for="item in feedbackCategories" :key="item.value" type="button"
                :aria-pressed="category === item.value" :class="{ selected: category === item.value }"
                @click="category = category === item.value ? '' : item.value">{{ item.label }}</button>
            </div>
          </fieldset>

          <div class="feedback-field">
            <label for="feedback-content">想说的话</label>
            <textarea id="feedback-content" ref="contentInput" v-model="content" maxlength="3000" :disabled="busy"
              placeholder="可以聊聊网站使用感受、了解工作室时的疑问，也可以提出活动、设备或其他建议。"
              :aria-invalid="Boolean(error)" :aria-describedby="error ? 'feedback-error' : undefined" />
            <div class="feedback-counter">{{ content.length }} / 3000</div>
          </div>

          <div class="feedback-field">
            <label for="feedback-images">补充图片 <span class="field-optional">可选</span></label>
            <input id="feedback-images" ref="fileInput" class="feedback-file-input" type="file" accept="image/jpeg,image/png" tabindex="-1"
              multiple :disabled="busy" @change="selectFiles" />
            <div class="feedback-dropzone" :class="{ dragging }" @dragover.prevent="dragging = !busy"
              @dragleave.prevent="dragging = false" @drop.prevent="dropFiles">
              <button type="button" :disabled="busy || files.length >= 6" @click="fileInput?.click()">
                <el-icon><Picture /></el-icon><span>{{ files.length ? '继续添加图片' : '点击添加图片，或拖到这里' }}</span>
              </button>
              <p>支持 JPG、PNG，每张不超过 5MB，最多 6 张</p>
            </div>
            <div v-if="files.length" class="feedback-images">
              <div v-for="(item, index) in files" :key="item.url" class="feedback-image">
                <el-image :src="item.url" :alt="`待投递图片 ${index + 1}`" fit="cover"
                  :preview-src-list="files.map(file => file.url)" :initial-index="index" preview-teleported />
                <button type="button" :aria-label="`移除图片 ${index + 1}`" :disabled="busy" @click="removeFile(index)">×</button>
                <span :title="item.file.name">{{ item.file.name }}</span>
              </div>
            </div>
          </div>

          <p v-if="error" id="feedback-error" class="feedback-error" role="alert">{{ error }}</p>
          <div class="feedback-form-footer">
            <p><el-icon><Lock /></el-icon>内容仅供管理员查看，不会在网站公开展示。</p>
            <button class="feedback-submit" type="submit" :disabled="busy">
              {{ busy ? '正在投递…' : '投递意见' }}<el-icon v-if="!busy"><Position /></el-icon>
            </button>
          </div>
        </form>

        <aside class="feedback-guide">
          <span class="guide-marker">一封意见，一点改变</span>
          <h2>从这些地方开始</h2>
          <div v-for="item in ideas" :key="item.title" class="guide-item">
            <el-icon><component :is="item.icon" /></el-icon>
            <div><h3>{{ item.title }}</h3><p>{{ item.text }}</p></div>
          </div>
          <div class="guide-note"><h3>投递小提示</h3><p>可描述具体问题、使用场景或建议。添加图片时，可先遮挡与意见无关的个人信息。</p><p>无需填写姓名、学号或联系方式。</p></div>
        </aside>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, nextTick, onBeforeUnmount } from 'vue'
import { Lock, Picture, Position, CircleCheck, Monitor, OfficeBuilding, Calendar } from '@element-plus/icons-vue'
import { feedbackApi, feedbackCategories } from '@/utils/feedbackApi'

const content = ref('')
const category = ref('')
const files = ref([])
const busy = ref(false)
const sent = ref(false)
const error = ref('')
const dragging = ref(false)
const fileInput = ref(null)
const contentInput = ref(null)
const successPanel = ref(null)
const ideas = [
  { title: '网站与初次了解', text: '页面使用、信息介绍、加入流程，或初次了解工作室时遇到的问题。', icon: Monitor },
  { title: '工作室与日常体验', text: '设备、环境、资源和项目交流，欢迎结合实际体验提出建议。', icon: OfficeBuilding },
  { title: '活动与交流想法', text: '想了解的技术、希望开展的活动，或其他值得分享的新想法。', icon: Calendar }
]

function addFiles(selected) {
  if (busy.value) return
  const incoming = [...selected]
  dragging.value = false
  if (files.value.length + incoming.length > 6) { error.value = '每次最多添加6张图片，请减少图片后重试。'; return }
  for (const file of incoming) {
    if (!['image/jpeg', 'image/png'].includes(file.type)) { error.value = '图片仅支持JPG或PNG格式。'; return }
    if (file.size === 0 || file.size > 5 * 1024 * 1024) { error.value = '图片不能为空，每张不能超过5MB。'; return }
  }
  files.value.push(...incoming.map(file => ({ file, url: URL.createObjectURL(file) })))
  error.value = ''
}

function selectFiles(event) { addFiles(event.target.files); event.target.value = '' }
function dropFiles(event) { addFiles(event.dataTransfer.files) }
function removeFile(index) { URL.revokeObjectURL(files.value[index].url); files.value.splice(index, 1); error.value = '' }
function clearFiles() { files.value.forEach(item => URL.revokeObjectURL(item.url)); files.value = [] }

async function submit() {
  if (busy.value) return
  if (!content.value.trim() && !files.value.length) {
    error.value = '请写下想说的话，或添加至少一张图片。'
    contentInput.value?.focus()
    return
  }
  error.value = ''
  busy.value = true
  const data = new FormData()
  data.append('content', content.value.trim())
  data.append('category', category.value || 'other')
  files.value.forEach(item => data.append('images', item.file))
  try {
    await feedbackApi.submit(data)
    sent.value = true
    content.value = ''
    category.value = ''
    clearFiles()
    await nextTick()
    successPanel.value?.focus({ preventScroll: true })
    successPanel.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  } catch (failure) { error.value = failure.message }
  finally { busy.value = false }
}

onBeforeUnmount(clearFiles)
</script>

<style scoped>
.feedback-page { color: #23415a; --el-color-primary: #0c3d70; background: #f8fbfe; }
.feedback-container { max-width: 1120px; margin: 0 auto; padding: 48px 32px 72px; }
.feedback-intro { margin-bottom: 36px; }
.feedback-eyebrow { color: #557b9f; font-size: 13px; letter-spacing: 2px; margin: 0 0 12px; }
.feedback-intro h1 { color: #0c3d70; font-size: clamp(28px, 3.5vw, 42px); font-weight: 650; line-height: 1.35; margin: 0 0 16px; }
.feedback-lead { color: #627a91; font-size: 15px; line-height: 1.9; margin: 0 0 18px; }
.feedback-privacy { display: inline-flex; align-items: center; gap: 7px; font-size: 13px; color: #426b93; }
.feedback-layout { display: grid; grid-template-columns: minmax(0, 1fr) 270px; gap: 24px; align-items: start; }
.feedback-form { min-width: 0; background: white; border: 1px solid #dce7f2; border-radius: 16px; padding: 28px; }
.form-heading { margin-bottom: 26px; }
.form-heading h2 { font-size: 20px; color: #0c3d70; margin: 0 0 7px; }
.form-heading > span { color: #7a8c9e; font-size: 12px; }
.feedback-field { margin: 0 0 24px; }
.category-field { padding: 0; border: 0; min-width: 0; }
.feedback-field label, .feedback-field legend { display: block; padding: 0; margin-bottom: 10px; font-size: 14px; font-weight: 600; color: #23415a; }
.field-optional { margin-left: 6px; font-weight: 400; font-size: 12px; color: #8698a9; }
.feedback-categories { display: flex; flex-wrap: wrap; gap: 8px; }
.feedback-categories button { min-height: 40px; padding: 8px 14px; border-radius: 7px; border: 1px solid #dce7f2; color: #526d86; background: white; font-size: 13px; }
.feedback-categories button:hover, .feedback-categories button.selected { color: #0c3d70; background: #edf4fb; border-color: #8eafcf; }
#feedback-content { width: 100%; box-sizing: border-box; min-height: 190px; resize: vertical; border: 1px solid #dce7f2; background: #fcfdff; border-radius: 9px; padding: 14px; color: #23415a; font: inherit; font-size: 15px; line-height: 1.8; }
#feedback-content::placeholder { color: #95a5b4; }
#feedback-content:focus { outline: 2px solid #a7c5e3; outline-offset: 1px; border-color: #0c3d70; }
.feedback-counter { text-align: right; font-size: 12px; color: #899bac; margin-top: 4px; }
.feedback-file-input { position: absolute; width: 1px; height: 1px; opacity: 0; overflow: hidden; }
.feedback-dropzone { text-align: center; border: 1px dashed #bdd0e3; border-radius: 9px; background: #f8fbff; padding: 20px 10px; }
.feedback-dropzone.dragging { border-color: #0c3d70; background: #eaf2fb; }
.feedback-dropzone button { display: inline-flex; align-items: center; justify-content: center; gap: 9px; min-height: 44px; padding: 6px 8px; font-size: 14px; background: transparent; color: #0c3d70; }
.feedback-dropzone .el-icon { font-size: 23px; }
.feedback-dropzone p { margin: 3px 0 0; font-size: 12px; color: #7c91a5; }
.feedback-images { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 12px; margin-top: 14px; }
.feedback-image { position: relative; min-width: 0; }
.feedback-image .el-image { width: 100%; height: 100px; border: 1px solid #dce7f2; border-radius: 7px; box-sizing: border-box; }
.feedback-image > button { position: absolute; top: -7px; right: -7px; display: grid; place-items: center; padding: 0; width: 30px; height: 30px; border-radius: 50%; background: #0c3d70; color: white; border: 2px solid white; font-size: 20px; }
.feedback-image > span { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 11px; color: #7c91a5; }
.feedback-error { font-size: 14px; line-height: 1.6; color: #ba303c; padding: 10px 12px; background: #fff4f5; border-radius: 6px; }
.feedback-form-footer { display: flex; flex-direction: column; align-items: flex-start; gap: 14px; padding-top: 18px; border-top: 1px solid #e9eff6; }
.feedback-form-footer p { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #71879b; margin: 0; }
.feedback-submit { display: inline-flex; align-items: center; justify-content: center; gap: 12px; min-height: 46px; padding: 10px 26px; border: 1px solid #0c3d70; border-radius: 8px; background: #0c3d70; color: white; font-size: 15px; }
.feedback-submit:hover:not(:disabled) { background: #164f86; }
.feedback-page button:disabled { opacity: .55; cursor: not-allowed; }
.feedback-page button:focus-visible, .feedback-page a:focus-visible { outline: 2px solid #6d9ccc; outline-offset: 3px; }
.feedback-guide { padding: 24px 20px; background: #edf4fb; border-radius: 16px; }
.guide-marker { font-size: 12px; color: #6b8dad; }
.feedback-guide h2 { font-size: 18px; color: #0c3d70; margin: 10px 0 22px; }
.guide-item { display: flex; gap: 12px; margin-bottom: 22px; }
.guide-item > .el-icon { flex: 0 0 auto; font-size: 20px; margin-top: 2px; color: #557fa8; }
.feedback-guide h3 { font-size: 14px; font-weight: 600; color: #365d84; margin: 0 0 7px; }
.feedback-guide p { font-size: 13px; line-height: 1.8; color: #6c8399; margin: 0; }
.guide-note { padding-top: 20px; border-top: 1px solid #d3e1ee; }
.guide-note p + p { margin-top: 12px; }
.feedback-success { text-align: center; border: 1px solid #dce7f2; border-radius: 16px; padding: 64px 24px; background: white; outline: none; }
.success-icon { font-size: 56px; color: #0c3d70; }
.feedback-success h2 { font-size: 26px; color: #0c3d70; margin: 16px 0 12px; }
.feedback-success p { color: #6c8399; font-size: 15px; line-height: 1.8; }
.success-actions { display: flex; align-items: center; justify-content: center; flex-wrap: wrap; gap: 20px; margin-top: 28px; }
.feedback-home { color: #0c3d70; font-size: 14px; }
@media (max-width: 900px) {
  .feedback-layout { grid-template-columns: minmax(0, 1fr); }
  .feedback-guide { padding: 24px; }
}
@media (max-width: 560px) {
  .feedback-container { padding: 30px 16px 44px; }
  .feedback-intro { margin-bottom: 26px; }
  .feedback-lead { font-size: 14px; }
  .feedback-form { padding: 22px 16px; border-radius: 12px; }
  .feedback-categories { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .feedback-categories button { min-height: 44px; }
  #feedback-content { font-size: 16px; }
  .feedback-form-footer .feedback-submit { width: 100%; }
  .feedback-form-footer p { align-items: flex-start; line-height: 1.6; }
  .feedback-form-footer p .el-icon { flex: 0 0 auto; margin-top: 3px; }
  .feedback-images { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .feedback-dropzone p { font-size: 11px; }
}
</style>
