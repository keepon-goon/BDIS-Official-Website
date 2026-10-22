<template>
  <div class="info-header-banner">
    <div class="info-header-container">
      <nav class="breadcrumb-trail" aria-label="面包屑导航">
        <router-link to="/" class="breadcrumb-link">首页</router-link>
        <el-icon class="breadcrumb-sep"><ArrowRight /></el-icon>
        <span class="breadcrumb-current">{{ route.meta.title }}</span>
      </nav>
      <div class="info-title-row">
        <h1 class="info-main-title">{{ route.meta.title }}</h1>
        <span class="info-title-badge">BDIS · CQUT</span>
      </div>
    </div>
  </div>
  <main class="info-content-body">
    <router-view />
  </main>
</template>

<script setup>
import { ArrowRight } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.info-header-banner {
  width: 100%;
  background: linear-gradient(135deg, #0C3D70 0%, #16569A 70%, #0284C7 100%);
  color: #FFFFFF;
  padding: 36px 0 32px;
  position: relative;
  overflow: hidden;
}

.info-header-banner::after {
  content: "";
  position: absolute;
  right: -50px;
  top: -50px;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.12) 0%, transparent 70%);
  pointer-events: none;
}

.info-header-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 32px;
  box-sizing: border-box;
}

.breadcrumb-trail {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  margin-bottom: 12px;
}

.breadcrumb-link {
  color: rgba(255, 255, 255, 0.75);
  text-decoration: none;
  transition: color 0.2s ease;
}

.breadcrumb-link:hover {
  color: #FFFFFF;
}

.breadcrumb-sep {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.breadcrumb-current {
  color: #FFFFFF;
  font-weight: 500;
}

.info-title-row {
  display: flex;
  align-items: baseline;
  gap: 16px;
}

.info-main-title {
  margin: 0;
  font-size: 32px;
  font-weight: 600;
  color: #FFFFFF;
  letter-spacing: -0.5px;
}

.info-title-badge {
  font-size: 12px;
  letter-spacing: 1px;
  color: rgba(255, 255, 255, 0.8);
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.info-content-body {
  width: 100%;
}

@media (max-width: 768px) {
  .info-header-banner {
    padding: 24px 0 20px;
  }

  .info-header-container {
    padding: 0 20px;
  }

  .info-main-title {
    font-size: 24px;
  }
}
</style>
