<template>
  <SplashScreen v-if="showSplash" @finish="showSplash = false" />
  <template v-if="isStandaloneRoute">
    <RouterView />
  </template>
  <template v-else>
    <BaseLayout>
      <RouterView />
    </BaseLayout>
  </template>
</template>

<script setup>
import { ref, computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import BaseLayout from './layouts/BaseLayout.vue'
import SplashScreen from './components/SplashScreen.vue'

const route = useRoute()
const isStandaloneRoute = computed(() => route.path.startsWith('/admin') || route.path.startsWith('/resume/'))
const showSplash = ref(true)
</script>
