<template>
  <section class="resume-shell">
    <RouterLink class="resume-back" to="/resume/apply">
      <span>← 返回在线填表</span>
    </RouterLink>

    <!-- Apple 风格分段进度指示条 -->
    <div class="resume-step-indicator">
      <div class="step-node is-active">
        <span class="step-badge">1</span>
        <span>上传 Word</span>
      </div>
      <span class="step-sep">›</span>
      <div class="step-node">
        <span class="step-badge">2</span>
        <span>排版预览</span>
      </div>
      <span class="step-sep">›</span>
      <div class="step-node">
        <span class="step-badge">3</span>
        <span>确认提交</span>
      </div>
    </div>

    <h1>上传已填写的 Word</h1>
    <p class="resume-lead">上传你已填写或贴好照片的 DOCX，先预览，再确认提交。文件最多6页、不超过50 MB。</p>
    <el-alert v-if="errorText" :title="errorText" type="error" :closable="false" show-icon />
    <el-form ref="formRef" :model="index" :rules="rules" label-position="top" :disabled="busy" @submit.prevent="upload">
      <div class="resume-card">
        <p class="resume-note">以下信息用于文件命名和联系，请与 Word 正文保持一致，无需重新填写整份报名表。</p>
        <div class="resume-grid">
          <el-form-item label="姓名" prop="name"><el-input id="upload-name" v-model="index.name" maxlength="30" /></el-form-item>
          <el-form-item label="学号" prop="studentNo"><el-input id="upload-studentNo" v-model="index.studentNo" maxlength="40" /></el-form-item>
          <el-form-item label="联系电话" prop="phone"><el-input id="upload-phone" v-model.trim="index.phone" maxlength="30" placeholder="手机号或带区号的固定电话" /></el-form-item>
        </div>
        <el-form-item label="已填写的 Word 文件">
          <input data-testid="resume-word-input" type="file" accept=".docx" :disabled="busy" @change="selectWord" />
        </el-form-item>
        <p class="resume-note">上传原件会保留正文和照片。若文件含外链照片，请先将照片直接粘贴到 Word 中，再另存为 DOCX。</p>
      </div>
      <div class="resume-actions">
        <el-button type="primary" size="large" native-type="submit" :loading="busy">上传并生成预览</el-button>
        <el-button @click="downloadTemplate">下载空白 Word 模板</el-button>
      </div>
    </el-form>
  </section>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { resumeDraft, setResumePreview } from '../utils/resumeDraft'
import { resumeApi, downloadResumeFile } from '../utils/resumeApi'
import { resumePhonePattern, resumePhoneMessage } from '../utils/resumeValidation'
import '../assets/resume.css'

const router = useRouter()
const index = resumeDraft.uploadIndex
for (const key of ['name', 'studentNo', 'phone']) {
  if (!index[key] || resumeDraft.preview?.sourceType === 'FORM') index[key] = resumeDraft.form[key]
}
const formRef = ref()
const busy = ref(false)
const errorText = ref('')
const file = ref(null)
let active = true
onBeforeUnmount(() => { active = false })

const rules = {
  name: [{ required: true, whitespace: true, message: '请填写姓名', trigger: 'blur' }],
  studentNo: [{ required: true, whitespace: true, message: '请填写学号', trigger: 'blur' }],
  phone: [
    { required: true, whitespace: true, message: '请填写联系电话', trigger: 'blur' },
    { pattern: resumePhonePattern, message: resumePhoneMessage, trigger: 'blur' }
  ]
}

function selectWord(event) {
  file.value = event.target.files?.[0] || null
}

async function upload() {
  if (busy.value || !await formRef.value.validate().catch(() => false)) return
  if (!file.value) {
    errorText.value = '请选择需要上传的 Word 文件'
    return
  }
  errorText.value = ''
  busy.value = true
  const version = ++resumeDraft.version
  try {
    const preview = await resumeApi.upload(file.value, index)
    if (!active || version !== resumeDraft.version) return
    setResumePreview(preview)
    await router.push('/resume/preview')
  } catch (error) {
    errorText.value = error.message
  } finally {
    busy.value = false
  }
}

async function downloadTemplate() {
  try {
    downloadResumeFile(await resumeApi.template())
  } catch (error) {
    errorText.value = error.message
  }
}
</script>
