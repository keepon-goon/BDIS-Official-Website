<template>
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
import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import BaseLayout from './layouts/BaseLayout.vue'

const route = useRoute()
const isStandaloneRoute = computed(() => route.path.startsWith('/admin') || route.path.startsWith('/resume/'))
</script>
