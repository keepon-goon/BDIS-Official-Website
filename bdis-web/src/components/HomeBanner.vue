<template>
  <div class="home-banner">
    <el-carousel height="100%" :interval="8000" arrow="hover" indicator-position="inside">
      <el-carousel-item v-for="(item, index) in activeItems" :key="item.id">
        <div class="banner-slide">
          <img
            class="banner-slide-img"
            :src="normalizeImageUrl(item.imageUrl)"
            :alt="item.title || 'BDIS banner'"
            :loading="index === 0 ? 'eager' : 'lazy'"
            :fetchpriority="index === 0 ? 'high' : 'low'"
            decoding="async"
          />
          <div class="banner-overlay" v-if="item.title">
            <span class="banner-title">{{ item.title }}</span>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { normalizeImageUrl } from '@/utils/imageUrl'

const props = defineProps({
  items: {
    type: Array,
    default: () => [],
  }
})

const activeItems = computed(() => {
  return props.items
    .filter(item => item.active !== false)
    .sort((a, b) => (a.order ?? 0) - (b.order ?? 0))
})
</script>

<style scoped>
.home-banner {
  width: 100%;
  height: 100%;
  position: relative;
  background-color: #0F172A;
}

:deep(.el-carousel),
:deep(.el-carousel__container) {
  height: 100%;
}

.banner-slide {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.banner-slide-img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  object-position: center;
  transition: transform 6s cubic-bezier(0.25, 1, 0.5, 1);
}

.banner-slide:hover .banner-slide-img {
  transform: scale(1.03);
}

.banner-overlay {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 30px 24px 20px;
  background: linear-gradient(180deg, transparent 0%, rgba(15, 23, 42, 0.75) 100%);
  display: flex;
  align-items: flex-end;
  pointer-events: none;
}

.banner-title {
  color: #FFFFFF;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

/* 轮播翻页按钮美化 */
:deep(.el-carousel__arrow) {
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.25s ease;
  border: 1px solid rgba(255, 255, 255, 0.6);
}

:deep(.el-carousel__arrow:hover) {
  background: #0C3D70 !important;
  transform: scale(1.08);
}

:deep(.el-carousel__arrow .el-icon) {
  color: #0C3D70;
  font-size: 18px;
  transition: color 0.25s ease;
}

:deep(.el-carousel__arrow:hover .el-icon) {
  color: #FFFFFF;
}

/* 指示器美化 */
:deep(.el-carousel__indicators--inside) {
  bottom: 12px;
}

:deep(.el-carousel__indicator) {
  padding: 6px 4px;
}

:deep(.el-carousel__button) {
  width: 20px;
  height: 4px;
  border-radius: 2px;
  background-color: rgba(255, 255, 255, 0.5);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  width: 32px;
  background-color: #FFFFFF;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.8);
}
</style>
