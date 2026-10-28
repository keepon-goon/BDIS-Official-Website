<template>
  <div class="feedback-admin">
    <div class="feedback-admin-heading">
      <div><h2>意见收集</h2><p>查看匿名投递的文字与图片，整理网站与工作室建议。</p></div>
      <el-button :loading="loading" @click="loadList">刷新列表</el-button>
    </div>
    <div class="feedback-stats">
      <div v-for="item in countItems" :key="item.key"><span>{{ item.label }}</span><strong>{{ counts[item.key] || 0 }}</strong></div>
    </div>
    <el-card shadow="never">
      <div class="feedback-filters">
        <el-select v-model="status" clearable placeholder="全部处理状态" aria-label="按处理状态筛选" @change="filterChanged">
          <el-option v-for="item in feedbackStatuses" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="category" clearable placeholder="全部意见分类" aria-label="按意见分类筛选" @change="filterChanged">
          <el-option v-for="item in feedbackCategories" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
      <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="feedback-list-error" />
      <el-table :data="records" v-loading="loading" stripe empty-text="当前没有符合条件的意见">
        <el-table-column label="投递时间" width="170"><template #default="{ row }">{{ formatTime(row.createdAt) }}</template></el-table-column>
        <el-table-column label="分类" width="130"><template #default="{ row }">{{ categoryLabel(row.category) }}</template></el-table-column>
        <el-table-column label="意见内容" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">{{ row.content || '仅图片投递' }}</template>
        </el-table-column>
        <el-table-column label="图片" width="85" align="center"><template #default="{ row }">{{ row.images.length }} 张</template></el-table-column>
        <el-table-column label="回复方式" width="115"><template #default="{ row }">{{ !row.replyRequested ? '无需回复' : row.publicReply ? '公开问答' : '邮箱私信' }}</template></el-table-column>
        <el-table-column label="回复情况" width="115"><template #default="{ row }">{{ !row.replyRequested ? '无需回复' : row.published ? '已公开' : row.replyContent ? '已回复未公开' : '尚未回复' }}</template></el-table-column>
        <el-table-column label="处理状态" width="110">
          <template #default="{ row }"><el-tag :type="statusInfo(row.status).type">{{ statusInfo(row.status).label }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }"><el-button size="small" type="primary" plain @click="openDetail(row)">查看详情</el-button></template>
        </el-table-column>
      </el-table>
      <div class="feedback-pagination">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadList" />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="匿名意见详情" width="min(780px, calc(100% - 32px))" destroy-on-close
      :close-on-click-modal="!saving && !replySaving && !emailSending" :close-on-press-escape="!saving && !replySaving && !emailSending"
      :show-close="!saving && !replySaving && !emailSending" @close="closeDetail">
      <div v-loading="detailLoading" class="feedback-detail">
        <el-alert v-if="detailError" :title="detailError" type="error" :closable="false" show-icon />
        <template v-if="detail">
          <div class="feedback-detail-meta"><el-tag>{{ categoryLabel(detail.category) }}</el-tag><span>{{ formatTime(detail.createdAt) }}</span></div>
          <h3>意见内容</h3>
          <p class="feedback-detail-text">{{ detail.content || '本条意见通过图片投递。' }}</p>
          <template v-if="detail.images.length">
            <h3>附件图片 <span>{{ detail.images.length }} 张</span></h3>
            <div class="feedback-detail-images">
              <div v-for="(image, index) in imageCards" :key="image.id">
                <el-image v-if="image.url" :src="image.url" :alt="`意见图片 ${index + 1}`" fit="cover"
                  :preview-src-list="imageCards.filter(item => item.url).map(item => item.url)"
                  :initial-index="imageCards.slice(0, index).filter(item => item.url).length" preview-teleported />
                <p v-else class="feedback-image-error">图片读取失败</p>
              </div>
            </div>
            <p v-if="imageCards.some(item => !item.url)" class="feedback-image-help">部分图片未能读取，可关闭后重新打开详情重试。</p>
          </template>
          <section v-if="detail.replyRequested" class="feedback-reply-editor">
            <h3>回复这位朋友</h3>
            <p class="feedback-reply-preference">{{ detail.publicReply ? `投稿人同意公开，展示署名：${detail.displayName}` : `投稿人仅接收私密回信：${detail.replyEmail}` }}</p>
            <label for="feedback-admin-reply">实验室朋友的回答</label>
            <el-input id="feedback-admin-reply" v-model="replyContent" type="textarea" :rows="6" maxlength="3000" show-word-limit
              placeholder="写下给这位朋友的回答" :disabled="replySaving || emailSending" />
            <el-checkbox v-model="replyPublished" :disabled="!detail.publicReply || replySaving || emailSending">在官网 Q&A 公开展示</el-checkbox>
            <p v-if="!detail.publicReply" class="feedback-private-note">投稿人未同意公开，本条问答只能通过邮箱回复。</p>
            <div class="feedback-reply-actions">
              <el-button type="primary" :loading="replySaving" :disabled="saving || emailSending || !replyContent.trim()" @click="saveReply">保存回复</el-button>
              <el-button v-if="!detail.publicReply" :loading="emailSending"
                :disabled="saving || replySaving || !detail.replyContent || replyDirty" @click="sendReplyEmail">{{ detail.replyEmailedAt ? '重新发送邮件' : '发送邮件回信' }}</el-button>
            </div>
            <p v-if="replyDirty && !detail.publicReply" class="feedback-private-note">先保存当前回复，再发送邮件。</p>
            <p v-if="detail.replyEmailedAt" class="feedback-private-note">邮件服务已接受发送：{{ formatTime(detail.replyEmailedAt) }}</p>
            <p v-if="replyError" class="feedback-reply-error" role="alert">{{ replyError }}</p>
          </section>
          <p v-else class="feedback-private-note">投稿人未选择接收回复，本条意见仅供管理员处理，不会公开展示。</p>
          <p class="feedback-private-note">附件图片和回信邮箱仅供管理员查看。公开问答只展示留言文字、回复及展示署名。</p>
        </template>
      </div>
      <template #footer>
        <div class="feedback-detail-footer">
          <label v-if="detail" for="feedback-status">处理状态</label>
          <el-select v-if="detail" id="feedback-status" v-model="selectedStatus" :disabled="saving || detailLoading || replySaving || emailSending" aria-label="更新处理状态">
            <el-option v-for="item in feedbackStatuses" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button :disabled="saving || replySaving || emailSending" @click="detailVisible = false">关闭</el-button>
          <el-button v-if="detail" type="primary" :loading="saving" :disabled="detailLoading || replySaving || emailSending || selectedStatus === detail.status" @click="saveStatus">保存状态</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { feedbackApi, feedbackCategories, feedbackStatuses } from '@/utils/feedbackApi'

