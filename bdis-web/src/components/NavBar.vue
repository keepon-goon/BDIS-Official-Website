<template>
  <header ref="navElement" class="nav" :class="{ 'is-compact': isMobileViewport || isDetached, 'is-detached': isDetached }"
    @keydown.esc.stop.prevent="closeMobileMenu(true)">
    <router-link to="/" class="nav-left" aria-label="BDIS 首页" @click="closeMobileMenu()">
      <img class="nav-logo" :src="logoImg" alt="BDIS 大数据智能工作室" />
    </router-link>

    <div class="nav-mobile-actions">
      <button type="button" class="nav-icon-button" aria-label="搜索" @click="handleSearch">
        <el-icon><Search /></el-icon>
      </button>
      <span ref="menuAnchor" class="nav-menu-origin" :class="{ 'is-collapsed': isDetached }" :inert="isDetached" :aria-hidden="isDetached">
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
      </span>
    </div>

    <div class="nav-float-actions" :class="{ 'is-visible': isDetached }" :inert="!isDetached" :aria-hidden="!isDetached">
      <button ref="floatingMenuToggle" type="button" class="nav-menu-toggle nav-float-menu"
        :aria-label="mobileMenuOpen ? '收起导航菜单' : '展开导航菜单'" :aria-expanded="mobileMenuOpen" aria-controls="site-navigation"
        @click="mobileMenuOpen = !mobileMenuOpen">
        <el-icon><Close v-if="mobileMenuOpen" /><svg v-else viewBox="0 0 24 24" aria-hidden="true"><path d="M4 6h16M4 12h16M4 18h16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" /></svg></el-icon><span>{{ mobileMenuOpen ? '收起' : '菜单' }}</span>
      </button>
      <button ref="backToTopToggle" type="button" class="nav-back-top" aria-label="回到页面顶部" @click="scrollToTop">
        <el-icon><Top /></el-icon><span>回顶</span>
      </button>
    </div>

    <div
      id="site-navigation"
      class="nav-right"
      :class="{ 'is-open': mobileMenuOpen }"
      :inert="(isMobileViewport || isDetached) && !mobileMenuOpen"
    >
      <nav class="nav-right-top" aria-label="相关链接" :style="{ '--menu-offset': mainMenus.length }">
        <a href="https://www.cqut.edu.cn/" class="nav-utility-link" style="--menu-order: 0">University</a>
        <a href="https://cs.cqut.edu.cn/" class="nav-utility-link" style="--menu-order: 1">School</a>
        <a href="https://www.tipdm.com/" class="nav-utility-link" style="--menu-order: 2">Partners</a>
        <router-link to="/wip" class="nav-utility-link lang-btn" style="--menu-order: 3" @click="closeMobileMenu()">EN</router-link>
        <router-link to="/admin/login" class="nav-utility-link admin-btn" style="--menu-order: 4" @click="closeMobileMenu()">Admin</router-link>
      </nav>
      <div class="nav-right-foot">
        <nav class="nav-menu-right" aria-label="主导航">
          <router-link
            v-for="(item, menuIndex) in mainMenus"
            :key="item.index"
            :to="item.path"
            class="nav-menu-link"
            :class="{ 'is-active': activeMain === item.index }"
            :style="{ '--menu-order': menuIndex }"
            :aria-current="activeMain === item.index ? 'page' : undefined"
            @mouseenter="prefetchRoute(item.index)"
            @focus="prefetchRoute(item.index)"
            @touchstart.passive="prefetchRoute(item.index)"
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
import { Search, Close, Top } from '@element-plus/icons-vue'
import logoImg from '../assets/logo.jpg'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const mobileMenuOpen = ref(false)
const isMobileViewport = ref(false)
const navElement = ref(null)
const menuToggle = ref(null)
const menuAnchor = ref(null)
const floatingMenuToggle = ref(null)
const backToTopToggle = ref(null)
const isDetached = ref(false)
let mobileViewport
let reduceMotion
let scrollFrame = 0

