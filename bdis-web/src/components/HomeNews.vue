<template>
  <div class="homeNews">
    <!-- 头部栏 -->
    <div class="homeNews-header">
      <div class="homeNews-header-left">
        <span class="homeNews-dot"></span>
        <h3 class="homeNews-header-title">动态资讯</h3>
        <span class="homeNews-header-sub">STUDIO UPDATES</span>
      </div>
      <div class="homeNews-header-right" @click="goMore">
        <span>更多</span>
        <el-icon><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 资讯列表 -->
    <div class="homeNews-list">
      <div
        v-for="item in newsList"
        :key="item.id"
        class="homeNews-item"
        @click="goDetail(item.id)"
      >
        <div class="homeNews-item-box">
          <div class="homeNews-item-box-left">
            <div class="homeNews-item-box-left-top" :title="item.title">
              <span class="homeNews-title-text">{{ item.title }}</span>
              <span v-if="item.summary" class="homeNews-summary-text">{{ ' - ' + item.summary }}</span>
            </div>
            
            <div class="homeNews-item-box-left-foot">
              <div class="homeNews-meta">
                <!-- 日期 -->
                <div class="homeNews-meta-block">
                  <el-icon class="homeNews-meta-icon"><Calendar /></el-icon>
                  <span class="homeNews-meta-date">{{ item.publishDate }}</span>
                </div>
                <!-- 分割线 -->
                <span class="homeNews-meta-divider"></span>
                <!-- 分类标签 -->
                <span class="homeNews-category-badge" :class="`badge-${item.category}`">
                  {{ categoryText(item.category) }}
                </span>
              </div>
            </div>
          </div>

          <!-- 右侧缩略图 -->
          <div class="homeNews-item-box-right" v-if="item.coverThumbUrl">
            <img
              class="homeNews-item-img"
              :src="normalizeImageUrl(item.coverThumbUrl)"
              :alt="item.title"
              loading="lazy"
              fetchpriority="low"
              decoding="async"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 查看全部条目 -->
    <div class="homeNews-item-more" @click="goMore">
      <span>浏览更多工作室动态与科研成果</span>
      <el-icon class="more-icon"><Right /></el-icon>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, ref } from 'vue'
import { Calendar, ArrowRight, Right } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { normalizeImageUrl } from '@/utils/imageUrl'

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
  return map[cat] || '动态'
}

onMounted(async () => {
  try {
    const res = await axios.get('/api/news', { params: { limit: 5 } })
    newsList.value = res.data?.data || []
  } catch (err) {
    console.error('newsList 接口请求失败', err)
  }
})

const goDetail = (id) => router.push('/info/news/' + id)
const goMore = () => router.push('/info/news')
</script>

<style scoped>
.homeNews {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  background-color: #FFFFFF;
}

/* 顶部标题栏 */
.homeNews-header {
  flex: 0 0 52px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #F8FAFC;
  border-bottom: 1px solid #E2E8F0;
}

.homeNews-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.homeNews-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #0284C7;
  box-shadow: 0 0 0 3px rgba(2, 132, 199, 0.2);
}

.homeNews-header-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #0C3D70;
}

.homeNews-header-sub {
  font-size: 11px;
  letter-spacing: 0.8px;
  color: #94A3B8;
  font-weight: 600;
}

.homeNews-header-right {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #64748B;
  cursor: pointer;
  transition: all 0.2s ease;
}

.homeNews-header-right:hover {
  color: #0C3D70;
  transform: translateX(2px);
}

/* 新闻列表 */
.homeNews-list {
  flex: 1 1 auto;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.homeNews-item {
  flex: 1 1 0;
  min-height: 98px;
  width: 100%;
  display: flex;
  cursor: pointer;
  border-bottom: 1px solid #F1F5F9;
  position: relative;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.homeNews-item::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: #0284C7;
  transform: scaleY(0);
  transition: transform 0.25s ease;
}

.homeNews-item:hover {
  background-color: #F8FAFC;
}

.homeNews-item:hover::before {
  transform: scaleY(1);
}

.homeNews-item-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 10px 18px;
  box-sizing: border-box;
  gap: 16px;
}

.homeNews-item-box-left {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
}

.homeNews-item-box-left-top {
  color: #1E293B;
  font-size: 15px;
  font-weight: 500;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s ease;
}

.homeNews-item:hover .homeNews-item-box-left-top {
  color: #0C3D70;
}

.homeNews-title-text {
  font-weight: 600;
}

.homeNews-summary-text {
  color: #64748B;
  font-weight: 400;
}

.homeNews-meta {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #94A3B8;
  gap: 8px;
}

.homeNews-meta-block {
  display: flex;
  align-items: center;
  gap: 4px;
}

.homeNews-meta-icon {
  font-size: 13px;
}

.homeNews-meta-date {
  color: #64748B;
}

.homeNews-meta-divider {
  width: 1px;
  height: 10px;
  background-color: #CBD5E1;
}

/* 分类徽章 */
.homeNews-category-badge {
  padding: 1px 7px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
  background: #F1F5F9;
  color: #475569;
}

.badge-recruit {
  background: #EFF6FF;
  color: #1D4ED8;
}

.badge-activity {
  background: #F0FDF4;
  color: #15803D;
}

.badge-contest {
  background: #FEF3C7;
  color: #B45309;
}

.badge-project_recruit {
  background: #FAF5FF;
  color: #7E22CE;
}

/* 缩略图 */
.homeNews-item-box-right {
  width: 100px;
  height: 72px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.homeNews-item-img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.homeNews-item:hover .homeNews-item-img {
  transform: scale(1.08);
}

/* 查看更多条目 */
.homeNews-item-more {
  flex: 0 0 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #0C3D70;
  color: #FFFFFF;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  border-top: 1px solid #E2E8F0;
}

.homeNews-item-more:hover {
  background: #16569A;
}

.homeNews-item-more .more-icon {
  transition: transform 0.25s ease;
}

.homeNews-item-more:hover .more-icon {
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .homeNews-item-box {
    padding: 12px 14px;
  }

  .homeNews-item-box-right {
    width: 80px;
    height: 60px;
  }
}
</style>
