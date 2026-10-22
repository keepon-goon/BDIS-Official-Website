<!-- src/components/NavBar.vue -->
<template>
  <header class="nav-wrapper" :class="{ 'is-scrolled': isScrolled }">
    <div class="nav-container">
      <!-- 左：Logo 与工作室名称 -->
      <div class="nav-left" @click="goHome">
        <img class="nav-logo" :src="logoImg" alt="BDIS 大数据智能工作室" />
      </div>

      <!-- 右侧：上层快捷链接 + 下层主菜单与搜索 -->
      <div class="nav-right">
        <!-- 顶部快捷导航与外部入口 -->
        <div class="nav-right-top">
          <a class="quick-chip" href="https://www.cqut.edu.cn/" target="_blank" rel="noopener noreferrer">
            <span>University</span>
          </a>
          <a class="quick-chip" href="https://cs.cqut.edu.cn/" target="_blank" rel="noopener noreferrer">
            <span>School</span>
          </a>
          <a class="quick-chip" href="https://www.tipdm.com/" target="_blank" rel="noopener noreferrer">
            <span>Partners</span>
          </a>
          <RouterLink class="quick-chip" to="/wip">
            <span>Members</span>
          </RouterLink>
          <RouterLink class="quick-chip lang-btn" to="/wip">
            <span>EN</span>
          </RouterLink>
          <button class="quick-chip admin-btn" @click="router.push('/admin/login')">
            <el-icon class="admin-icon"><User /></el-icon>
            <span>Admin</span>
          </button>
        </div>

        <!-- 底部主菜单与检索 -->
        <div class="nav-right-bottom">
          <nav class="nav-menu">
            <div
              v-for="item in mainMenus"
              :key="item.index"
              class="nav-item"
              :class="{ 'is-active': activeMain === item.index }"
              @click="handleMenuClick(item)"
            >
              <span class="nav-item-text">{{ item.label }}</span>
              <span class="nav-item-indicator"></span>
            </div>
          </nav>

          <!-- 搜索按钮 -->
          <button class="nav-search-btn" title="全站检索" @click="handleSearch">
            <el-icon class="search-icon"><Search /></el-icon>
          </button>

          <!-- 移动端汉堡菜单触发器 -->
          <button class="nav-mobile-toggle" @click="mobileMenuVisible = !mobileMenuVisible">
            <el-icon><Menu /></el-icon>
          </button>
        </div>
      </div>
    </div>

    <!-- 移动端抽屉导航 -->
    <el-drawer
      v-model="mobileMenuVisible"
      title="BDIS 导航菜单"
      direction="rtl"
      size="280px"
      :with-header="true"
    >
      <div class="mobile-menu-list">
        <div
          v-for="item in mainMenus"
          :key="item.index"
          class="mobile-menu-item"
          :class="{ 'is-active': activeMain === item.index }"
          @click="handleMenuClick(item); mobileMenuVisible = false"
        >
          {{ item.label }}
        </div>
        <div class="mobile-menu-divider"></div>
        <div class="mobile-menu-item" @click="router.push('/search'); mobileMenuVisible = false">
          <el-icon><Search /></el-icon> 全站检索
        </div>
        <div class="mobile-menu-item" @click="router.push('/admin/login'); mobileMenuVisible = false">
          <el-icon><User /></el-icon> 管理后台
        </div>
      </div>
    </el-drawer>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Search, User, Menu } from '@element-plus/icons-vue'
import logoImg from '../assets/logo.jpg'
import { useRouter, useRoute, RouterLink } from 'vue-router'

const router = useRouter()
const route = useRoute()
const mobileMenuVisible = ref(false)
const isScrolled = ref(false)

const mainMenus = [
  { index: 'home', label: '首页', path: "/" },
  { index: 'about', label: '关于我们', path: "/info/about" },
  { index: 'research', label: '项目进展', path: "/info/research" },
  { index: 'platform', label: '创新竞赛', path: "/info/platform" },
  { index: 'news', label: '动态公告', path: "/info/news" },
  { index: 'education', label: '学习资料', path: "/info/education" },
  { index: 'join', label: '加入我们', path: "/info/join" },
]

const activeMain = computed(() => {
  const path = route.path
  if (path === '/') return 'home'
  const matched = mainMenus.find(m => path.startsWith(m.path) && m.path !== '/')
  return matched ? matched.index : 'home'
})

