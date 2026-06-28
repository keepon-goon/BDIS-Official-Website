<template>
  <section class="search-page">
    <div class="search-container">
      <div class="search-header">
        <h1 class="search-title">搜索结果</h1>
        <div class="search-input-row">
          <el-input
            v-model="keyword"
            placeholder="输入关键词搜索..."
            size="large"
            clearable
            @keyup.enter="doSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="doSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
        <p class="search-info" v-if="searched">
          共找到 <strong>{{ total }}</strong> 条与 "<strong>{{ lastKeyword }}</strong>" 相关的结果
        </p>
      </div>

      <div class="search-results" v-if="results.length > 0">
        <div
          class="result-card"
          v-for="item in results"
          :key="item.type + '-' + item.id"
          @click="goToItem(item)"
        >
          <div class="result-type">
            <el-tag :type="getTypeTag(item.type)" size="small">{{ getTypeText(item.type) }}</el-tag>
          </div>
          <h3 class="result-title">{{ item.title }}</h3>
          <p class="result-summary">{{ item.summary }}</p>
          <span class="result-date" v-if="item.date">{{ item.date }}</span>
        </div>
      </div>

      <div v-else-if="searched" class="empty-container">
        <el-empty description="未找到相关内容" />
      </div>

      <div class="pagination-container" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="doSearch"
        />
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const keyword = ref(route.query.q || '')
const lastKeyword = ref('')
const results = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searched = ref(false)

const getTypeText = (type) => {
  const map = { news: '新闻', announcement: '公告', resource: '资料' }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = { news: '', announcement: 'warning', resource: 'success' }
  return map[type] || ''
}

const goToItem = (item) => {
  if (item.link) {
    if (item.link.startsWith('http')) {
      window.open(item.link, '_blank')
    } else {
      router.push(item.link)
    }
  }
}

const doSearch = async () => {
  if (!keyword.value.trim()) return
  lastKeyword.value = keyword.value
  searched.value = true
  try {
    const { data } = await axios.get('/api/search', {
      params: { q: keyword.value, page: currentPage.value, limit: pageSize.value }
    })
    if (data.code === 0) {
      results.value = data.data?.list || []
      total.value = data.data?.total || 0
    }
  } catch (e) {
    console.error('搜索失败', e)
    results.value = []
    total.value = 0
  }
}

onMounted(() => {
  if (keyword.value) doSearch()
})
</script>

<style scoped>
.search-page {
  min-height: calc(100vh - 135px);
  background-color: #f9f9fa;
}

.search-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 60px 32px;
}

.search-header {
  margin-bottom: 40px;
}

.search-title {
  font-size: 32px;
  font-weight: 600;
  color: #0c3d70;
  margin: 0 0 24px;
  text-align: center;
}

.search-input-row {
  max-width: 600px;
  margin: 0 auto 16px;
}

:deep(.el-input-group__append) {
  background-color: #0c3d70;
  color: #fff;
  border-color: #0c3d70;
}

.search-info {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin: 0;
}

.search-results {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.result-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.result-type {
  margin-bottom: 8px;
}

.result-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.result-summary {
  font-size: 15px;
  color: #666;
  margin: 0 0 8px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.result-date {
  font-size: 13px;
  color: #999;
}

.empty-container {
  padding: 60px 0;
  text-align: center;
}

.pagination-container {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
</style>
