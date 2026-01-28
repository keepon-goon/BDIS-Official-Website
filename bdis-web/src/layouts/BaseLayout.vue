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
import NavBar from '../components/NavBar.vue'
import Footer from '../components/Footer.vue'
</script>

<style scoped>
.layout {
  min-height: 100vh;
  min-width: 100vw;
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