const prefetchMap = {
  about: () => import('@/components/info/About.vue'),
  research: () => import('@/components/info/Research.vue'),
  platform: () => import('@/components/info/Platform.vue'),
  news: () => import('@/components/info/News.vue'),
  education: () => import('@/components/info/Education.vue'),
  join: () => import('@/components/info/Join.vue'),
  feedback: () => import('@/views/FeedbackView.vue'),
  qa: () => import('@/views/FeedbackQaView.vue'),
}

const prefetched = new Set()
const prefetchRoute = (key) => {
  if (key && prefetchMap[key] && !prefetched.has(key)) {
    prefetched.add(key)
    prefetchMap[key]().catch(() => {})
  }
}

watch(mobileMenuOpen, (isOpen) => {
  if (isOpen) {
    const prefetchIdle = window.requestIdleCallback || ((cb) => setTimeout(cb, 100))
    prefetchIdle(() => {
      Object.keys(prefetchMap).forEach(key => prefetchRoute(key))
    })
  }
})

const mainMenus = [
  { index: 'home', label: '首页', path: "/" },
  { index: 'about', label: '关于我们', path: "/info/about" },
  { index: 'research', label: '项目进展', path: "/info/research" },
  { index: 'platform', label: '创新竞赛', path: "/info/platform" },
  { index: 'news', label: '动态公告', path: "/info/news" },
  { index: 'education', label: '学习资料', path: "/info/education" },
  { index: 'join', label: '加入我们', path: "/info/join" },
  { index: 'feedback', label: '匿名信箱', path: '/feedback' },
  { index: 'qa', label: 'Q&A', path: '/feedback/qa' },
]

const activeMain = computed(() => {
  const path = route.path
  if (path === '/') return 'home'
  const matched = mainMenus.find(item => item.path === path)
    || mainMenus.find(item => item.index === route.meta.tab
    || (item.path !== '/' && path.startsWith(item.path)))
  return matched ? matched.index : ''
})

const closeMobileMenu = (restoreFocus = false) => {
  mobileMenuOpen.value = false
  if (restoreFocus) nextTick(() => (isDetached.value ? floatingMenuToggle.value : menuToggle.value)?.focus())
}

const updateDetached = () => {
  scrollFrame = 0
  const anchor = isMobileViewport.value ? menuAnchor.value : navElement.value
  if (!anchor) return
  const bounds = anchor.getBoundingClientRect()
  const detached = isDetached.value ? bounds.top < 0 : bounds.bottom <= 0
  if (detached === isDetached.value) return
  const activeElement = document.activeElement
  const restoreToggleFocus = activeElement === menuToggle.value || activeElement === floatingMenuToggle.value || activeElement === backToTopToggle.value
  isDetached.value = detached
  if (!detached && !isMobileViewport.value) closeMobileMenu()
  if (restoreToggleFocus) nextTick(() => {
    const target = detached ? floatingMenuToggle.value : isMobileViewport.value ? menuToggle.value : navElement.value?.querySelector('.nav-left')
    target?.focus({ preventScroll: true })
  })
}

const handleScroll = () => {
  if (!scrollFrame) scrollFrame = requestAnimationFrame(updateDetached)
}

const scrollToTop = () => {
  closeMobileMenu()
  window.scrollTo({ top: 0, behavior: reduceMotion.matches ? 'auto' : 'smooth' })
}

const handleOutsidePress = (event) => {
  if (mobileMenuOpen.value && !navElement.value?.contains(event.target)) {
    closeMobileMenu()
  }
}

const handleViewportChange = () => {
  isMobileViewport.value = mobileViewport.matches
  closeMobileMenu()
  handleScroll()
}

const handleSearch = () => {
  closeMobileMenu()
  router.push('/search')
}

watch(() => route.fullPath, () => { closeMobileMenu(); nextTick(handleScroll) })

