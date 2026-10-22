<template>
  <div class="resume-admin">
    <el-card class="resume-admin-card">
      <template #header><strong>邮件设置</strong></template>
      <p class="resume-note">自动模式在游客确认保存后尝试发送一次；手动模式只保存，之后由管理员发送。修改设置不会自动重发历史报名。</p>
      <el-alert v-if="!settings.mailConfigured" title="发件邮箱参数尚未配置，可先保存报名；配置后再发送或补发。" type="warning" :closable="false" show-icon />
      <el-form :model="settings" label-position="top" @submit.prevent="saveSettings">
        <div class="resume-grid">
          <el-form-item label="发送模式">
            <el-radio-group v-model="settings.sendMode"><el-radio value="AUTO">确认保存后自动发送</el-radio><el-radio value="MANUAL">管理员手动发送</el-radio></el-radio-group>
          </el-form-item>
          <el-form-item label="目标收件邮箱"><el-input id="resume-recipient" v-model="settings.recipient" maxlength="254" placeholder="尚未确定时可以留空" /></el-form-item>
        </div>
        <el-button type="primary" native-type="submit" :loading="savingSettings">保存邮件设置</el-button>
      </el-form>
    </el-card>
    <el-card class="resume-admin-card">
      <template #header><strong>报名简历</strong></template>
      <el-form inline @submit.prevent="search">
        <el-form-item label="姓名"><el-input v-model="filters.name" clearable maxlength="30" placeholder="按姓名查询" /></el-form-item>
        <el-form-item label="学号"><el-input v-model="filters.studentNo" clearable maxlength="40" placeholder="按学号查询" /></el-form-item>
        <el-form-item label="邮件状态">
          <el-select v-model="filters.mailStatus" clearable placeholder="全部" style="width:160px">
            <el-option v-for="(label, value) in mailLabels" :key="value" :label="label" :value="Number(value)" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" native-type="submit">查询</el-button></el-form-item>
      </el-form>
      <el-alert v-if="errorText" :title="errorText" type="error" :closable="false" show-icon />
      <el-table :data="rows" v-loading="loading" row-key="id" style="width:100%">
        <el-table-column prop="name" label="姓名" min-width="110" />
        <el-table-column prop="studentNo" label="学号" min-width="140" />
        <el-table-column prop="academicYear" label="学年" width="115" />
        <el-table-column label="来源" width="95"><template #default="{ row }">{{ row.submissionSource === 'FORM' ? '在线填写' : row.submissionSource === 'UPLOAD' ? '上传Word' : '历史记录' }}</template></el-table-column>
        <el-table-column label="邮件状态" min-width="150"><template #default="{ row }"><el-tooltip :content="row.mailLastError || row.mailRecipient || '尚未发送'"><span>{{ mailLabels[row.mailStatus ?? 0] }}</span></el-tooltip></template></el-table-column>
        <el-table-column label="保存时间" min-width="170"><template #default="{ row }">{{ (row.createdAt || '').replace('T', ' ') }}</template></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <el-button link type="primary" :disabled="!row.fileAvailable" @click="showPdf(row)">PDF</el-button>
            <el-button link type="primary" :disabled="!row.fileAvailable" @click="downloadWord(row)">Word</el-button>
            <el-button link type="primary" :loading="sending === row.id" :disabled="!row.fileAvailable || !settings.mailConfigured || !settings.recipient || row.mailStatus === 1" @click="send(row)">{{ row.mailStatus === 0 ? '发送' : '补发' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="resume-admin-pagination" v-model:current-page="page" :page-size="10" :total="total"
        layout="prev, pager, next, total" @current-change="loadList" />
    </el-card>
    <el-dialog v-model="detailVisible" title="报名详情" width="min(820px, 94vw)">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item v-for="item in detailFields" :key="item.key" :label="item.label">{{ detailValue(item.key) }}</el-descriptions-item>
        </el-descriptions>
        <p v-if="detail.submissionSource === 'UPLOAD'" class="resume-note">这份简历通过Word上传，完整填写内容请以PDF或Word原件为准。</p>
        <div v-for="item in narrativeFields" :key="item.key" class="resume-detail-text"><strong>{{ item.label }}</strong><p>{{ detailValue(item.key) }}</p></div>
        <p class="resume-note">文件：{{ detail.documentName || '历史记录，未生成新版文件' }}</p>
        <p class="resume-note">最近一次目标：{{ detail.mailRecipient || '尚未发送' }}；结果：{{ detail.mailLastError || mailLabels[detail.mailStatus ?? 0] }}</p>
      </template>
    </el-dialog>
    <el-dialog v-model="pdfVisible" title="已保存的报名表 PDF" width="min(1000px, 96vw)" @closed="clearPdf">
      <ResumePdfPreview :url="pdfUrl" :filename="pdfFilename" />
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import ResumePdfPreview from '../../components/resume/ResumePdfPreview.vue'
import { resumeApi, downloadResumeFile } from '../../utils/resumeApi'
import '../../assets/resume.css'

const settings = reactive({ sendMode: 'AUTO', recipient: '', mailConfigured: false })
const filters = reactive({ name: '', studentNo: '', mailStatus: null })
const rows = ref([])
const page = ref(1)
const total = ref(0)
const loading = ref(false)
const savingSettings = ref(false)
const sending = ref('')
const errorText = ref('')
const detail = ref(null)
const detailVisible = ref(false)
const pdfVisible = ref(false)
const pdfUrl = ref('')
const pdfFilename = ref('报名表.pdf')
const mailLabels = { 0: '待发送', 1: '发送中/待核实', 2: '邮件服务器已接受', 3: '失败/待核实' }
const detailFields = [
  { key: 'name', label: '姓名' }, { key: 'studentNo', label: '学号' }, { key: 'gender', label: '性别' },
  { key: 'ethnicity', label: '民族' }, { key: 'college', label: '学院' }, { key: 'birthMonth', label: '出生年月' },
  { key: 'major', label: '专业' }, { key: 'phone', label: '联系电话' }, { key: 'specialties', label: '特点特长' },
  { key: 'gpa', label: '平均学分绩点' }, { key: 'academicYear', label: '学年' },
  { key: 'hasCompetitionExperience', label: '是否参加相关比赛' }
]
const narrativeFields = [{ key: 'intro', label: '个人简历' }, { key: 'awards', label: '本学年获奖情况' }, { key: 'showcase', label: '个人风采展示' }]

onMounted(async () => {
  try { Object.assign(settings, await resumeApi.settings()) } catch (error) { errorText.value = error.message }
  await loadList()
})
onBeforeUnmount(clearPdf)

async function loadList() {
  loading.value = true
  errorText.value = ''
  try {
    const result = await resumeApi.list({ ...filters, mailStatus: filters.mailStatus === '' ? null : filters.mailStatus, page: page.value, pageSize: 10 })
    rows.value = result.list
    total.value = result.total
  } catch (error) { errorText.value = error.message }
  finally { loading.value = false }
}
function search() { page.value = 1; loadList() }
function detailValue(key) {
  const value = detail.value[key]
  if (value === true) return '是'
  if (value === false) return '否'
  if (value !== null && value !== undefined && value !== '') return value
  return detail.value.submissionSource === 'UPLOAD' ? '详见Word原件' : '未填写'
}
async function saveSettings() {
  savingSettings.value = true
  try {
    Object.assign(settings, await resumeApi.updateSettings({ sendMode: settings.sendMode, recipient: settings.recipient.trim() }))
    ElMessage.success('邮件设置已保存')
  } catch (error) { ElMessage.error(error.message) }
  finally { savingSettings.value = false }
}
async function showDetail(row) {
  try { detail.value = await resumeApi.detail(row.id); detailVisible.value = true }
  catch (error) { ElMessage.error(error.message) }
}
function clearPdf() { if (pdfUrl.value) URL.revokeObjectURL(pdfUrl.value); pdfUrl.value = '' }
async function showPdf(row) {
  try {
    const file = await resumeApi.savedPdf(row.id)
    clearPdf()
    pdfUrl.value = URL.createObjectURL(file.blob)
    pdfFilename.value = row.documentName + '.pdf'
    pdfVisible.value = true
  } catch (error) { ElMessage.error(error.message) }
}
async function downloadWord(row) {
  try { downloadResumeFile(await resumeApi.savedWord(row.id)) } catch (error) { ElMessage.error(error.message) }
}
async function send(row) {
  sending.value = row.id
  try { const result = await resumeApi.send(row.id); ElMessage.success(result.message) }
  catch (error) { ElMessage.error(error.message) }
  finally { sending.value = ''; await loadList() }
}
</script>

<style scoped>
.resume-admin-card { margin-bottom: 20px; }
.resume-admin-pagination { margin-top: 20px; justify-content: flex-end; }
.resume-detail-text { margin: 20px 0; }
.resume-detail-text p { white-space: pre-wrap; overflow-wrap: anywhere; line-height: 1.8; }
</style>
