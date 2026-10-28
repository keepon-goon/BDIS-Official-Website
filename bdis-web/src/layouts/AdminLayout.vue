<template>
  <div class="admin-layout" :class="{ 'is-mobile': isMobile }">
    <!-- 移动端暗色遮罩层 -->
    <div
      v-if="isMobile && isMobileMenuOpen"
      class="sidebar-backdrop"
      @click="closeMobileMenu"
    ></div>

    <!-- 侧边导航栏 -->
    <aside
      class="admin-sidebar"
      :class="{
        'mobile-sidebar': isMobile,
        'mobile-sidebar-open': isMobile && isMobileMenuOpen
      }"
    >
      <div class="sidebar-header">
        <div class="sidebar-branding">
          <h2 class="sidebar-title">BDIS CMS</h2>
          <p class="sidebar-subtitle">后台管理系统</p>
        </div>
        <button
          v-if="isMobile"
          class="sidebar-close-btn"
          @click="closeMobileMenu"
          aria-label="关闭导航"
        >
          <el-icon :size="20"><Close /></el-icon>
        </button>
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
        <el-menu-item index="resumes">
          <el-icon><Document /></el-icon>
          <span>简历管理</span>
        </el-menu-item>
        <el-menu-item index="feedback">
          <el-icon><ChatDotRound /></el-icon>
          <span>意见收集</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <div class="admin-main">
      <header class="admin-header">
        <div class="header-left">
          <!-- 移动端汉堡切换按钮 -->
          <button
            v-if="isMobile"
            class="mobile-hamburger-btn"
            @click="toggleMobileMenu"
            aria-label="展开或收起菜单"
          >
            <el-icon :size="20"><Expand v-if="!isMobileMenuOpen" /><Fold v-else /></el-icon>
          </button>
          <span class="header-title">{{ currentPageTitle }}</span>
        </div>

        <!-- 桌面端右侧操作 -->
        <div v-if="!isMobile" class="header-right">
          <span class="header-user">{{ adminNickname || '管理员' }}</span>
          <el-button text @click="handleLogout">退出登录</el-button>
          <el-button text @click="goToFrontend">返回前台</el-button>
        </div>

        <!-- 移动端右侧紧凑下拉操作 -->
        <div v-else class="header-right-mobile">
          <el-dropdown trigger="click" placement="bottom-end">
            <div class="mobile-user-pill">
              <span class="mobile-avatar-badge">{{ (adminNickname || '管')[0] }}</span>
              <span class="mobile-user-text">{{ adminNickname || '管理员' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="mobile-admin-dropdown">
                <el-dropdown-item @click="goToFrontend">
                  <el-icon><HomeFilled /></el-icon>返回官网
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout" style="color: #f56c6c;">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Odometer, Picture, Document, Bell, Suitcase,
  User, Reading, ChatDotRound, Setting,
  Fold, Expand, Close, HomeFilled, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const adminNickname = ref(localStorage.getItem('adminNickname') || '')
const isMobile = ref(false)
const isMobileMenuOpen = ref(false)

const checkScreen = () => {
  isMobile.value = window.innerWidth < 992
  if (!isMobile.value) {
    isMobileMenuOpen.value = false
  }
}

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}

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
  if (path.includes('/admin/resumes')) return 'resumes'
  if (path.includes('/admin/feedback')) return 'feedback'
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
    'lab-info': '站点设置',
    resumes: '简历管理',
    feedback: '意见收集'
  }
  return titles[activeMenu.value] || '控制台'
})

const handleMenuSelect = (index) => {
  router.push(`/admin/${index}`)
  if (isMobile.value) {
    isMobileMenuOpen.value = false
  }
}

const handleLogout = () => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminNickname')
  router.push('/admin/login')
}

const goToFrontend = () => {
  router.push('/')
}

