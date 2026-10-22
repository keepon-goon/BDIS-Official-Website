<template>
  <section class="resume-shell">
    <RouterLink class="resume-back" to="/info/join">
      <span>← 返回加入我们</span>
    </RouterLink>

    <!-- 流程步骤条 -->
    <div class="resume-step-bar">
      <div class="step-item is-active">
        <div class="step-number">1</div>
        <div class="step-text">
          <div class="step-title">在线填写信息</div>
          <div class="step-desc">基本信息与个人陈述</div>
        </div>
      </div>
      <div class="step-connector"></div>
      <div class="step-item">
        <div class="step-number">2</div>
        <div class="step-text">
          <div class="step-title">排版预览与核对</div>
          <div class="step-desc">两页标准核验与照片合成</div>
        </div>
      </div>
      <div class="step-connector"></div>
      <div class="step-item">
        <div class="step-number">3</div>
        <div class="step-text">
          <div class="step-title">正式确认提交</div>
          <div class="step-desc">文档归档与投递登记</div>
        </div>
      </div>
    </div>

    <h1>在线填写招新报名表</h1>
    <p class="resume-lead">填写后生成两页标准报名表，下一步可核对 PDF、下载 Word，或上传证件照。照片可以留空。</p>
    <el-alert v-if="errorText" :title="errorText" type="error" :closable="false" show-icon />
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top" :disabled="busy" @submit.prevent="generate">
      <div class="resume-card">
        <h2>基本信息</h2>
        <div class="resume-grid">
          <el-form-item v-for="field in fields" :key="field.key" :label="field.label" :prop="field.key">
            <el-date-picker v-if="field.key === 'birthMonth'" id="resume-birthMonth" v-model="form.birthMonth"
              type="month" value-format="YYYY-MM" :default-value="birthCalendarDefault" placeholder="请选择出生年月" />
            <template v-else-if="['gender', 'ethnicity', 'college', 'major'].includes(field.key)">
              <el-select :id="`resume-${field.key}`" :model-value="selections[field.key]" filterable clearable
                :placeholder="field.key === 'major' && !form.college ? '请先选择学院' : `请选择${field.label}`"
                :no-data-text="field.key === 'major' && !form.college ? '请先选择学院' : '没有匹配的选项'"
                @update:model-value="changeSelection(field.key, $event)" @visible-change="visible => menuOpened(field.key, visible)">
                <el-option v-for="option in fieldOptions(field.key)" :key="option" :label="option" :value="option" />
              </el-select>
              <el-input v-if="selections[field.key] === '其他'" :id="`resume-${field.key}-other`"
                v-model.trim="form[field.key]" class="resume-other" :maxlength="field.max" :placeholder="`请填写具体${field.label}`" />
            </template>
            <el-input v-else :id="`resume-${field.key}`" v-model.trim="form[field.key]" :maxlength="field.max"
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
            placeholder="让我们听听你的故事" />
        </el-form-item>
        <el-form-item label="本学年获奖情况" prop="awards">
          <ResumeChoiceTextarea id="resume-awards" v-model="form.awards" v-model:choice="resumeDraft.narrativeChoices.awards"
            yes-label="有" no-label="无" placeholder="让我们欣赏你的事迹" declined-text="让我们一起谱写自己的篇章"
            negative-value="无" :disabled="busy" />
        </el-form-item>
        <el-form-item label="个人风采展示" prop="showcase">
          <ResumeChoiceTextarea id="resume-showcase" v-model="form.showcase" v-model:choice="resumeDraft.narrativeChoices.showcase"
            yes-label="小露一手" no-label="不告诉你" placeholder="can can need" declined-text="伤心了...(ㄒoㄒ)" :disabled="busy" />
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
import { ref, reactive, computed, watch, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ResumeChoiceTextarea from '../components/resume/ResumeChoiceTextarea.vue'
import resumeOptions from '../data/resumeOptions.json'
import { resumePhonePattern, resumePhoneMessage } from '../utils/resumeValidation'
import { resumeDraft, setResumePreview } from '../utils/resumeDraft'
import { resumeApi, downloadResumeFile } from '../utils/resumeApi'
import '../assets/resume.css'

const router = useRouter()
const form = resumeDraft.form
const formRef = ref()
const busy = ref(false)
const downloading = ref(false)
const errorText = ref('')
const today = new Date()
const birthCalendarDefault = new Date(today.getFullYear() - 18, today.getMonth(), 1)
const genders = ['男', '女', '沃尔玛购物袋', '武装直升机']
const colleges = [...resumeOptions.colleges.map(college => college.name), '其他']
const ethnicities = [...resumeOptions.ethnicities, '其他']
const majors = computed(() => !form.college ? [] : [...(resumeOptions.colleges.find(college => college.name === form.college)?.majors || []), '其他'])
const selectionFor = (value, options) => !value ? '' : options.includes(value) ? value : '其他'
const selections = reactive({
  gender: form.gender, ethnicity: selectionFor(form.ethnicity, ethnicities),
  college: selectionFor(form.college, colleges), major: selectionFor(form.major, majors.value)
})
let active = true
onBeforeUnmount(() => { active = false })

const fields = [
  { key: 'name', label: '姓名', max: 30 }, { key: 'gender', label: '性别', max: 10 },
  { key: 'ethnicity', label: '民族', max: 30 }, { key: 'college', label: '学院', max: 80 },
  { key: 'studentNo', label: '学号', max: 40 }, { key: 'birthMonth', label: '出生年月' },
  { key: 'major', label: '专业', max: 80 }, { key: 'phone', label: '联系电话', max: 30, placeholder: '手机号或带区号的固定电话' },
  { key: 'specialties', label: '特点特长', max: 120 },
  { key: 'gpa', label: '平均学分绩点', max: 20, placeholder: '例如3.60，也可填写暂无' }
]
const required = message => [{ required: true, whitespace: true, message, trigger: 'blur' }]
const rules = {
  name: required('请填写姓名'), college: required('请填写学院'), studentNo: required('请填写学号'),
  major: required('请填写专业'), phone: [...required('请填写联系电话'), { pattern: resumePhonePattern, message: resumePhoneMessage, trigger: 'blur' }],
  intro: required('请填写个人简历'),
  ethnicity: [{ validator: (_rule, value, callback) => callback(selections.ethnicity === '其他' && !value.trim() ? new Error('请填写具体民族') : undefined), trigger: 'blur' }],
  hasCompetitionExperience: [{ required: true, type: 'boolean', message: '请选择是否参加过相关比赛', trigger: 'change' }]
}

function fieldOptions(key) {
  return { gender: genders, ethnicity: ethnicities, college: colleges, major: majors.value }[key]
}

function changeSelection(key, value) {
  selections[key] = value
  form[key] = value === '其他' ? '' : value
}

function menuOpened(key, visible) {
  if (key === 'major' && visible && !form.college) ElMessage.info('请先选择学院')
}

watch(() => form.college, () => { form.major = ''; selections.major = '' })

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