const records = ref([])
const counts = ref({})
const total = ref(0)
const page = ref(1)
const pageSize = 10
const status = ref('')
const category = ref('')
const loading = ref(false)
const error = ref('')
const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)
const detailError = ref('')
const imageCards = ref([])
const selectedStatus = ref('')
const saving = ref(false)
const replyContent = ref('')
const replyPublished = ref(false)
const replySaving = ref(false)
const emailSending = ref(false)
const replyError = ref('')
const replyDirty = computed(() => replyContent.value !== (detail.value?.replyContent || '') || replyPublished.value !== Boolean(detail.value?.published))
const countItems = [{ key: 'total', label: '全部意见' }, { key: 'UNREAD', label: '未读' }, { key: 'PROCESSING', label: '处理中' }, { key: 'RESOLVED', label: '已处理' }]
const categoryLabel = value => feedbackCategories.find(item => item.value === value)?.label || '其他想法'
const statusInfo = value => feedbackStatuses.find(item => item.value === value) || feedbackStatuses[0]
const formatTime = value => value ? value.replace('T', ' ').slice(0, 16) : '—'
let listRequest
let detailRequest

async function loadList() {
  listRequest?.abort()
  const controller = new AbortController()
  listRequest = controller
  loading.value = true
  error.value = ''
  try {
    const result = await feedbackApi.list({ page: page.value, pageSize, status: status.value || undefined, category: category.value || undefined }, controller.signal)
    if (controller.signal.aborted) return
    records.value = result.list
    total.value = result.total
    counts.value = result.counts
    if (page.value > 1 && !records.value.length) {
      page.value = Math.max(1, Math.ceil(total.value / pageSize))
      await loadList()
    }
  } catch (failure) {
    if (!controller.signal.aborted) { error.value = failure.message; records.value = [] }
  } finally { if (listRequest === controller) loading.value = false }
}

function filterChanged() { page.value = 1; loadList() }
function clearImages() { imageCards.value.forEach(item => { if (item.url) URL.revokeObjectURL(item.url) }); imageCards.value = [] }
function closeDetail() { detailRequest?.abort(); clearImages() }

async function openDetail(row) {
  closeDetail()
  const controller = new AbortController()
  detailRequest = controller
  detail.value = null
  detailError.value = ''
  replyError.value = ''
  detailVisible.value = true
  detailLoading.value = true
  try {
    const result = await feedbackApi.detail(row.id, controller.signal)
    if (controller.signal.aborted) return
    detail.value = result
    selectedStatus.value = result.status
    replyContent.value = result.replyContent || ''
    replyPublished.value = Boolean(result.published)
    for (const image of result.images) {
      try {
        const blob = await feedbackApi.image(image.url, controller.signal)
        if (controller.signal.aborted) return
        imageCards.value.push({ id: image.id, url: URL.createObjectURL(blob) })
      } catch (failure) {
        if (controller.signal.aborted) return
        imageCards.value.push({ id: image.id, url: '' })
      }
    }
  } catch (failure) { if (!controller.signal.aborted) detailError.value = failure.message }
  finally { if (detailRequest === controller) detailLoading.value = false }
}

