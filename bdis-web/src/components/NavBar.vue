<template>
  <header ref="navElement" class="nav" @keydown.esc.stop.prevent="closeMobileMenu(true)">
    <router-link to="/" class="nav-left" aria-label="BDIS 首页" @click="closeMobileMenu()">
      <img class="nav-logo" :src="logoImg" alt="BDIS 大数据智能工作室" />
    </router-link>

    <div class="nav-mobile-actions">
      <button type="button" class="nav-icon-button" aria-label="搜索" @click="handleSearch">
        <el-icon><Search /></el-icon>
      </button>
      <button
        ref="menuToggle"
        type="button"
        class="nav-menu-toggle"
        :aria-label="mobileMenuOpen ? '收起导航菜单' : '展开导航菜单'"
        :aria-expanded="mobileMenuOpen"
        aria-controls="site-navigation"
        @click="mobileMenuOpen = !mobileMenuOpen"
      >
        <el-icon>
          <Close v-if="mobileMenuOpen" />
          <svg v-else viewBox="0 0 24 24" aria-hidden="true">
            <path d="M4 6h16M4 12h16M4 18h16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
          </svg>
        </el-icon>
        <span>{{ mobileMenuOpen ? '收起' : '菜单' }}</span>
      </button>
    </div>

    <div id="site-navigation" class="nav-right" :class="{ 'is-open': mobileMenuOpen }">
      <nav class="nav-right-top" aria-label="相关链接">
        <a href="https://www.cqut.edu.cn/" class="nav-utility-link">University</a>
        <a href="https://cs.cqut.edu.cn/" class="nav-utility-link">School</a>
        <a href="https://www.tipdm.com/" class="nav-utility-link">Partners</a>
        <router-link to="/wip" class="nav-utility-link" @click="closeMobileMenu()">Members</router-link>
        <router-link to="/wip" class="nav-utility-link lang-btn" @click="closeMobileMenu()">EN</router-link>
        <router-link to="/admin/login" class="nav-utility-link admin-btn" @click="closeMobileMenu()">Admin</router-link>
      </nav>
      <div class="nav-right-foot">
        <nav class="nav-menu-right" aria-label="主导航">
          <router-link
            v-for="item in mainMenus"
            :key="item.index"
            :to="item.path"
            class="nav-menu-link"
            :class="{ 'is-active': activeMain === item.index }"
            :aria-current="activeMain === item.index ? 'page' : undefined"
            @click="closeMobileMenu()"
          >
            {{ item.label }}
          </router-link>
        </nav>
        <button type="button" class="nav-icon-button nav-desktop-search" aria-label="搜索" @click="handleSearch">
          <el-icon><Search /></el-icon>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { Search, Close } from '@element-plus/icons-vue'
import logoImg from '../assets/logo.jpg'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const mobileMenuOpen = ref(false)
const navElement = ref(null)
const menuToggle = ref(null)
let mobileViewport

const mainMenus = [
  { index: 'home', label: '首页', path: "/" },
  { index: 'about', label: '关于我们', path: "/info/about" },
  { index: 'research', label: '项目进展', path: "/info/research" },
  { index: 'platform', label: '创新竞赛', path: "/info/platform" },
  { index: 'news', label: '动态公告', path: "/info/news" },
  { index: 'education', label: '学习资料', path: "/info/education" },
  { index: 'join', label: '加入我们', path: "/info/join" },
  { index: 'feedback', label: '匿名信箱', path: '/feedback' },
]

const activeMain = computed(() => {
  const path = route.path
  if (path === '/') return 'home'
  const matched = mainMenus.find(item => item.index === route.meta.tab
    || (item.path !== '/' && path.startsWith(item.path)))
  return matched ? matched.index : ''
})

const closeMobileMenu = (restoreFocus = false) => {
  mobileMenuOpen.value = false
  if (restoreFocus) nextTick(() => menuToggle.value?.focus())
}

const handleOutsidePress = (event) => {
  if (mobileMenuOpen.value && !navElement.value?.contains(event.target)) {
    closeMobileMenu()
  }
}

const handleViewportChange = () => {
  closeMobileMenu()
}

const handleSearch = () => {
  closeMobileMenu()
  router.push('/search')
}

watch(() => route.fullPath, () => closeMobileMenu())

onMounted(() => {
  // Keep this breakpoint in sync with the responsive styles below.
  mobileViewport = window.matchMedia('(max-width: 1279px)')
  mobileViewport.addEventListener('change', handleViewportChange)
  document.addEventListener('pointerdown', handleOutsidePress)
})

onBeforeUnmount(() => {
  mobileViewport?.removeEventListener('change', handleViewportChange)
  document.removeEventListener('pointerdown', handleOutsidePress)
})
</script>

