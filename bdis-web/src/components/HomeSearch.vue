<template>
  <div class="search-main">
    <!-- 输入框部分 -->
    <div class="search-input">
      <el-input v-model="input" placeholder="输入关键词检索相关内容">
        <template #suffix>
          <el-button :icon="Search" @click="SearchClick" class="search-el-button">搜索</el-button>
        </template>
      </el-input>
    </div>
    <!-- 热门搜索部分 -->
    <div class="search-hot">
      <div class="search-hot-left">
        <h5>热门搜索：</h5>
      </div>
      <div class="search-hot-right-list">
        <div class="search-hot-right-item" v-for="item in hotSearch" :key="item.index"
          @click="hotWordClick(item.label)">
          <span class="search-hot-text">{{ item.label }}</span>
          <el-icon class="search-hot-icon">
            <TopRight />
          </el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search, TopRight } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const input = ref('')
// 热门搜索部分
const hotSearch = [
  { index: 1, label: '加入我们' },
  { index: 2, label: '项目招募' },
  { index: 3, label: '科研方向' },
  { index: 4, label: '资料推荐' },
  { index: 5, label: '开源代码' },
  { index: 6, label: '活动预告' },
  { index: 7, label: '企业合作' },
  { index: 8, label: '常见问题' },
]

// 搜索逻辑
const router = useRouter()
const SearchClick = () => {
  router.push('/wip')
  console.log('search:', input.value)
}

const hotWordClick = (label) => {
  input.value = label
  SearchClick()
}
</script>

<style scoped>
.search-main {
  height: 186px;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.search-input {
  display: flex;
  height: 85px;
  padding: 12px;
  margin: 0 0 0 30px;
  box-sizing: border-box;
}

:deep(.el-input__inner) {
  height: 65px;
  padding: 20px;
  background-color: rgb(249, 249, 250);
}

:deep(.el-input__inner::placeholder) {
  font-size: 16px;
  color: #554a4a76;
}

:deep(.el-icon) {
  font-size: 20px;
}

:deep(.search-el-button) {
  background-color: #0C3D70;
  color: #FFFF;
  font-size: 16px;
  height: 65px;
  width: 140px;
  padding: 0 !important;
  margin: 0 !important;
  border-radius: 0px;
}

:deep(.el-input__wrapper) {
  padding: 0 !important;
  border-radius: 0px;
}

/* 热门搜索部分 */
.search-hot {
  height: 101px;
  width: 1014px;
  display: flex;
}

.search-hot-left {
  margin-left: 28px;
}

.search-hot-left h5 {
  width: 100px;
  color: #0C3D70;
  margin: 10px 0 0 0;
}


.search-hot-right-list {
  display: flex;
  flex-wrap: wrap;
}

.search-hot-right-item {
  width: 156px;
  height: 18px;
  font-size: 14px;
  font-weight: 400;
  margin-top: 11px;
  margin-left: 0px;
  color: #554a4a76;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.search-hot-right-item:hover {
  color: #0C3D70;
}

.search-hot-right-item .search-hot-icon {
  font-size: 14px;
  display: flex;
  align-items: center;
  color: #0C3D70;
  font-weight: 800;
}
</style>