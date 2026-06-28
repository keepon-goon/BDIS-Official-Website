<template>
  <template v-if="isAdminRoute">
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
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
</script>
