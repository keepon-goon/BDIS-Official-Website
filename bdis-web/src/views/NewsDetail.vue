<template>
  <section class="detail-page">
    <div class="detail-container" v-if="item">
      <div class="detail-header">
        <el-tag :type="getTagType(item.category)" size="small">
          {{ getCategoryText(item.category) }}
        </el-tag>
        <span class="detail-date">
          <el-icon><Calendar /></el-icon>
          {{ item.publishDate }}
        </span>
      </div>
      <h1 class="detail-title">{{ item.title }}</h1>
      <div class="detail-cover" v-if="item.coverThumbUrl">
        <img :src="normalizeImageUrl(item.coverThumbUrl)" :alt="item.title" />
      </div>
      <div class="detail-body">
        <p class="detail-summary">{{ item.summary }}</p>
        <div v-if="item.content" class="detail-content" v-html="normalizeUploadPaths(item.content)"></div>
      </div>
      <div class="detail-actions">
        <el-button @click="$router.back()">返回列表</el-button>
        <el-button type="primary" v-if="item.link" @click="openLink(item.link)">查看原文</el-button>
      </div>
    </div>
    <div v-else-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>
    <div v-else class="empty-container">
      <el-empty description="内容不存在" />
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { Calendar } from '@element-plus/icons-vue'
import { normalizeImageUrl, normalizeUploadPaths } from '@/utils/imageUrl'

const route = useRoute()
const item = ref(null)
const loading = ref(true)

const getCategoryText = (category) => {
  const map = {
    recruit: '招新通告', activity: '活动组会', notice: '综合新闻',
    contest: '创新竞赛', project_recruit: '项目招募'
  }
  return map[category] || '未分类'
}

const getTagType = (category) => {
  const map = {
    recruit: 'success', activity: 'warning', notice: 'info',
    contest: 'danger', project_recruit: ''
  }
  return map[category] || ''
}

const openLink = (link) => {
  if (link.startsWith('http')) {
    window.open(link, '_blank')
  }
}

onMounted(async () => {
  try {
    const { data } = await axios.get(`/api/news/${route.params.id}`)
    if (data.code === 0 && data.data) {
      item.value = data.data
    }
  } catch (e) {
    console.error('获取详情失败', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail-page {
  min-height: calc(100vh - 135px);
  background-color: #f9f9fa;
}

.detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 60px 32px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.detail-date {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #999;
}

.detail-title {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin: 0 0 24px;
  line-height: 1.4;
}

.detail-cover {
  margin-bottom: 32px;
  border-radius: 12px;
  overflow: hidden;
}

.detail-cover img {
  width: 100%;
  max-height: 450px;
  object-fit: cover;
  display: block;
}

.detail-body {
  background: #fff;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 32px;
}

.detail-summary {
  font-size: 18px;
  line-height: 1.8;
  color: #444;
  margin: 0 0 24px;
  white-space: pre-line;
}

.detail-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
}

.detail-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 16px 0;
}

.detail-content :deep(p) {
  margin: 12px 0;
}

.detail-content :deep(h2),
.detail-content :deep(h3) {
  margin: 24px 0 12px;
  color: #0c3d70;
}

.detail-content :deep(blockquote) {
  border-left: 4px solid #0c3d70;
  padding-left: 16px;
  margin: 16px 0;
  color: #666;
  background: #f9f9fa;
  padding: 12px 16px;
  border-radius: 0 8px 8px 0;
}

.detail-actions {
  display: flex;
  gap: 12px;
}

.loading-container,
.empty-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 60px 32px;
}
</style>