onMounted(() => {
  checkScreen()
  window.addEventListener('resize', checkScreen)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', checkScreen)
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f0f2f5;
  position: relative;
  width: 100%;
  overflow-x: hidden;
}

/* 侧边栏桌面端 */
.admin-sidebar {
  width: 220px;
  background-color: #0C3D70;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  transition: transform 0.28s cubic-bezier(0.16, 1, 0.3, 1);
}

.sidebar-header {
  padding: 20px 18px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sidebar-title {
  color: #ffffff;
  font-size: 19px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

.sidebar-subtitle {
  color: #b0c4de;
  font-size: 12px;
  margin: 4px 0 0;
}

.sidebar-close-btn {
  background: transparent;
  border: none;
  color: #ffffff;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  opacity: 0.85;
}

.sidebar-close-btn:hover {
  opacity: 1;
  background: rgba(255, 255, 255, 0.1);
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
}

:deep(.sidebar-menu .el-menu-item) {
  height: 48px;
  line-height: 48px;
  font-size: 14px;
}

:deep(.sidebar-menu .el-menu-item.is-active) {
  background-color: rgba(255, 255, 255, 0.15) !important;
}

:deep(.sidebar-menu .el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

/* 主内容外壳 */
.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  position: relative;
}

.admin-header {
  height: 54px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.mobile-hamburger-btn {
  background: transparent;
  border: none;
  color: #0C3D70;
  cursor: pointer;
  padding: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  flex-shrink: 0;
}

.mobile-hamburger-btn:hover {
  background: #f0f4f8;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a3353;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.header-user {
  font-size: 14px;
  color: #556980;
}

/* 移动端头像胶囊 */
.mobile-user-pill {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 3px 8px 3px 4px;
  background: #f0f4f8;
  border-radius: 16px;
  cursor: pointer;
}

.mobile-avatar-badge {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #0C3D70;
  color: #ffffff;
  display: grid;
  place-items: center;
  font-size: 12px;
  font-weight: 700;
}

.mobile-user-text {
  font-size: 12px;
  color: #334e68;
  max-width: 70px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.admin-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  overflow-x: hidden;
}

/* 移动端侧边抽屉适配 */
@media (max-width: 991px) {
  .admin-sidebar.mobile-sidebar {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    width: 240px;
    z-index: 2100;
    transform: translateX(-100%);
    box-shadow: 4px 0 20px rgba(0, 0, 0, 0.25);
  }

  .admin-sidebar.mobile-sidebar-open {
    transform: translateX(0);
  }

  .sidebar-backdrop {
    position: fixed;
    inset: 0;
    background: rgba(12, 33, 58, 0.45);
    backdrop-filter: blur(2px);
    z-index: 2050;
    animation: fadeInBackdrop 0.2s ease-out;
  }

  @keyframes fadeInBackdrop {
    from { opacity: 0; }
    to { opacity: 1; }
  }

  .admin-header {
    height: 48px;
    padding: 0 12px;
  }

  .admin-content {
    padding: 10px 8px;
  }
}

/* 核心移动端通用适配（768px 以下覆盖所有管理子页面） */
@media (max-width: 768px) {
  /* 页面内边距与标题动作条 */
  :deep(.page-container) {
    padding: 10px 4px !important;
  }

  :deep(.page-header) {
    flex-direction: column !important;
    align-items: flex-start !important;
    gap: 10px !important;
    margin-bottom: 12px !important;
  }

  :deep(.page-header h2) {
    font-size: 17px !important;
  }

  :deep(.page-header-actions) {
    width: 100% !important;
    display: flex !important;
    flex-wrap: wrap !important;
    gap: 8px !important;
  }

  :deep(.page-header-actions .el-button) {
    flex: 1 1 calc(50% - 6px) !important;
    margin-left: 0 !important;
  }

  /* 表格防挤压：卡片内容横向自适应滑动 */
  :deep(.el-card) {
    border-radius: 8px !important;
  }

  :deep(.el-card__body) {
    padding: 10px 8px !important;
    overflow-x: auto !important;
  }

  :deep(.el-table) {
    width: 100% !important;
    font-size: 13px !important;
  }

  :deep(.el-table .el-table__cell) {
    padding: 8px 4px !important;
  }

  /* 控制台面板网格移动端自适应 */
  :deep(.stats-grid) {
    grid-template-columns: repeat(2, 1fr) !important;
    gap: 8px !important;
    margin-bottom: 20px !important;
  }

  :deep(.stat-card) {
    padding: 12px 10px !important;
    gap: 8px !important;
    border-radius: 6px !important;
  }

  :deep(.stat-icon) {
    width: 38px !important;
    height: 38px !important;
    border-radius: 8px !important;
  }

  :deep(.stat-number) {
    font-size: 20px !important;
  }

  :deep(.stat-label) {
    font-size: 11px !important;
  }

  :deep(.quick-actions) {
    padding: 14px 12px !important;
  }

  :deep(.actions-grid) {
    gap: 8px !important;
  }

  :deep(.actions-grid .action-btn) {
    flex: 1 1 calc(50% - 6px) !important;
    min-width: 0 !important;
    margin-left: 0 !important;
  }

  /* 意见反馈面板移动端自适应 */
  :deep(.feedback-stats) {
    grid-template-columns: repeat(2, 1fr) !important;
    gap: 8px !important;
  }

  :deep(.feedback-stats > div) {
    padding: 12px 10px !important;
  }

  :deep(.feedback-stats strong) {
    font-size: 20px !important;
  }

  :deep(.feedback-admin-heading) {
    flex-direction: column !important;
    align-items: flex-start !important;
    gap: 10px !important;
  }

  :deep(.feedback-filters) {
    gap: 8px !important;
  }

  :deep(.feedback-filters .el-select) {
    width: 100% !important;
    flex: 1 1 calc(50% - 6px) !important;
  }

  /* 弹窗在移动端自动居中、自适应宽度、防溢出 */
  :deep(.el-dialog) {
    width: min(94vw, 680px) !important;
    max-width: 95vw !important;
    margin: 16px auto !important;
    border-radius: 12px !important;
  }

  :deep(.el-dialog__header) {
    padding: 14px 14px 8px !important;
    margin-right: 0 !important;
  }

  :deep(.el-dialog__title) {
    font-size: 16px !important;
  }

  :deep(.el-dialog__body) {
    padding: 12px !important;
    max-height: 72vh !important;
    overflow-y: auto !important;
  }

  :deep(.el-dialog__footer) {
    padding: 10px 14px 14px !important;
  }

  /* 弹窗中的表单自适应上下堆叠，防被 label 挤爆 */
  :deep(.el-dialog .el-form-item) {
    margin-bottom: 14px !important;
  }

  :deep(.el-dialog .el-form-item__label) {
    float: none !important;
    display: block !important;
    text-align: left !important;
    margin-bottom: 4px !important;
    line-height: 1.3 !important;
    width: auto !important;
  }

  :deep(.el-dialog .el-form-item__content) {
    margin-left: 0 !important;
  }

  /* 分页组件在手机端换行居中 */
  :deep(.el-pagination) {
    flex-wrap: wrap !important;
    gap: 6px !important;
    justify-content: center !important;
  }

  :deep(.el-pagination .el-pagination__total),
  :deep(.el-pagination .el-pagination__jump) {
    margin: 0 !important;
    font-size: 12px !important;
  }
}
</style>
