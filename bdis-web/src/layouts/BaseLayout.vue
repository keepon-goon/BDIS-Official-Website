<!-- src/layouts/BaseLayout.vue -->
<template>
  <div class="layout">
    <header class="layout-header">
      <NavBar />
    </header>

    <main class="layout-main">
      <slot />
    </main>

    <footer class="layout-footer">
      <Footer />
    </footer>
  </div>
</template>

<script setup>
import { provide, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'

const route = useRoute()
const labInfo = ref(null)
const labInfoLoading = ref(false)

provide('siteLabInfo', { labInfo, labInfoLoading })

watch(() => route.path, async (path, previousPath) => {
  if (previousPath && path !== '/info/about' && path !== '/info/join') return
  if (labInfoLoading.value) return
  labInfoLoading.value = true
  try {
    const { data } = await axios.get('/api/lab/info')
    labInfo.value = data.code === 0 ? data.data : null
  } catch {
    labInfo.value = null
  } finally {
    labInfoLoading.value = false
  }
}, { immediate: true })
</script>

<style scoped>
.layout {
  min-height: 100vh;
  width: 100%;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background-color: white;
}

.layout-header {
  flex: 0 0 auto;
  width: 100%;
}

.layout-main {
  flex: 1 1 auto;
  width: 100%;
}

.layout-footer {
  width: 100%;
}
</style>
