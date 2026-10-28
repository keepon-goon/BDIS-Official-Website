<template>
  <div class="research">
    <div class="tesearch-items" v-for="item in source" :key="item.id" @click="goDetail(item.id)" role="button">
      <div class="item-box">
        <div class="item-box-left">
          <div class="item-header">
            <span class="item-name">{{ item.name + " | " + item.tagline }}</span>
          </div>
          <div class="item-row">
            <el-icon class="icon">
              <Calendar />
            </el-icon>
            <span class="icon-word">开始时间：{{ item.updatedAt }}</span>
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
            <span class="icon-word">参与人员：{{ (item.membersArray || []).join('、') || '—' }}</span>
          </div>
          <div class="item-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: (item.progress || 0) + '%' }"></div>
            </div>
            <span class="progress-text">{{ item.progress || 0 }}%</span>
          </div>
        </div>
        <div class="item-box-middle"></div>
        <div class="item-box-right">
          <img :src="normalizeImageUrl(item.coverUrl)" :alt="item.name" loading="lazy" decoding="async" />
        </div>
      </div>
    </div>
    <div class="more">更多项目数据同步中，敬请期待...</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Calendar, Position, User } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { normalizeImageUrl } from '@/utils/imageUrl'

const source = ref([])

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/projects/progress', { params: { limit: 5 } })
    if (data.code === 0) {
      source.value = data.data
    }
  } catch (e) {
    console.error("获取项目进展数据失败", e)
  }
})

const router = useRouter()
const goDetail = id => router.push('/info/project/' + id)
</script>

<style scoped>
.research {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 30px clamp(16px, 4vw, 40px);
}

.tesearch-items {
  background: #fff;
  border: 1px solid #e2ecf5;
  border-radius: 12px;
  background-color: rgb(243, 246, 248);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease, background-color 0.3s ease;
  overflow: hidden;
}

.tesearch-items:hover {
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
  word-break: break-word;
}

.item-progress {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 4px;
}

.progress-bar {
  flex: 1;
  height: 10px;
  background: #eef2f6;
  border-radius: 999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #0c3d70;
  border-radius: 999px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 13px;
  color: #0c3d70;
  font-weight: 600;
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
  .research {
    padding: 20px 16px;
    gap: 16px;
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
  .research {
    padding: 14px 12px;
    gap: 12px;
  }

  .item-box {
    padding: 14px;
    gap: 14px;
  }
}
</style>
