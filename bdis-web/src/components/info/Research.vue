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
            <span class="icon-word">开始时间：{{ item.updatedAt }}
            </span>
          </div>
          <div class="item-row">
            <el-icon class="icon">
              <Position />
            </el-icon>
            <span class="icon-word">招募角色：{{ (item.recruitRoles || []).join('、') || '—' }}
            </span>
          </div>
          <div class="item-row">
            <el-icon class="icon">
              <User />
            </el-icon>
            <span class="icon-word">参与人员：{{ (item.members || []).join('、') || '—' }}
            </span>
          </div>
          <div class="item-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: (item.progress || 0) + '%' }"></div>
            </div>
            <span class="progress-text">{{ item.progress || 0 }}%</span>
          </div>
        </div>
        <div class="item-box-middle">
        </div>
        <div class="item-box-right">
          <img :src="item.coverUrl" :alt="item.name" />
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
const goDetail = id => router.push('/wip')//V2版本再做细节页面的增加

</script>

<style scoped>
.research {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px 140px 20px 140px;
}

.tesearch-items {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 0px;
  padding: 14px 16px;
  background-color: rgb(243, 246, 248);
  cursor: pointer;
  transition: transform 0.5s ease, box-shadow 0.5s ease;
}

.tesearch-items:hover {
  transform: translateY(-4px);
  box-shadow: 0 15px 15px 0 rgba(0, 0, 0, 0.08);
  background-color: #FFFFFFDE;
}

.item-box {
  width: 100%;
  height: 240px;
  display: grid;
  grid-template-columns: 5.5fr 1fr 2.5fr;
  gap: 12px;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;
}

.item-box-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 500;
  color: rgb(35, 65, 90);
  flex-wrap: wrap;
}

.item-name {
  font-family: 'MiSans';
  font-size: 20px;
  text-align: left;
}

.icon {
  display: flex;
  color: #4c4c4c;
  font-size: 16px;
  margin: 2px 6px 0 0;
}

.item-row {
  font-size: 14px;
  color: #444;
  text-align: left;
  display: flex;
}

.item-progress {
  display: flex;
  align-items: center;
  gap: 8px;
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
  height: 100%;
  border-radius: 0px;
  overflow: hidden;
  background: #f7f7f8;
}

.item-box-right img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
  transform-origin: center center;
}

.item-box:hover img {
  transform: scale(1.06);
}

.more {
  font-family: 'MiSans';
  font-size: 16px;
  color: #000;
}
</style>