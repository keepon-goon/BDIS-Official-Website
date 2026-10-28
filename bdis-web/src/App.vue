<template>
  <SplashScreen
    v-if="showSplash"
    @enter-home="startHomeReveal"
    @finish="onSplashFinish"
  />
  <div :class="['app-root-view', { 'home-emerging': isEmerging, 'splash-active': showSplash && !isEmerging }]">
    <template v-if="isStandaloneRoute">
      <RouterView />
    </template>
    <template v-else>
      <BaseLayout>
        <RouterView />
      </BaseLayout>
    </template>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import BaseLayout from './layouts/BaseLayout.vue'
import SplashScreen from './components/SplashScreen.vue'

const route = useRoute()
const isStandaloneRoute = computed(() => route.path.startsWith('/admin') || route.path.startsWith('/resume/'))
const showSplash = ref(true)
const isEmerging = ref(false)

const startHomeReveal = () => {
  isEmerging.value = true
}

const onSplashFinish = () => {
  showSplash.value = false
  setTimeout(() => {
    isEmerging.value = false
  }, 1800)
}
</script>

<style scoped>
.app-root-view {
  min-height: 100vh;
  width: 100%;
}

/* 当开屏刚开始时，首页保持隐蔽静候唤醒 */
.app-root-view.splash-active {
  opacity: 0;
}

/* 核心：出首页大屏时，各模块一点点浮现出来（放慢至 1.15s~1.35s，如晨光透射般柔和舒展） */
.app-root-view.home-emerging :deep(.layout-header) {
  animation: headerDrop 1.15s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.app-root-view.home-emerging :deep(.home-one-left .banner) {
  animation: sectionRise 1.35s 0.18s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.app-root-view.home-emerging :deep(.home-one-right) {
  animation: sectionRise 1.35s 0.32s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.app-root-view.home-emerging :deep(.home-one-left .search) {
  animation: sectionRise 1.35s 0.46s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.app-root-view.home-emerging :deep(.layout-footer) {
  animation: sectionRise 1.35s 0.55s cubic-bezier(0.16, 1, 0.3, 1) both;
}

/* 非首页通用平滑升入 */
.app-root-view.home-emerging :deep(.layout-main > :not(.home)) {
  animation: sectionRise 1.35s 0.22s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes headerDrop {
  0% {
    opacity: 0;
    transform: translateY(-20px);
    filter: blur(5px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
    filter: blur(0px);
  }
}

@keyframes sectionRise {
  0% {
    opacity: 0;
    transform: translateY(32px);
    filter: blur(8px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
    filter: blur(0px);
  }
}
</style>