async function saveStatus() {
  if (!detail.value || saving.value) return
  saving.value = true
  const id = detail.value.id
  const nextStatus = selectedStatus.value
  try {
    await feedbackApi.status(id, nextStatus)
    if (detail.value?.id === id) detail.value.status = nextStatus
    ElMessage.success('处理状态已保存')
    await loadList()
  } catch (failure) { ElMessage.error(failure.message) }
  finally { saving.value = false }
}

async function saveReply() {
  if (!detail.value || replySaving.value || emailSending.value) return
  replySaving.value = true
  replyError.value = ''
  try {
    const result = await feedbackApi.reply(detail.value.id, { replyContent: replyContent.value, published: replyPublished.value })
    detail.value = result
    replyContent.value = result.replyContent
    replyPublished.value = Boolean(result.published)
    selectedStatus.value = result.status
    ElMessage.success(result.published ? '回复已保存并公开展示' : '回复已保存')
    await loadList()
  } catch (failure) { replyError.value = failure.message }
  finally { replySaving.value = false }
}

async function sendReplyEmail() {
  if (!detail.value || emailSending.value || replyDirty.value) return
  emailSending.value = true
  replyError.value = ''
  try {
    detail.value = await feedbackApi.emailReply(detail.value.id)
    ElMessage.success('回信已提交给邮件服务')
  } catch (failure) { replyError.value = failure.message }
  finally { emailSending.value = false }
}

onMounted(loadList)
onBeforeUnmount(() => { listRequest?.abort(); closeDetail() })
</script>

<style scoped>
.feedback-admin-heading { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 20px; }
.feedback-admin-heading h2 { color: #0c3d70; margin: 0 0 7px; font-size: 22px; }
.feedback-admin-heading p { color: #718398; margin: 0; font-size: 13px; }
.feedback-stats { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 16px; margin-bottom: 20px; }
.feedback-stats > div { display: flex; justify-content: space-between; align-items: center; gap: 8px; background: white; border: 1px solid #e1e9f2; border-radius: 8px; padding: 20px; }
.feedback-stats span { color: #6e8196; font-size: 14px; }
.feedback-stats strong { color: #0c3d70; font-size: 26px; }
.feedback-filters { display: flex; gap: 12px; flex-wrap: wrap; margin-bottom: 20px; }
.feedback-filters .el-select { width: 180px; }
.feedback-list-error { margin-bottom: 16px; }
.feedback-pagination { display: flex; justify-content: flex-end; margin-top: 20px; }
.feedback-detail { min-height: 160px; }
.feedback-detail-meta { display: flex; align-items: center; gap: 14px; color: #7b8da1; font-size: 13px; margin-bottom: 24px; }
.feedback-detail h3 { color: #0c3d70; font-size: 15px; margin: 18px 0 12px; }
.feedback-detail h3 span { color: #7b8da1; font-weight: 400; font-size: 12px; }
.feedback-detail-text { white-space: pre-wrap; overflow-wrap: anywhere; line-height: 1.9; color: #34516e; background: #f6f9fd; border-radius: 8px; padding: 18px; }
.feedback-detail-images { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 12px; }
.feedback-detail-images .el-image, .feedback-image-error { width: 100%; height: 140px; border-radius: 6px; background: #f2f6fb; }
.feedback-image-error { display: grid; place-items: center; color: #8c9bac; font-size: 13px; margin: 0; }
.feedback-image-help { color: #b25c26; font-size: 12px; }
.feedback-private-note { color: #8293a4; font-size: 12px; margin-top: 24px; }
.feedback-reply-editor { margin-top: 24px; padding-top: 4px; border-top: 1px solid #e1e9f2; }
.feedback-reply-editor > label { display: block; margin: 14px 0 10px; color: #34516e; font-size: 14px; }
.feedback-reply-preference { color: #62778d; font-size: 13px; overflow-wrap: anywhere; line-height: 1.8; }
.feedback-reply-actions { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 12px; }
.feedback-reply-actions .el-button + .el-button { margin-left: 0; }
.feedback-reply-error { color: #ba303c; font-size: 13px; line-height: 1.8; }
.feedback-detail-footer { display: flex; align-items: center; justify-content: flex-end; flex-wrap: wrap; gap: 12px; }
.feedback-detail-footer label { font-size: 13px; color: #62778d; }
.feedback-detail-footer .el-select { width: 130px; }
@media (max-width: 1000px) { .feedback-stats { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 600px) { .feedback-detail-images { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
</style>
