<template>
  <div class="platform">
    <div class="row-1">
      <div class="description">
        <span>在这里 · 发起竞赛组队 | 找到竞赛队友</span>
      </div>
      <button class="post" @click="post">发布组队贴</button>
    </div>
    <div class="platform-items" v-for="item in source" :key="item.id" @click="goDetail(item.id)" role="button">
      <div class="item-box">
        <div class="item-box-left">
          <div class="item-header">
            <span class="item-name">{{ item.title + " | " + item.summary }}</span>
          </div>
          <div class="item-row">
            <el-icon class="icon">
              <Calendar />
            </el-icon>
            <span class="icon-word">截止日期：{{ item.deadline }}</span>
          </div>
          <div class="item-row">
            <el-icon class="icon">
              <Position />
            </el-icon>
            <span class="icon-word">招募角色：{{ (item.recruitRolesArray || []).join('、') || '—' }}</span>
          </div>
          <div class="item-row">
            <el-icon class="icon">
              <User />
            </el-icon>
            <span class="icon-word">当前人数：{{ item.currentSize ?? '—' }} | 目标人数：{{ item.targetSize ?? '—' }}</span>
          </div>
          <div class="item-row">
            <el-icon class="icon">
              <ChatDotRound />
            </el-icon>
            <span class="icon-word">联系方式：{{ item.type }} ： {{ item.value }}</span>
          </div>
        </div>
        <div class="item-box-middle"></div>
        <div class="item-box-right">
          <img :src="normalizeImageUrl(item.coverUrl)" :alt="item.title" loading="lazy" decoding="async" />
        </div>
      </div>
    </div>
    <div class="more">更多竞赛数据同步中，敬请期待...</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Calendar, Position, User, ChatDotRound } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { normalizeImageUrl } from '@/utils/imageUrl'

const source = ref([])

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/platform/forum', { params: { limit: 5 } })
    if (data.code === 0) {
      source.value = data.data
    }
  } catch (e) {
    console.error("获取创新竞赛贴数据失败", e)
  }
})

const router = useRouter()
const goDetail = id => router.push('/info/forum/' + id)
const post = () => router.push('/wip') // V2版本再做细节页面的增加
</script>

<style scoped>
.platform {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 30px clamp(16px, 4vw, 40px);
}

.row-1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 6px;
}

.description span {
  display: block;
  font-family: 'MiSans', -apple-system, sans-serif;
  font-weight: 600;
  color: #0C3D70;
  font-size: clamp(20px, 2.5vw, 30px);
  line-height: 1.35;
  text-align: left;
}

.post {
  background-color: #0C3D70;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 10px 22px;
  font-family: 'MiSans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
  flex-shrink: 0;
}

.post:hover {
  background-color: #164f86;
}

.platform-items {
  background: #fff;
  border: 1px solid #e2ecf5;
  border-radius: 12px;
  background-color: rgb(243, 246, 248);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease, background-color 0.3s ease;
  overflow: hidden;
}

.platform-items:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 24px rgba(12, 61, 112, 0.08);
  background-color: #ffffff;
}

.item-box {
  width: 100%;
  min-height: 200px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 24px;
  align-items: center;
  padding: 24px;
  box-sizing: border-box;
}

.item-box-middle {
  display: none;
}

.item-box-left {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
}

.item-header {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.item-name {
  font-family: 'MiSans', -apple-system, sans-serif;
  font-size: clamp(17px, 1.8vw, 20px);
  font-weight: 600;
  text-align: left;
  line-height: 1.4;
  color: rgb(35, 65, 90);
  word-break: break-word;
}

.icon {
  display: flex;
  color: #0c3d70;
  font-size: 16px;
  margin-top: 3px;
  flex-shrink: 0;
}

.item-row {
  font-size: 14px;
  color: #444;
  text-align: left;
  display: flex;
  align-items: flex-start;
  gap: 6px;
  line-height: 1.6;
  word-break: break-word;
}

.icon-word {
  flex: 1;
  min-width: 0;
  word-break: break-all;
}

.item-box-right {
  width: 100%;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  background: #eef3f8;
}

.item-box-right img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
  transform-origin: center center;
}

.item-box:hover .item-box-right img {
  transform: scale(1.05);
}

.more {
  font-family: 'MiSans', sans-serif;
  font-size: 15px;
  color: #888888;
  padding: 16px 0;
}

/* 响应式断点：平板与大屏手机 */
@media (max-width: 768px) {
  .platform {
    padding: 20px 16px;
    gap: 16px;
  }

  .row-1 {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .description span {
    font-size: 20px;
  }

  .post {
    width: 100%;
    text-align: center;
    padding: 11px;
  }

  .item-box {
    grid-template-columns: 1fr;
    padding: 16px;
    gap: 16px;
    height: auto;
    min-height: auto;
  }

  .item-box-right {
    order: -1;
    height: clamp(160px, 48vw, 220px);
  }

  .item-name {
    font-size: 17px;
  }

  .item-row {
    font-size: 13.5px;
  }
}

/* 响应式断点：小屏手机 */
@media (max-width: 480px) {
  .platform {
    padding: 14px 12px;
    gap: 12px;
  }

  .item-box {
    padding: 14px;
    gap: 14px;
  }
}
</style>
