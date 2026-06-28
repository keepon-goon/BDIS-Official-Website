<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <h2 class="sidebar-title">BDIS CMS</h2>
        <p class="sidebar-subtitle">后台管理</p>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        @select="handleMenuSelect"
        background-color="#0C3D70"
        text-color="#b0c4de"
        active-text-color="#ffffff"
      >
        <el-menu-item index="dashboard">
          <el-icon><Odometer /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="banners">
          <el-icon><Picture /></el-icon>
          <span>轮播管理</span>
        </el-menu-item>
        <el-menu-item index="news">
          <el-icon><Document /></el-icon>
          <span>新闻管理</span>
        </el-menu-item>
        <el-menu-item index="announcements">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="projects">
          <el-icon><Suitcase /></el-icon>
          <span>项目管理</span>
        </el-menu-item>
        <el-menu-item index="team-members">
          <el-icon><User /></el-icon>
          <span>成员管理</span>
        </el-menu-item>
        <el-menu-item index="resources">
          <el-icon><Reading /></el-icon>
          <span>学习资料</span>
        </el-menu-item>
        <el-menu-item index="forum-posts">
          <el-icon><ChatDotRound /></el-icon>
          <span>竞赛组队</span>
        </el-menu-item>
        <el-menu-item index="lab-info">
          <el-icon><Setting /></el-icon>
          <span>站点设置</span>
        </el-menu-item>
      </el-menu>
    </aside>
    <div class="admin-main">
      <header class="admin-header">
        <div class="header-left">
          <span class="header-title">{{ currentPageTitle }}</span>
        </div>
        <div class="header-right">
          <span class="header-user">{{ adminNickname || '管理员' }}</span>
          <el-button text @click="handleLogout">退出登录</el-button>
          <el-button text @click="goToFrontend">返回前台</el-button>
        </div>
      </header>
      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Odometer, Picture, Document, Bell, Suitcase,
  User, Reading, ChatDotRound, Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const adminNickname = ref(localStorage.getItem('adminNickname') || '')

const activeMenu = computed(() => {
  const path = route.path
  if (path.includes('/admin/dashboard')) return 'dashboard'
  if (path.includes('/admin/banners')) return 'banners'
  if (path.includes('/admin/news')) return 'news'
  if (path.includes('/admin/announcements')) return 'announcements'
  if (path.includes('/admin/projects')) return 'projects'
  if (path.includes('/admin/team-members')) return 'team-members'
  if (path.includes('/admin/resources')) return 'resources'
  if (path.includes('/admin/forum-posts')) return 'forum-posts'
  if (path.includes('/admin/lab-info')) return 'lab-info'
  return 'dashboard'
})

const currentPageTitle = computed(() => {
  const titles = {
    dashboard: '控制台',
    banners: '轮播管理',
    news: '新闻管理',
    announcements: '公告管理',
    projects: '项目管理',
    'team-members': '成员管理',
    resources: '学习资料管理',
    'forum-posts': '竞赛组队管理',
    'lab-info': '站点设置'
  }
  return titles[activeMenu.value] || '控制台'
})

const handleMenuSelect = (index) => {
  router.push(`/admin/${index}`)
}

const handleLogout = () => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminNickname')
  router.push('/admin/login')
}

const goToFrontend = () => {
  router.push('/')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.admin-sidebar {
  width: 220px;
  background-color: #0C3D70;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 24px 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-title {
  color: #ffffff;
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

.sidebar-subtitle {
  color: #b0c4de;
  font-size: 12px;
  margin: 4px 0 0;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

:deep(.sidebar-menu .el-menu-item) {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

:deep(.sidebar-menu .el-menu-item.is-active) {
  background-color: rgba(255, 255, 255, 0.15) !important;
}

:deep(.sidebar-menu .el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.admin-header {
  height: 56px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-user {
  font-size: 14px;
  color: #666;
}

.admin-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
