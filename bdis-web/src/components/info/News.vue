<!-- src/components/info/News.vue -->
<template>
  <section class="news-page">
    <!-- 主要内容区域 -->
    <div class="news-container">
      <!-- 筛选标签 -->
      <div class="filter-section">
        <div class="filter-group">
          <button
            v-for="category in categories"
            :key="category.value"
            type="button"
            class="filter-button"
            :class="{ active: selectedCategory === category.value }"
            :aria-pressed="selectedCategory === category.value"
            @click="handleCategoryChange(category.value)"
          >
            {{ category.label }}
          </button>
        </div>
      </div>

      <!-- 公告列表 - 卡片式布局 -->
      <div class="news-list" v-if="announcements.length > 0">
        <div 
          class="announcement-card" 
          v-for="item in announcements" 
          :key="item.id"
          @click="handleItemClick(item)"
        >
          <div class="card-left">
            <div class="card-image">
              <img :src="item.coverThumbUrl" :alt="item.title" v-if="item.coverThumbUrl" />
              <div class="image-placeholder" v-else>
                <el-icon><Document /></el-icon>
              </div>
            </div>
          </div>
          <div class="card-right">
            <div class="card-header">
              <el-tag :type="getTagType(item.category)" size="small" class="category-tag">
                {{ getCategoryText(item.category) }}
              </el-tag>
              <span class="card-date">
                <el-icon><Calendar /></el-icon>
                {{ item.publishDate }}
              </span>
            </div>
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-summary">{{ item.summary }}</p>
            <div class="card-footer">
              <span class="read-more">阅读更多 <el-icon><ArrowRight /></el-icon></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载中状态 -->
      <div v-else-if="loading" class="loading-placeholder">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-container">
        <el-empty description="暂无公告内容" />
      </div>

      <!-- 分页 -->
      <div class="pagination-container" v-if="announcements.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { Calendar, Document, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()

const announcements = ref([])
const loading = ref(false)
const selectedCategory = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const categories = [
  { value: 'all', label: '全部' },
  { value: 'recruit', label: '招新通告' },
  { value: 'activity', label: '活动组会' },
  { value: 'notice', label: '综合新闻' },
  { value: 'contest', label: '创新竞赛' },
  { value: 'project_recruit', label: '项目招募' }
]

// 分类文本映射
const getCategoryText = (category) => {
  const map = {
    recruit: '招新通告',
    activity: '活动组会',
    notice: '综合新闻',
    contest: '创新竞赛',
    project_recruit: '项目招募',
  }
  return map[category] || '未分类'
}

// 标签类型映射
const getTagType = (category) => {
  const map = {
    recruit: 'success',
    activity: 'warning',
    notice: 'info',
    contest: 'danger',
    project_recruit: '',
  }
  return map[category] || ''
}

// 获取公告列表
const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
    }
    if (selectedCategory.value !== 'all') {
      params.category = selectedCategory.value
    }

    const res = await axios.get('/api/announcements', { params })
    if (res.data?.code === 0) {
      announcements.value = res.data.data?.list || []
      total.value = res.data.data?.total || 0
    }
  } catch (err) {
    console.error('获取公告列表失败', err)
    announcements.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 分类改变
const handleCategoryChange = (category) => {
  if (selectedCategory.value === category) return
  selectedCategory.value = category
  currentPage.value = 1
  fetchAnnouncements()
}

// 分页大小改变
const handleSizeChange = () => {
  currentPage.value = 1
  fetchAnnouncements()
}

// 页码改变
const handlePageChange = () => {
  fetchAnnouncements()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 点击公告项
// const handleItemClick = (item) => {
//   if (item.link) {
//     // 如果是外部链接（http/https开头），在新窗口打开
//     if (item.link.startsWith('http://') || item.link.startsWith('https://')) {
//       window.open(item.link, '_blank')
//     } else {
//       // 内部链接
//       router.push(item.link).catch(() => {
//         router.push({ name: 'wip' })
//       })
//     }
//   } else {
//     router.push({ name: 'wip' })
//   }
// }

const handleItemClick = (item) => {
  // 统一跳转到页面建设中
  router.push({ name: 'wip' })
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.news-page {
  min-height: calc(100vh - 135px);
  background-color: #f9f9fa;
}

.news-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 32px;
}

.filter-section {
  margin-bottom: 32px;
  display: flex;
  justify-content: center;
}

.filter-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.filter-button {
  padding: 8px 16px;
  border-radius: 999px;
  border: 1px solid #d6dbe5;
  background: #fff;
  color: #0c3d70;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-button:hover {
  border-color: #0c3d70;
  box-shadow: 0 6px 14px rgba(12, 61, 112, 0.12);
  transform: translateY(-1px);
}

.filter-button.active {
  background: #0c3d70;
  color: #fff;
  border-color: #0c3d70;
  box-shadow: 0 8px 16px rgba(12, 61, 112, 0.2);
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.announcement-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.announcement-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.card-left {
  flex: 0 0 240px;
  height: 180px;
}

.card-image {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.announcement-card:hover .card-image img {
  transform: scale(1.05);
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.image-placeholder .el-icon {
  font-size: 48px;
}

.card-right {
  flex: 1;
  padding: 24px 32px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.category-tag {
  font-weight: 500;
}

.card-date {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #999;
}

.card-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-summary {
  font-size: 16px;
  line-height: 1.6;
  color: #666;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

.read-more {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #0c3d70;
  font-weight: 500;
  transition: gap 0.3s ease;
}

.announcement-card:hover .read-more {
  gap: 8px;
}

.loading-placeholder,
.empty-container {
  padding: 60px 0;
  text-align: center;
}

.pagination-container {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

:deep(.el-pagination) {
  justify-content: center;
}

:deep(.el-pagination button),
:deep(.el-pagination .el-pager li) {
  background-color: white;
}

:deep(.el-pagination .btn-next),
:deep(.el-pagination .btn-prev) {
  background-color: white;
}

@media (max-width: 768px) {
  .news-container {
    padding: 40px 16px;
  }

  .announcement-card {
    flex-direction: column;
  }

  .card-left {
    flex: none;
    width: 100%;
    height: 200px;
  }

  .card-right {
    padding: 20px;
  }

  .card-title {
    font-size: 18px;
  }

  .filter-section {
    margin-bottom: 24px;
  }

  .filter-group {
    width: 100%;
  }
}
</style>
