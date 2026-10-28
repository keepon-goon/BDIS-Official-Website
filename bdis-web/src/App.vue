<template>
  <SplashScreen
    v-if="showSplash"
    @enter-home="startHomeReveal"
    @finish="onSplashFinish"
  />
  <div :class="['app-root-view', { 'splash-active': showSplash && !isEmerging }]">
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
}
</script>

<style scoped>
.app-root-view {
  min-height: 100vh;
  width: 100%;
  transition: opacity 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}

/* 当开屏刚开始时，首页保持隐蔽静候唤醒 */
.app-root-view.splash-active {
  opacity: 0;
}
</style>
