<!-- src/components/info/Education.vue -->
<template>
  <section class="education-page">
    <!-- 主要内容区域 -->
    <div class="education-container">
      <!-- 分类导航 -->
      <div class="category-nav">
        <el-menu
          mode="horizontal"
          :default-active="activeCategory"
          @select="handleCategorySelect"
          class="category-menu"
          active-text-color="#0c3d70"
        >
          <el-menu-item index="all">全部</el-menu-item>
          <el-menu-item index="frontend">前端开发</el-menu-item>
          <el-menu-item index="backend">后端开发</el-menu-item>
          <el-menu-item index="database">数据库</el-menu-item>
          <el-menu-item index="ai">人工智能</el-menu-item>
          <el-menu-item index="tools">开发工具</el-menu-item>
          <el-menu-item index="course">课程资料</el-menu-item>
        </el-menu>
      </div>

      <!-- 搜索框 -->
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学习资料..."
          clearable
          @clear="handleClear"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #suffix>
            <el-button @click="handleSearch" text>搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 资料列表 -->
      <div class="education-content">
        <!-- 资料卡片网格 -->
        <div class="resources-grid" v-if="resources.length > 0">
          <div
            class="resource-card"
            v-for="resource in resources"
            :key="resource.id"
            @click="handleResourceClick(resource)"
          >
            <div class="resource-content">
              <h3 class="resource-title">{{ resource.title }}</h3>
              <p class="resource-desc">{{ resource.description }}</p>
              <div class="resource-footer">
                <el-button type="primary" text :icon="ArrowRight">查看详情</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载中状态 -->
        <div v-else-if="loading" class="loading-container">
          <el-skeleton :rows="4" animated :repeat="6" />
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-container">
          <el-empty description="暂无学习资料" />
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container" v-if="resources.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 36, 48]"
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
import {
  Search,
  ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()

const resources = ref([])
const loading = ref(false)
const activeCategory = ref('all')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 获取资料列表
const fetchResources = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
    }
    if (activeCategory.value !== 'all') {
      params.category = activeCategory.value
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    const res = await axios.get('/api/resources', { params })
    if (res.data?.code === 0) {
      resources.value = res.data.data?.list || []
      total.value = res.data.data?.total || 0
    }
  } catch (err) {
    console.error('获取学习资料失败', err)
    resources.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 分类选择
const handleCategorySelect = (key) => {
  activeCategory.value = key
  currentPage.value = 1
  fetchResources()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchResources()
}

// 清空搜索
const handleClear = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  fetchResources()
}

// 分页大小改变
const handleSizeChange = () => {
  currentPage.value = 1
  fetchResources()
}

// 页码改变
const handlePageChange = () => {
  fetchResources()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 点击资料项
const handleResourceClick = (resource) => {
  if (resource.link) {
    // 如果是外部链接（http/https开头），在新窗口打开
    if (resource.link.startsWith('http://') || resource.link.startsWith('https://')) {
      window.open(resource.link, '_blank')
    } else {
      // 如果是内部链接，先检查路由是否存在
      // 如果不存在或跳转失败，跳转到建设中页面
      try {
        router.push(resource.link).catch(() => {
          router.push({ name: 'wip' })
        })
      } catch (error) {
        router.push({ name: 'wip' })
      }
    }
  } else {
    // 没有链接，跳转到建设中页面（详情页还未开发）
    router.push({ name: 'wip' })
  }
}

onMounted(() => {
  fetchResources()
})
</script>

<style scoped>
.education-page {
  min-height: calc(100vh - 135px);
  background-color: #f9f9fa;
}

.education-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 32px;
}

.category-nav {
  margin-bottom: 24px;
  background: white;
  border-radius: 8px;
  padding: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

:deep(.category-menu) {
  border-bottom: none;
  display: flex;
  justify-content: center;
}

:deep(.category-menu .el-menu-item) {
  border-bottom: none;
  height: 48px;
  line-height: 48px;
}

:deep(.category-menu .el-menu-item.is-active) {
  color: #0c3d70;
  border-bottom: 2px solid #0c3d70;
}

.search-section {
  margin-bottom: 32px;
}

:deep(.el-input) {
  max-width: 600px;
  margin: 0 auto;
}

:deep(.el-input__wrapper) {
  border-radius: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.education-content {
  min-height: 400px;
}

.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.resource-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.resource-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.resource-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.resource-title {
  font-size: 18px;
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

.resource-desc {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  margin: 0 0 16px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.resource-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.loading-container,
.empty-container {
  padding: 60px 0;
  text-align: center;
}

.pagination-container {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination) {
  justify-content: center;
}

@media (max-width: 768px) {
  .education-container {
    padding: 40px 16px;
  }

  .resources-grid {
    grid-template-columns: 1fr;
  }

  .category-nav {
    overflow-x: auto;
  }

  :deep(.category-menu) {
    width: max-content;
  }
}
</style>

