<template>
  <div class="home-banner">
    <el-carousel height="100%" :interval="10000" arrow="hover" indicator-position="outside">
      <el-carousel-item v-for="(item, index) in activeItems" :key="item.id">
        <img
          class="banner-slide-img"
          :src="normalizeImageUrl(item.imageUrl)"
          :alt="item.title || 'BDIS banner'"
          :loading="index === 0 ? 'eager' : 'lazy'"
          :fetchpriority="index === 0 ? 'high' : 'low'"
          decoding="async"
        />
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { normalizeImageUrl } from '@/utils/imageUrl'

// 接受父组件传入的数组
const props = defineProps({
  items: {
    type: Array,
    default: () => [],
  }
})

// 对父组件传入的数组过�?
const activeItems = computed(() => {
  const lastItems = props.items.filter(item => item.active !== false).sort((a, b) => (a.order ?? 0) - (b.order ?? 0))
  return lastItems
})


</script>

<style scoped>
.home-banner {
  width: 100%;
  height: 100%;
}

:deep(.el-carousel),
:deep(.el-carousel__container) {
  height: 100%;
}

.banner-slide-img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  object-position: center;
  background-color: black;
}

:deep(.el-carousel__arrow) {
  border-radius: 0px;
  height: 65px;
  width: 45px;
  background-color: #FFFFFF99;
  margin: -16px;
}

:deep(.el-carousel__arrow .el-icon) {
  color: #000;
  font-size: 20px;
}
</style>
