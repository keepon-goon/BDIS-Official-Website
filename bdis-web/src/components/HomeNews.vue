<template>
  <div class="homeNews">
    <div class="homeNews-list">
      <div class="homeNews-item" v-for="item in newsList" :key="item.order" @click="goDetail(item.id)">
        <div class="homeNews-item-box">
          <div class="homeNews-item-box-left">
            <div class="homeNews-item-box-left-top">
              {{ item.title + ": " + item.summary }}
            </div>
            <div class="homeNews-item-box-left-foot">
              <div class="homeNews-meta">
                <!-- 左边日期部分 -->
                <div class="homeNews-meta-block">
                  <el-icon class="homeNews-meta-icon">
                    <Calendar />
                  </el-icon>
                  <span class="homeNews-meta-date">
                    {{ item.publishDate }}
                  </span>
                </div>
                <!-- 中间竖线部分 -->
                <span class="homeNews-meta-divider"></span>
                <!-- 右边分类部分 -->
                <div class="homeNews-meta-block">
                  <el-icon class="homeNews-meta-icon">
                    <Collection />
                  </el-icon>
                  <span class="homeNews-meta-category">
                    {{ categoryText(item.category) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div class="homeNews-item-box-right">
            <img class="homeNews-item-img" :src="item.coverThumbUrl" :alt="item.title"></img>
          </div>
        </div>
      </div>
    </div>
    <div class="homeNews-item-more" @click="goMore">
      查看全部
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, ref } from 'vue'
import { Calendar, Collection } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const newsList = ref([])
const router = useRouter()

const categoryText = (cat) => {
  const map = {
    recruit: '招新通告',
    activity: '活动组会',
    notice: '综合新闻',
    contest: '创新竞赛',
    project_recruit: '项目招募',
  }
  return map[cat]
}

onMounted(async () => {
  try {
    const res = await axios.get('/api/news', { params: { limit: 5 } })
    newsList.value = res.data?.data || []
  } catch (err) {
    console.log('newsList接口请求失败', err)
  }
})

const goDetail = (id) => router.push('/wip')//V2版本再做细节页面
const goMore = () => router.push('/wip')//V2版本再做细节页面
</script>

<style scoped>
.homeNews {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.homeNews-list {
  /* height: calc(100% - 55px); */
  height: 700px;
  display: flex;
  flex-direction: column;
  background-color: rgb(249, 249, 250);
}

.homeNews-item {
  /* height: calc((100% - 0px) / 5); */
  height: 140px;
  width: 100%;
  display: flex;
  cursor: pointer;
  transition: transform 0.5s ease, box-shadow 0.5s ease;
}

.homeNews-item:hover {
  box-shadow: 0 15px 15px 0 rgba(0, 0, 0, 0.08);
  background-color: #FFFFFFDE;
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
  cursor: pointer;
  transition: background-color 0.5s ease color 0.5s ease;
}

.homeNews-item-more:hover {
  background-color: rgb(236, 245, 255);
  color: rgb(64, 158, 255);
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

/* 整行容器 */
.homeNews-meta {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #8b8b8b;
}

/* 日期块 / 分类块 */
.homeNews-meta-block {
  display: flex;
  align-items: center;
}

/* 图标 */
.homeNews-meta-icon {
  font-size: 14px;
  margin-right: 4px;
}

/* 日期：蓝色 */
.homeNews-meta-date {
  color: #0056a8;
}

/* 分类：灰色 */
.homeNews-meta-category {
  color: #666;
}

/* 中间竖线 */
.homeNews-meta-divider {
  width: 1px;
  height: 12px;
  background-color: #d0d0d0;
  margin: 0 12px;
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
  transition: transform 0.5s ease;
  transform-origin: center center;
}

.homeNews-item:hover .homeNews-item-img {
  transform: scale(1.06);
}
</style>
