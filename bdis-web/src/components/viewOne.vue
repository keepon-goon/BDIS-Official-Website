<template>
  <section class="home-one">
    <!-- 右边：轮播图加搜索框 -->
    <div class="home-one-left">
      <!-- 轮播图 -->
      <div class="banner">
        <HomeBanner :items="bannerList" />
      </div>
      <!-- 搜索框 -->
      <div class="search">
        <HomeSearch />
      </div>
    </div>
    <!-- 左边：新闻动态 -->
    <div class="home-one-right">
      <HomeNews />
    </div>
  </section>
</template>

<script setup>
import HomeBanner from './HomeBanner.vue';
import HomeSearch from './HomeSearch.vue';
import HomeNews from './HomeNews.vue';
import { onMounted, ref } from 'vue'
import axios from 'axios';

const bannerList = ref([])

// 页面挂载后请求接口
onMounted(async () => {
  try {
    const res = await axios.get('/api/banner')
    console.log('banner 接口返回原始数据：', res.status, res.data)
    bannerList.value = res.data?.data || []
    console.log('bannerList 赋值后的内容：', bannerList.value)
  } catch (err) {
    console.error('bannerList接口请求失败', err)
  }
})

</script>

<style scoped>
.home-one {
  display: grid;
  grid-template-columns: 9.5fr 6.5fr;
  height: 750px;
}

.home-one-left,
.home-one-right {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.home-one-left .banner {
  flex: 0 0 450px;
}

.home-one-left .search {
  flex: 1 1 auto;
}
</style>