onMounted(() => {
  // Keep this breakpoint in sync with the responsive styles below.
  mobileViewport = window.matchMedia('(max-width: 1279px)')
  isMobileViewport.value = mobileViewport.matches
  reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)')
  mobileViewport.addEventListener('change', handleViewportChange)
  document.addEventListener('pointerdown', handleOutsidePress)
  window.addEventListener('scroll', handleScroll, { passive: true })
  window.addEventListener('resize', handleScroll, { passive: true })
  handleScroll()
})

onBeforeUnmount(() => {
  mobileViewport?.removeEventListener('change', handleViewportChange)
  document.removeEventListener('pointerdown', handleOutsidePress)
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', handleScroll)
  cancelAnimationFrame(scrollFrame)
})
</script>

<style scoped>
.nav {
  --ease-out: cubic-bezier(0.23, 1, 0.32, 1);
  --ease-out-back: cubic-bezier(0.34, 1.56, 0.64, 1);
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

.nav-menu-origin {
  display: inline-flex;
}

.nav-menu-origin .nav-menu-toggle {
  opacity: 1;
  visibility: visible;
  transform-origin: 50% 60%;
  transform: perspective(180px) rotateX(0deg) scale(1);
  clip-path: polygon(0 0, 100% 0, 100% 100%, 0 100%);
  transition: transform 260ms var(--ease-out), clip-path 260ms var(--ease-out), opacity 180ms ease, visibility 0s;
}

.nav-menu-origin.is-collapsed .nav-menu-toggle {
  opacity: 0;
  visibility: hidden;
  transform: perspective(180px) rotateX(52deg) scale(.08, .04);
  clip-path: polygon(50% 48%, 50% 48%, 50% 62%, 50% 62%);
  transition-delay: 0s, 0s, 0s, 260ms;
}

.nav-menu-toggle,
.nav-back-top {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 44px;
  padding: 0 10px;
  border: 1px solid #0C3D70;
  border-radius: 8px;
  background-color: #0C3D70;
  color: #fff;
  font-size: 14px;
  white-space: nowrap;
}

.nav-float-actions {
  position: fixed;
  top: max(14px, env(safe-area-inset-top));
  right: max(16px, env(safe-area-inset-right));
  z-index: 40;
  display: flex;
  gap: 8px;
  padding: 6px;
  border: 1px solid #dce7f2;
  border-radius: 16px;
  background: rgba(255, 255, 255, .96);
  box-shadow: 0 6px 24px rgba(12, 61, 112, .14);
  transform-origin: center;
  transform: scale(.08);
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
  transition: transform 280ms var(--ease-out), opacity 180ms ease, visibility 0s 280ms;
}

.nav-float-actions.is-visible {
  transform: scale(1);
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
  transition-delay: 0s;
}

.nav-float-actions .nav-float-menu,
.nav-float-actions .nav-back-top {
  transition: transform 280ms var(--ease-out), background-color 160ms ease;
}

.nav-float-actions .nav-float-menu { transform: translateX(38px) scale(.7); }
.nav-float-actions .nav-back-top { transform: translateX(-38px) scale(.7); background: #edf4fb; color: #0C3D70; border-color: #dce7f2; }
.nav-float-actions.is-visible .nav-float-menu,
.nav-float-actions.is-visible .nav-back-top { transform: translateX(0) scale(1); }
.nav-back-top .el-icon { font-size: 20px; }
.nav.is-compact.is-detached .nav-right { position: fixed; top: max(82px, calc(env(safe-area-inset-top) + 68px)); right: max(16px, env(safe-area-inset-right)); }

@media (hover: hover) and (pointer: fine) {
  .nav-float-menu:hover { background: #164f86; }
  .nav-back-top:hover { background: #dceaf7; }
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

}

.nav.is-compact .nav-right {
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
    visibility: hidden;
    opacity: 0;
    pointer-events: none;
    transform: translateY(-8px) scale(0.97);
    transform-origin: top right;
    transition:
      opacity 160ms var(--ease-out),
      transform 160ms var(--ease-out),
      visibility 0s 160ms;
  }

.nav.is-compact .nav-right.is-open {
    visibility: visible;
    opacity: 1;
    pointer-events: auto;
    transform: translateY(0) scale(1);
    transition-duration: 180ms, 180ms, 0s;
    transition-delay: 0s;
  }

.nav.is-compact .nav-menu-link,
.nav.is-compact .nav-utility-link {
    opacity: 0;
    transform: perspective(500px) translateY(-30%) rotateX(-14deg) scale(0.97);
    transform-origin: top center;
    transition:
      opacity 120ms var(--ease-out),
      transform 160ms var(--ease-out),
      background-color 200ms ease;
  }

.nav.is-compact .nav-right.is-open .nav-menu-link,
.nav.is-compact .nav-right.is-open .nav-utility-link {
    opacity: 1;
    transform: perspective(500px) translateY(0) rotateX(0) scale(1);
    transition:
      opacity 140ms var(--ease-out) calc((var(--menu-offset, 0) + var(--menu-order, 0)) * 30ms),
      transform 220ms var(--ease-out-back) calc((var(--menu-offset, 0) + var(--menu-order, 0)) * 30ms),
      background-color 200ms ease;
  }

.nav.is-compact .nav-right-foot {
    display: block;
    order: 0;
  }

.nav.is-compact .nav-menu-right {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 0;
  }

.nav.is-compact .nav-menu-link {
    min-height: 44px;
    padding: 0 14px;
    border-bottom: 0;
    border-radius: 4px;
    background-color: transparent;
    font-size: 16px;
  }

.nav.is-compact .nav-menu-link:hover {
    background-color: #f3f6fa;
  }

.nav.is-compact .nav-menu-link.is-active {
    background-color: #eaf1f8;
    color: #0C3D70;
  }

.nav.is-compact .nav-right-top {
    display: flex;
    flex-direction: column;
    order: 1;
    gap: 0;
    padding-top: 8px;
    border-top: 1px solid #e4e7ed;
  }

.nav.is-compact .nav-right-top .nav-utility-link {
    justify-content: flex-start;
    min-height: 44px;
    padding: 0 14px;
    border-radius: 4px;
    background-color: transparent;
    color: #52667a;
    font-size: 13px;
  }

.nav.is-compact .nav-right-top .nav-utility-link:hover {
    background-color: #f3f6fa;
    color: #0C3D70;
  }

.nav.is-compact .nav-desktop-search {
    display: none;
  }

@media (prefers-reduced-motion: reduce) {
  .nav.is-compact .nav-right,
  .nav.is-compact .nav-right.is-open {
    transform: none;
    transition: opacity 100ms ease, visibility 0s 100ms;
  }

  .nav.is-compact .nav-right.is-open {
    transition-delay: 0s;
  }

  .nav.is-compact .nav-menu-link,
  .nav.is-compact .nav-utility-link,
  .nav.is-compact .nav-right.is-open .nav-menu-link,
  .nav.is-compact .nav-right.is-open .nav-utility-link {
    transform: none;
    transition: opacity 100ms ease, background-color 200ms ease;
  }

  .nav-menu-origin .nav-menu-toggle,
  .nav-menu-origin.is-collapsed .nav-menu-toggle,
  .nav-float-actions,
  .nav-float-actions.is-visible,
  .nav-float-actions .nav-float-menu,
  .nav-float-actions .nav-back-top,
  .nav-float-actions.is-visible .nav-float-menu,
  .nav-float-actions.is-visible .nav-back-top {
    transform: none;
    clip-path: none;
    transition: opacity 100ms ease, visibility 0s 100ms;
  }

  .nav-float-actions.is-visible,
  .nav-menu-origin:not(.is-collapsed) .nav-menu-toggle { transition-delay: 0s; }
}
</style>
