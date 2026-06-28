<template>
  <section class="detail-page">
    <div class="detail-container" v-if="item">
      <div class="detail-header">
        <span class="status-badge" :class="item.status">{{ statusText(item.status) }}</span>
        <span class="detail-date">
          <el-icon><Calendar /></el-icon>
          {{ item.updatedAt }}
        </span>
      </div>
      <h1 class="detail-title">{{ item.name }}</h1>
      <p class="detail-tagline">{{ item.tagline }}</p>

      <div class="detail-cover" v-if="item.coverUrl">
        <img :src="normalizeImageUrl(item.coverUrl)" :alt="item.name" />
      </div>

      <div class="detail-info-grid">
        <div class="info-card">
          <h3>项目进度</h3>
          <div class="progress-section">
            <el-progress :percentage="item.progress || 0" :stroke-width="12" color="#0c3d70" />
          </div>
        </div>
        <div class="info-card">
          <h3>招募角色</h3>
          <div class="tags-section">
            <el-tag v-for="role in (item.recruitRolesArray || [])" :key="role" class="tag-item">{{ role }}</el-tag>
            <span v-if="!item.recruitRolesArray?.length" class="no-data">暂无</span>
          </div>
        </div>
        <div class="info-card">
          <h3>参与成员</h3>
          <div class="tags-section">
            <el-tag v-for="member in (item.membersArray || [])" :key="member" type="info" class="tag-item">{{ member }}</el-tag>
            <span v-if="!item.membersArray?.length" class="no-data">暂无</span>
          </div>
        </div>
      </div>

      <div class="detail-actions">
        <el-button @click="$router.back()">返回列表</el-button>
        <el-button type="primary" v-if="item.link" @click="openLink(item.link)">查看详情</el-button>
      </div>
    </div>
    <div v-else-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>
    <div v-else class="empty-container">
      <el-empty description="项目不存在" />
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { Calendar } from '@element-plus/icons-vue'
import { normalizeImageUrl } from '@/utils/imageUrl'

const route = useRoute()
const item = ref(null)
const loading = ref(true)

const statusText = (s) => {
  const map = { recruiting: '招募中', ongoing: '进行中', paused: '已暂停', done: '已完成' }
  return map[s] || s
}

const openLink = (link) => {
  if (link?.startsWith('http')) window.open(link, '_blank')
}

onMounted(async () => {
  try {
    const { data } = await axios.get(`/api/projects/${route.params.id}`)
    if (data.code === 0) item.value = data.data
  } catch (e) {
    console.error('获取项目详情失败', e)
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

.status-badge {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
  color: #fff;
}

.status-badge.recruiting { background-color: #67c23a; }
.status-badge.ongoing { background-color: #409eff; }
.status-badge.paused { background-color: #e6a23c; }
.status-badge.done { background-color: #909399; }

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
  margin: 0 0 12px;
}

.detail-tagline {
  font-size: 18px;
  color: #666;
  margin: 0 0 32px;
  line-height: 1.6;
}

.detail-cover {
  margin-bottom: 32px;
  border-radius: 12px;
  overflow: hidden;
}

.detail-cover img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  display: block;
}

.detail-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.info-card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.info-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #0c3d70;
  margin: 0 0 16px;
}

.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  font-size: 13px;
}

.no-data {
  color: #999;
  font-size: 14px;
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
