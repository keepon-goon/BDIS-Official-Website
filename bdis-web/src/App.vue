<!-- src/App.vue -->
<script setup>
import { RouterView } from 'vue-router'
import BaseLayout from './layouts/BaseLayout.vue'
</script>

<template>
  <BaseLayout>
    <RouterView />
  </BaseLayout>
</template>
