<template>
  <div class="home-hero-wrapper">
    <section class="home-one">
      <!-- 左侧：轮播大图 + 智能检索区 -->
      <div class="home-one-left">
        <!-- 轮播大图 -->
        <div class="banner">
          <HomeBanner :items="bannerList" />
        </div>
        <!-- 检索与热搜区 -->
        <div class="search">
          <HomeSearch />
        </div>
      </div>
      <!-- 右侧：资讯动态流 -->
      <div class="home-one-right">
        <HomeNews />
      </div>
    </section>
  </div>
</template>

<script setup>
import HomeBanner from './HomeBanner.vue'
import HomeSearch from './HomeSearch.vue'
import HomeNews from './HomeNews.vue'
import { onMounted, ref } from 'vue'
import axios from 'axios'

const bannerList = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/banner')
    bannerList.value = res.data?.data || []
  } catch (err) {
    console.error('bannerList 接口请求失败', err)
  }
})
</script>

<style scoped>
.home-hero-wrapper {
  max-width: 1440px;
  margin: 20px auto 40px;
  padding: 0 24px;
  box-sizing: border-box;
}

.home-one {
  --home-search-height: clamp(190px, 11.5vw, 230px);

  display: grid;
  grid-template-columns: minmax(0, 9.8fr) minmax(420px, 6.2fr);
  height: clamp(660px, 38vw, 740px);
  width: 100%;
  overflow: hidden;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 10px 30px -5px rgba(12, 61, 112, 0.08), 0 4px 12px -2px rgba(0, 0, 0, 0.03);
  transition: box-shadow 0.3s ease;
}

.home-one-left,
.home-one-right {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.home-one-left {
  border-right: 1px solid rgba(226, 232, 240, 0.8);
}

.home-one-left .banner {
  flex: 1 1 auto;
  min-height: 0;
  position: relative;
}

.home-one-left .search {
  flex: 0 0 var(--home-search-height);
  min-height: 190px;
  background: #FFFFFF;
}

@media (max-width: 1180px) {
  .home-hero-wrapper {
    margin: 16px auto 32px;
    padding: 0 16px;
  }

  .home-one {
    grid-template-columns: 1fr;
    height: auto;
    overflow: visible;
  }

  .home-one-left {
    border-right: none;
    border-bottom: 1px solid rgba(226, 232, 240, 0.8);
    height: auto;
  }

  .home-one-right {
    height: clamp(540px, 75vw, 700px);
  }

  .home-one-left .banner {
    height: clamp(320px, 52vw, 540px);
    flex: none;
  }

  .home-one-left .search {
    flex: none;
    min-height: 210px;
  }
}

@media (max-width: 768px) {
  .home-hero-wrapper {
    margin: 10px auto 20px;
    padding: 0 10px;
  }

  .home-one {
    border-radius: 12px;
  }

  .home-one-left .banner {
    height: clamp(230px, 56vw, 340px);
  }

  .home-one-right {
    height: auto;
  }
}
</style>
