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
      :close-on-click-modal="!saving" :close-on-press-escape="!saving" :show-close="!saving" @close="closeDetail">
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
          <p class="feedback-private-note">匿名投递内容仅供管理员查看，请在工作室内部妥善处理。</p>
        </template>
      </div>
      <template #footer>
        <div class="feedback-detail-footer">
          <label v-if="detail" for="feedback-status">处理状态</label>
          <el-select v-if="detail" id="feedback-status" v-model="selectedStatus" :disabled="saving || detailLoading" aria-label="更新处理状态">
            <el-option v-for="item in feedbackStatuses" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button :disabled="saving" @click="detailVisible = false">关闭</el-button>
          <el-button v-if="detail" type="primary" :loading="saving" :disabled="detailLoading || selectedStatus === detail.status" @click="saveStatus">保存状态</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
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
  detailVisible.value = true
  detailLoading.value = true
  try {
    const result = await feedbackApi.detail(row.id, controller.signal)
    if (controller.signal.aborted) return
    detail.value = result
    selectedStatus.value = result.status
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
.feedback-detail-footer { display: flex; align-items: center; justify-content: flex-end; flex-wrap: wrap; gap: 12px; }
.feedback-detail-footer label { font-size: 13px; color: #62778d; }
.feedback-detail-footer .el-select { width: 130px; }
@media (max-width: 1000px) { .feedback-stats { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 600px) { .feedback-detail-images { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
</style>
