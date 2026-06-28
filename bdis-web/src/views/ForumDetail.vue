<template>
  <section class="detail-page">
    <div class="detail-container" v-if="item">
      <div class="detail-header">
        <span class="detail-deadline">
          <el-icon><Calendar /></el-icon>
          截止日期：{{ item.deadline }}
        </span>
      </div>
      <h1 class="detail-title">{{ item.title }}</h1>
      <p class="detail-summary">{{ item.summary }}</p>

      <div class="detail-cover" v-if="item.coverUrl">
        <img :src="normalizeImageUrl(item.coverUrl)" :alt="item.title" />
      </div>

      <div class="detail-info-grid">
        <div class="info-card">
          <h3>招募角色</h3>
          <div class="tags-section">
            <el-tag v-for="role in (item.recruitRoles || [])" :key="role" class="tag-item">{{ role }}</el-tag>
            <span v-if="!item.recruitRoles?.length" class="no-data">暂无</span>
          </div>
        </div>
        <div class="info-card">
          <h3>团队规模</h3>
          <div class="size-info">
            <span class="current">{{ item.currentSize ?? 0 }}</span>
            <span class="divider">/</span>
            <span class="target">{{ item.targetSize ?? 0 }}</span>
            <span class="label">人</span>
          </div>
        </div>
        <div class="info-card">
          <h3>联系方式</h3>
          <div class="contact-info">
            <span class="contact-type">{{ contactTypeText(item.type) }}：</span>
            <span class="contact-value">{{ item.value }}</span>
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
      <el-empty description="帖子不存在" />
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

const contactTypeText = (type) => {
  const map = { wechat: '微信', qq: 'QQ', email: '邮箱', link: '链接' }
  return map[type] || type
}

const openLink = (link) => {
  if (link?.startsWith('http')) window.open(link, '_blank')
}

onMounted(async () => {
  try {
    const { data } = await axios.get(`/api/platform/forum/${route.params.id}`)
    if (data.code === 0) item.value = data.data
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
  margin-bottom: 20px;
}

.detail-deadline {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #e6a23c;
  background: rgba(230, 162, 60, 0.1);
  padding: 4px 12px;
  border-radius: 4px;
}

.detail-title {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px;
}

.detail-summary {
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

.no-data { color: #999; font-size: 14px; }

.size-info {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.size-info .current { font-size: 32px; font-weight: 700; color: #0c3d70; }
.size-info .divider { font-size: 24px; color: #ccc; }
.size-info .target { font-size: 24px; color: #666; }
.size-info .label { font-size: 14px; color: #999; margin-left: 4px; }

.contact-info {
  font-size: 15px;
  color: #333;
}

.contact-type { color: #666; }
.contact-value { color: #0c3d70; font-weight: 500; }

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