const handleMenuClick = (item) => {
  router.push(item.path)
}

const goHome = () => {
  router.push('/')
}

const handleSearch = () => {
  router.push('/search')
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.nav-wrapper {
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
  background: rgba(255, 255, 255, 0.94);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.nav-wrapper.is-scrolled {
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 4px 20px -2px rgba(12, 61, 112, 0.08);
  border-bottom-color: rgba(203, 213, 225, 0.9);
}

.nav-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 10px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
}

/* 左侧 Logo */
.nav-left {
  flex: 0 0 auto;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: transform 0.25s ease;
}

.nav-left:hover {
  transform: translateY(-1px);
}

.nav-logo {
  height: 52px;
  width: auto;
  object-fit: contain;
}

/* 右侧菜单与快捷栏 */
.nav-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

/* 顶部快捷条 */
.nav-right-top {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quick-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  background-color: #F1F5F9;
  border-radius: 6px;
  text-decoration: none;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  line-height: 1.4;
}

.quick-chip:hover {
  background-color: #E2E8F0;
  color: #0C3D70;
  transform: translateY(-1px);
}

.quick-chip.lang-btn {
  background: #0C3D70;
  color: #FFFFFF;
}

.quick-chip.lang-btn:hover {
  background: #16569A;
  color: #FFFFFF;
}

.quick-chip.admin-btn {
  background: rgba(12, 61, 112, 0.08);
  color: #0C3D70;
  border: 1px solid rgba(12, 61, 112, 0.15);
}

.quick-chip.admin-btn:hover {
  background: rgba(12, 61, 112, 0.16);
  border-color: rgba(12, 61, 112, 0.3);
}

.admin-icon {
  font-size: 13px;
}

/* 底部主导航 */
.nav-right-bottom {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-item {
  position: relative;
  padding: 8px 16px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.nav-item-text {
  font-size: 16px;
  font-weight: 500;
  color: #334155;
  transition: color 0.25s ease;
}

.nav-item-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%) scaleX(0);
  width: 24px;
  height: 3px;
  background: linear-gradient(90deg, #0C3D70 0%, #0284C7 100%);
  border-radius: 2px;
  transition: transform 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.nav-item:hover {
  background-color: rgba(12, 61, 112, 0.04);
}

.nav-item:hover .nav-item-text {
  color: #0C3D70;
}

.nav-item.is-active .nav-item-text {
  color: #0C3D70;
  font-weight: 600;
}

.nav-item.is-active .nav-item-indicator {
  transform: translateX(-50%) scaleX(1);
}

/* 搜索按钮 */
.nav-search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: 1px solid #E2E8F0;
  background: #F8FAFC;
  color: #475569;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.nav-search-btn:hover {
  background: #0C3D70;
  color: #FFFFFF;
  border-color: #0C3D70;
  transform: translateY(-1px) scale(1.04);
  box-shadow: 0 4px 12px rgba(12, 61, 112, 0.2);
}

.search-icon {
  font-size: 17px;
}

/* 移动端菜单控制 */
.nav-mobile-toggle {
  display: none;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 8px;
  border: 1px solid #E2E8F0;
  background: #F8FAFC;
  font-size: 20px;
  color: #334155;
  cursor: pointer;
}

.mobile-menu-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mobile-menu-item {
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #334155;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.2s ease;
}

.mobile-menu-item:hover,
.mobile-menu-item.is-active {
  background: rgba(12, 61, 112, 0.08);
  color: #0C3D70;
  font-weight: 600;
}

.mobile-menu-divider {
  height: 1px;
  background: #E2E8F0;
  margin: 10px 0;
}

/* 响应式断点 */
@media (max-width: 1024px) {
  .nav-menu {
    gap: 2px;
  }

  .nav-item {
    padding: 8px 10px;
  }

  .nav-item-text {
    font-size: 15px;
  }
}

@media (max-width: 860px) {
  .nav-right-top {
    display: none;
  }

  .nav-menu {
    display: none;
  }

  .nav-mobile-toggle {
    display: flex;
  }

  .nav-logo {
    height: 44px;
  }

  .nav-container {
    padding: 12px 20px;
  }
}
</style>