<style scoped>
.nav {
  position: relative;
  z-index: 20;
  display: flex;
  align-items: center;
  gap: 24px;
  height: 135px;
  width: 100%;
  padding: 0 clamp(20px, 2.2vw, 32px);
  box-sizing: border-box;
  background-color: #ffffff;
}

.nav-left {
  display: block;
  flex: 0 0 auto;
  line-height: 0;
}

.nav-logo {
  display: block;
  height: 66px;
  width: auto;
  max-width: 100%;
  object-fit: contain;
}

.nav-right {
  min-width: 0;
  margin-left: auto;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 16px;
}

.nav-right-foot {
  display: flex;
  align-items: center;
  gap: clamp(12px, 2vw, 30px);
}

.nav-right-top {
  display: flex;
  gap: 2px;
}

.nav-utility-link {
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  min-height: 36px;
  padding: 0 16px;
  background-color: #f1f1f1;
  color: #23415A;
  font-size: 13px;
  text-decoration: none;
}

.nav-right-top .lang-btn {
  background-color: #0C3D70;
  color: white;
}

.nav-right-top .admin-btn {
  background-color: rgba(12, 61, 112, 0.2);
  color: #0C3D70;
}

.nav-menu-right {
  display: flex;
  align-items: center;
  gap: clamp(12px, 2vw, 30px);
}

.nav-menu-link {
  display: flex;
  align-items: center;
  box-sizing: border-box;
  min-height: 44px;
  border-bottom: 2px solid transparent;
  font-size: 18px;
  font-weight: 550;
  white-space: nowrap;
  text-decoration: none;
  color: #0C3D70;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.nav-menu-link:hover,
.nav-menu-link.is-active {
  border-bottom-color: #0C3D70;
  color: #0C3D70;
}

.nav-icon-button,
.nav-menu-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  box-sizing: border-box;
  height: 44px;
  min-width: 44px;
  padding: 0;
  border: 1px solid #e4e7ed;
  border-radius: 50%;
  background-color: #f1f1f1;
  color: #0C3D70;
  cursor: pointer;
}

.nav-icon-button .el-icon,
.nav-menu-toggle .el-icon {
  font-size: 20px;
}

.nav a:focus-visible,
.nav button:focus-visible {
  outline: 2px solid #409eff;
  outline-offset: 3px;
}

.nav button:focus:not(:focus-visible) {
  outline: none;
}

.nav-mobile-actions {
  display: none;
}

@media (max-width: 1279px) {
  .nav {
    flex-wrap: wrap;
    gap: 0;
    height: auto;
    min-height: 76px;
    padding: 14px 16px;
  }

  .nav-left {
    flex: 1 1 0;
    min-width: 0;
  }

  .nav-logo {
    width: 220px;
    height: auto;
    max-height: 48px;
    object-position: left center;
  }

  .nav-mobile-actions {
    display: flex;
    align-items: center;
    flex: 0 0 auto;
    gap: 8px;
    margin-left: 12px;
  }

  .nav-menu-toggle {
    gap: 6px;
    padding: 0 10px;
    border-color: #0C3D70;
    border-radius: 8px;
    background-color: #0C3D70;
    color: #fff;
    font-size: 14px;
  }

  .nav-right {
    display: none;
    position: absolute;
    top: calc(100% - 2px);
    right: 16px;
    align-items: stretch;
    gap: 8px;
    box-sizing: border-box;
    width: min(288px, calc(100% - 32px));
    max-height: calc(100vh - 92px);
    max-height: calc(100dvh - 92px);
    margin: 0;
    padding: 8px;
    overflow-y: auto;
    overscroll-behavior: contain;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 10px 28px rgba(12, 61, 112, 0.16);
  }

  .nav-right.is-open {
    display: flex;
  }

  .nav-right-foot {
    display: block;
    order: 0;
  }

  .nav-menu-right {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 0;
  }

  .nav-menu-link {
    min-height: 44px;
    padding: 0 14px;
    border-bottom: 0;
    border-radius: 4px;
    background-color: transparent;
    font-size: 16px;
  }

  .nav-menu-link:hover {
    background-color: #f3f6fa;
  }

  .nav-menu-link.is-active {
    background-color: #eaf1f8;
    color: #0C3D70;
  }

  .nav-right-top {
    display: flex;
    flex-direction: column;
    order: 1;
    gap: 0;
    padding-top: 8px;
    border-top: 1px solid #e4e7ed;
  }

  .nav-right-top .nav-utility-link {
    justify-content: flex-start;
    min-height: 44px;
    padding: 0 14px;
    border-radius: 4px;
    background-color: transparent;
    color: #52667a;
    font-size: 13px;
  }

  .nav-right-top .nav-utility-link:hover {
    background-color: #f3f6fa;
    color: #0C3D70;
  }

  .nav-desktop-search {
    display: none;
  }
}
</style>
