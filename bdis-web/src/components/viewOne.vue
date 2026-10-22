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
  --home-search-height: clamp(186px, 11.2vw, 230px);

  display: grid;
  grid-template-columns: minmax(0, 9.5fr) minmax(420px, 6.5fr);
  height: clamp(680px, 36.8vw, 755px);
  width: 100%;
  overflow: hidden;
}

.home-one-left,
.home-one-right {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.home-one-left .banner {
  flex: 1 1 auto;
  min-height: 0;
}

.home-one-left .search {
  flex: 0 0 var(--home-search-height);
  min-height: 186px;
}

@media (max-width: 1180px) {
  .home-one {
    grid-template-columns: 1fr;
    height: auto;
    overflow: visible;
  }

  .home-one-left {
    height: auto;
  }

  .home-one-right {
    height: clamp(560px, 78vw, 720px);
  }

  .home-one-left .banner {
    height: clamp(320px, 52vw, 560px);
    flex: none;
  }

  .home-one-left .search {
    flex: none;
    min-height: 220px;
  }
}

@media (max-width: 768px) {
  .home-one-left .banner {
    height: clamp(240px, 58vw, 360px);
  }

  .home-one-right {
    height: auto;
  }
}
</style>
