<template>
  <div class="home-banner">
    <el-carousel height="525px" :interval="10000" arrow="hover" indicator-position="outside">
      <el-carousel-item v-for="item in activeItems" :key="item.id">
        <div class="banner-slide" :style="slideStyle(item.imageUrl)"></div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router'

// 接受父组件传入的数组
const props = defineProps({
  items: {
    type: Array,
    default: () => [],
  }
})

// 对父组件传入的数组过滤
const activeItems = computed(() => {
  const lastItems = props.items.filter(item => item.active !== false).sort((a, b) => (a.order ?? 0) - (b.order ?? 0))
  return lastItems
})


//渲染图片
const slideStyle = (url) => ({
  backgroundImage: url ? `url(${url})` : 'none',
  backgroundSize: 'cover',
  backgroundPosition: 'center',
})


</script>

<style scoped>
.home-banner {
  width: 100%;
  height: 100%;
}

.banner-slide {
  width: 100%;
  height: 100%;
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