<template>
  <section class="resume-shell">
    <template v-if="resumeDraft.submission">
      <el-result icon="success" title="报名已保存" :sub-title="resumeDraft.submission.message">
        <template #extra>
          <p>提交编号：{{ resumeDraft.submission.submissionId }}</p>
          <p class="resume-note">无需重复提交。邮件尚未成功时，可由管理员后续补发。</p>
          <div class="resume-actions">
            <el-button @click="router.push('/info/join')">返回加入我们</el-button>
            <el-button type="primary" @click="newApplication">填写新的报名表</el-button>
          </div>
        </template>
      </el-result>
    </template>
    <template v-else-if="preview">
      <h1>核对报名表预览</h1>
      <p class="resume-lead">请核对两页内容与分页效果。你可以下载当前 Word 自行贴照，也可以上传证件照合成后再次预览。</p>
      <p class="resume-filename">{{ preview.wordFilename }}</p>
      <el-tag>{{ preview.sourceType === 'UPLOAD' ? '上传的 Word 原件' : preview.hasPhoto ? '已合成照片' : '照片留白' }}</el-tag>
      <el-alert v-if="errorText" :title="errorText" type="error" :closable="false" show-icon />
      <div class="resume-card resume-photo-panel" v-if="preview.sourceType === 'FORM'">
        <h2>证件照（可选）</h2>
        <p class="resume-note">请上传竖版证件照，建议宽高比 <strong>2:3</strong>、<strong>600×900 像素</strong>，支持 JPG/JPEG、PNG，单张不超过 <strong>5 MB</strong>、不超过1200万像素。比例明显不符时会拒绝；合格照片等比缩放、居中留白，不自动裁切。</p>
        <div class="resume-actions">
          <el-button type="primary" :loading="operation === 'photo'" :disabled="busy || loadingPdf" @click="photoInput.click()">
            {{ preview.hasPhoto ? '更换照片并重新预览' : '上传照片合成预览' }}
          </el-button>
          <span class="resume-note">不上传照片也可以确认提交。</span>
        </div>
        <input ref="photoInput" data-testid="resume-photo-input" class="resume-file-input" type="file" accept="image/jpeg,image/png" @change="changePhoto" />
      </div>
      <p v-else class="resume-note">此预览保留上传 Word 的原内容。需要更换照片或修改文字时，请在本地编辑后重新上传。</p>
      <div class="resume-actions">
        <el-button :loading="operation === 'download'" :disabled="busy || loadingPdf" @click="downloadWord">下载当前 Word</el-button>
        <el-button :disabled="busy || loadingPdf" @click="router.push('/resume/upload')">上传已编辑 Word</el-button>
        <el-button v-if="preview.sourceType === 'FORM'" :disabled="busy || loadingPdf" @click="router.push('/resume/apply')">返回修改表单</el-button>
        <el-button :disabled="busy || loadingPdf" @click="loadPdf">重新加载预览</el-button>
      </div>
      <p class="resume-note">下载后在本地修改不会自动同步。自行贴照后，请上传修改后的 Word，再预览并确认提交。</p>
      <div v-loading="loadingPdf || operation === 'photo'" element-loading-text="正在生成或读取预览，请稍候">
        <ResumePdfPreview :url="pdfUrl" :filename="preview.pdfFilename" @ready="renderedUrl = $event" @error="renderFailed" />
      </div>
      <div class="resume-actions">
        <el-button type="primary" size="large" :loading="operation === 'confirm'" :disabled="!canConfirm" @click="confirm">确认当前版本并提交</el-button>
        <span class="resume-note">预览有效期至 {{ new Date(preview.expiresAt).toLocaleString() }}</span>
      </div>
    </template>
    <div v-else class="resume-empty">
      <h1>请先生成报名表预览</h1>
      <p class="resume-note">页面刷新或预览凭证丢失后，需要重新填写生成，或上传已保存的 Word。</p>
      <div class="resume-actions">
        <el-button type="primary" @click="router.push('/resume/apply')">在线填写</el-button>
        <el-button @click="router.push('/resume/upload')">上传已填写 Word</el-button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import ResumePdfPreview from '../components/resume/ResumePdfPreview.vue'
