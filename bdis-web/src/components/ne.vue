<template>
  <div class="homeNews">
    <div class="homeNews-list">
      <div class="homeNews-item" v-for="item in newsList" :key="item.order">
        <div class="homeNews-item-box">
          <div class="homeNews-item-box-left">
            <div class="homeNews-item-box-left-top">
              {{ item.title + ": " + item.summary }}
            </div>
            <div class="homeNews-item-box-left-foot">
            </div>
          </div>
          <div class="homeNews-item-box-right">
            <img class="homeNews-item-img" :src="item.coverThumbUrl" :alt="item.title"></img>
          </div>
        </div>
      </div>
    </div>
    <div class="homeNews-item-more">
      查看全部
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, ref } from 'vue'

const newsList = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/news', { params: { limit: 5 } })
    newsList.value = res.data?.data || []
  } catch (err) {
    console.log('newsList接口请求失败', err)
  }
})
</script>

<style scoped>
.homeNews {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.homeNews-list {
  height: calc(100% - 55px);
  display: flex;
  flex-direction: column;
  background-color: rgb(249, 249, 250);
}

.homeNews-item {
  height: calc((100% - 0px) / 5);
  width: 100%;
  display: flex;
}

.homeNews-item-box {
  display: flex;
  box-sizing: border-box;
  width: 100%;
  padding: 8px 12px;
}

.homeNews-item-more {
  height: 55px;
  line-height: 55px;
  width: 100%;
  text-align: center;
  border-top: 1px solid #eee;
  background-color: #0C3D70;
  color: #FFFFFF;
}

.homeNews-item-box-left {
  box-sizing: border-box;
  width: 64%;
  height: 100%;
  position: relative;
  padding: 0 20px 0 30px;
  text-align: left;
  font-size: 16px;
}

.homeNews-item-box-left::after {
  content: "";
  display: block;
  position: absolute;
  bottom: 0px;
  height: 0.5px;
  background-color: rgb(162, 162, 165);
  left: 20px;
  right: 30px;
}

.homeNews-item-box-left-top {
  height: 45px;
  width: 100%;
  margin: 10px 0px 10px 0px;
  box-sizing: border-box;
  color: #333;
  font-size: 17px;
  font-weight: 500;
  font-family: "MiSans";
}

.homeNews-item-box-left-foot {
  height: 16px;
  width: 100%;
  position: absolute;
  bottom: 15px;
}

.homeNews-item-box-right {
  width: 36%;
  height: 100%;
  border-radius: 10px 10px 0 0;
  overflow: hidden;
}

.homeNews-item-img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}
</style>
