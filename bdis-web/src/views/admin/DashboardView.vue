<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.label">
        <div class="stat-icon" :style="{ backgroundColor: stat.color }">
          <el-icon :size="24"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-number">{{ stat.count }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>

    <div class="quick-actions">
      <h3 class="section-title">快捷操作</h3>
      <div class="actions-grid">
        <el-button v-for="action in actions" :key="action.label" @click="$router.push(action.path)" class="action-btn">
          <el-icon><component :is="action.icon" /></el-icon>
          {{ action.label }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, shallowRef } from 'vue'
import axios from 'axios'
import {
  Picture, Document, Bell, Suitcase, User,
  Reading, ChatDotRound, Plus
} from '@element-plus/icons-vue'

const stats = ref([
  { label: '轮播图', count: 0, icon: shallowRef(Picture), color: '#409eff' },
  { label: '新闻动态', count: 0, icon: shallowRef(Document), color: '#67c23a' },
  { label: '公告通知', count: 0, icon: shallowRef(Bell), color: '#e6a23c' },
  { label: '项目进展', count: 0, icon: shallowRef(Suitcase), color: '#f56c6c' },
  { label: '团队成员', count: 0, icon: shallowRef(User), color: '#909399' },
  { label: '学习资料', count: 0, icon: shallowRef(Reading), color: '#0c3d70' },
  { label: '竞赛组队', count: 0, icon: shallowRef(ChatDotRound), color: '#b37feb' },
])

const actions = [
  { label: '新增轮播', icon: Plus, path: '/admin/banners' },
  { label: '发布新闻', icon: Plus, path: '/admin/news' },
  { label: '发布公告', icon: Plus, path: '/admin/announcements' },
  { label: '添加成员', icon: Plus, path: '/admin/team-members' },
  { label: '添加资料', icon: Plus, path: '/admin/resources' },
  { label: '站点设置', icon: Plus, path: '/admin/lab-info' },
]

onMounted(async () => {
  const token = localStorage.getItem('adminToken')
  const headers = { Authorization: `Bearer ${token}` }

  const fetchCount = async (url) => {
    try {
      const res = await axios.get(url, { headers })
      if (res.data?.code === 0) {
        const d = res.data.data
        if (Array.isArray(d)) return d.length
        if (d?.total !== undefined) return d.total
        if (d?.list) return d.list.length
      }
    } catch (e) { /* ignore */ }
    return 0
  }

  const urls = [
    '/api/admin/banners',
    '/api/admin/news',
    '/api/admin/announcements',
    '/api/admin/projects',
    '/api/admin/team-members',
    '/api/admin/resources',
    '/api/admin/forum-posts',
  ]

  const counts = await Promise.all(urls.map(fetchCount))
  stats.value.forEach((s, i) => { s.count = counts[i] })
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 2px;
}

.quick-actions {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px;
}

.actions-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.action-btn {
  min-width: 120px;
}
</style>
