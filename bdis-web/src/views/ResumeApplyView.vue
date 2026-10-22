<template>
  <section class="resume-shell">
    <RouterLink class="resume-back" to="/info/join">返回加入我们</RouterLink>
    <h1>在线填写招新报名表</h1>
    <p class="resume-lead">填写后生成两页报名表，下一步可核对 PDF、下载 Word，或上传证件照。照片可以留空。</p>
    <el-alert v-if="errorText" :title="errorText" type="error" :closable="false" show-icon />
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top" :disabled="busy" @submit.prevent="generate">
      <div class="resume-card">
        <h2>基本信息</h2>
        <div class="resume-grid">
          <el-form-item v-for="field in fields" :key="field.key" :label="field.label" :prop="field.key">
            <el-date-picker v-if="field.key === 'birthMonth'" id="resume-birthMonth" v-model="form.birthMonth"
              type="month" value-format="YYYY-MM" placeholder="请选择出生年月" />
            <el-input v-else :id="`resume-${field.key}`" v-model="form[field.key]" :maxlength="field.max"
              :placeholder="field.placeholder || `请填写${field.label}`" />
          </el-form-item>
        </div>
        <el-form-item label="是否参加过大数据、计算机相关比赛" prop="hasCompetitionExperience">
          <el-radio-group v-model="form.hasCompetitionExperience">
            <el-radio :value="true">是</el-radio>
            <el-radio :value="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </div>
      <div class="resume-card">
        <h2>个人介绍</h2>
        <el-form-item label="个人简历" prop="intro">
          <el-input id="resume-intro" v-model="form.intro" type="textarea" :rows="7" maxlength="800" show-word-limit
            placeholder="介绍你的学习经历、技能与项目经历，可分行填写。" />
        </el-form-item>
        <el-form-item label="本学年获奖情况" prop="awards">
          <el-input id="resume-awards" v-model="form.awards" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="没有可填写“无”" />
        </el-form-item>
        <el-form-item label="个人风采展示" prop="showcase">
          <el-input id="resume-showcase" v-model="form.showcase" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="填写作品介绍、作品名称或网址。" />
        </el-form-item>
      </div>
      <p class="resume-note">最终文档须为一到两页。内容较多无法完整排版时，可下载空白 Word，调整后通过“上传已填写 Word”提交。</p>
      <div class="resume-actions">
        <el-button type="primary" size="large" :loading="busy" native-type="submit">生成预览</el-button>
        <el-button :loading="downloading" @click="downloadTemplate">下载空白 Word 模板</el-button>
        <el-button @click="router.push('/resume/upload')">上传已填写 Word</el-button>
      </div>
    </el-form>
  </section>
</template>

<script setup>
import { ref, watch, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { resumeDraft, setResumePreview } from '../utils/resumeDraft'
import { resumeApi, downloadResumeFile } from '../utils/resumeApi'
import '../assets/resume.css'

const router = useRouter()
const form = resumeDraft.form
const formRef = ref()
const busy = ref(false)
const downloading = ref(false)
const errorText = ref('')
let active = true
onBeforeUnmount(() => { active = false })

const fields = [
  { key: 'name', label: '姓名', max: 30 }, { key: 'gender', label: '性别', max: 10 },
  { key: 'ethnicity', label: '民族', max: 30 }, { key: 'college', label: '学院', max: 80 },
  { key: 'studentNo', label: '学号', max: 40 }, { key: 'birthMonth', label: '出生年月' },
  { key: 'major', label: '专业', max: 80 }, { key: 'phone', label: '联系电话', max: 30 },
  { key: 'specialties', label: '特点特长', max: 120 },
  { key: 'gpa', label: '平均学分绩点', max: 20, placeholder: '例如3.60，也可填写暂无' }
]
const required = message => [{ required: true, whitespace: true, message, trigger: 'blur' }]
const rules = {
  name: required('请填写姓名'), college: required('请填写学院'), studentNo: required('请填写学号'),
  major: required('请填写专业'), phone: required('请填写联系电话'), intro: required('请填写个人简历'),
  hasCompetitionExperience: [{ required: true, type: 'boolean', message: '请选择是否参加过相关比赛', trigger: 'change' }]
}

watch(form, () => {
  resumeDraft.version++
  resumeDraft.preview = null
  resumeDraft.submission = null
}, { deep: true })

async function generate() {
  if (busy.value || !await formRef.value.validate().catch(() => false)) return
  errorText.value = ''
  busy.value = true
  const version = resumeDraft.version
  try {
    const preview = await resumeApi.generate(JSON.parse(JSON.stringify(form)))
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
  downloading.value = true
  try { downloadResumeFile(await resumeApi.template()) } catch (error) { errorText.value = error.message }
  finally { downloading.value = false }
}
</script>