import { resumeDraft, setResumePreview, resetResumeDraft } from '../utils/resumeDraft'
import { resumeApi, downloadResumeFile } from '../utils/resumeApi'
import '../assets/resume.css'

const router = useRouter()
const preview = computed(() => resumeDraft.preview)
const photoInput = ref()
const pdfUrl = ref('')
const pdfToken = ref('')
const renderedUrl = ref('')
const loadingPdf = ref(false)
const operation = ref('')
const errorText = ref('')
const busy = computed(() => Boolean(operation.value))
const canConfirm = computed(() => preview.value && !busy.value && !loadingPdf.value && pdfUrl.value
  && pdfToken.value === preview.value.previewToken
  && renderedUrl.value === pdfUrl.value
  && (preview.value.sourceType !== 'FORM' || preview.value.formVersion === resumeDraft.version))
let active = true

function clearPdf() {
  if (pdfUrl.value) URL.revokeObjectURL(pdfUrl.value)
  pdfUrl.value = ''
  pdfToken.value = ''
  renderedUrl.value = ''
}

function renderFailed(message) { renderedUrl.value = ''; errorText.value = message }
onBeforeUnmount(() => { active = false; clearPdf() })
onMounted(() => { if (preview.value) loadPdf() })

async function loadPdf() {
  if (!preview.value) return
  const token = preview.value.previewToken
  clearPdf()
  loadingPdf.value = true
  errorText.value = ''
  try {
    const { blob } = await resumeApi.previewPdf(token)
    if (!active || preview.value?.previewToken !== token) return
    pdfUrl.value = URL.createObjectURL(blob)
    pdfToken.value = token
  } catch (error) { errorText.value = error.message }
  finally { loadingPdf.value = false }
}

async function validatePhoto(file) {
  if (file.size > 5 * 1024 * 1024) throw new Error('单张照片不能超过5MB')
  if (file.type && !['image/jpeg', 'image/png'].includes(file.type)) throw new Error('照片仅支持JPG/JPEG或PNG')
  const url = URL.createObjectURL(file)
  try {
    const image = new Image()
    await new Promise((resolve, reject) => {
      image.onload = resolve
      image.onerror = () => reject(new Error('照片无法读取，请更换图片'))
      image.src = url
    })
    if (image.naturalWidth * image.naturalHeight > 12000000) throw new Error('照片不能超过1200万像素，请缩小后上传')
    if (Math.abs((image.naturalWidth / image.naturalHeight) / (2 / 3) - 1) > 0.1500001)
      throw new Error('照片比例不符合要求，请使用接近2:3的竖版照片，建议600×900像素')
  } finally { URL.revokeObjectURL(url) }
}

async function changePhoto(event) {
  const file = event.target.files?.[0]
  if (!file || busy.value || !preview.value) return
  operation.value = 'photo'
  errorText.value = ''
  try {
    await validatePhoto(file)
    const result = await resumeApi.photo(preview.value.previewToken, file)
    if (!active) return
    setResumePreview(result)
    await loadPdf()
  } catch (error) { errorText.value = error.message }
  finally { operation.value = ''; event.target.value = '' }
}

async function downloadWord() {
  operation.value = 'download'
  try { downloadResumeFile(await resumeApi.previewWord(preview.value.previewToken)) }
  catch (error) { errorText.value = error.message }
  finally { operation.value = '' }
}

async function confirm() {
  if (!canConfirm.value) return
  operation.value = 'confirm'
  errorText.value = ''
  try {
    const result = await resumeApi.confirm(preview.value.previewToken)
    if (!active) return
    resumeDraft.submission = result
    resumeDraft.preview = null
    clearPdf()
  } catch (error) { errorText.value = error.message }
  finally { operation.value = '' }
}

function newApplication() { resetResumeDraft(); router.push('/resume/apply') }
</script>
