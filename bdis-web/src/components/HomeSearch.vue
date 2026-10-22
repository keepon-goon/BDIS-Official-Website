<template>
  <div class="search-main">
    <!-- 输入框部分 -->
    <div class="search-input-wrapper">
      <div class="search-box">
        <el-input
          v-model="input"
          placeholder="输入关键词检索新闻、项目、科研成果或资料..."
          class="custom-search-input"
          @keyup.enter="SearchClick"
        >
          <template #prefix>
            <el-icon class="search-prefix-icon"><Search /></el-icon>
          </template>
        </el-input>
        <button class="search-submit-btn" @click="SearchClick">
          <span>搜索</span>
          <el-icon><Right /></el-icon>
        </button>
      </div>
    </div>

    <!-- 热门搜索部分 -->
    <div class="search-hot">
      <div class="search-hot-label">
        <el-icon class="hot-flame-icon"><Compass /></el-icon>
        <span>热门检索</span>
      </div>
      <div class="search-hot-tags">
        <div
          v-for="item in hotSearch"
          :key="item.index"
          class="hot-tag-chip"
          @click="hotWordClick(item.label)"
        >
          <span class="hot-tag-text">{{ item.label }}</span>
          <el-icon class="hot-tag-arrow"><TopRight /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search, TopRight, Right, Compass } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const input = ref('')
const router = useRouter()

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

const SearchClick = () => {
  if (input.value.trim()) {
    router.push({ path: '/search', query: { q: input.value.trim() } })
  }
}

const hotWordClick = (label) => {
  input.value = label
  SearchClick()
}
</script>

<style scoped>
.search-main {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 16px 24px;
  box-sizing: border-box;
  background: linear-gradient(180deg, #FFFFFF 0%, #F8FAFC 100%);
}

.search-input-wrapper {
  margin-bottom: 14px;
}

.search-box {
  display: flex;
  align-items: center;
  background: #FFFFFF;
  border-radius: 12px;
  border: 1.5px solid #E2E8F0;
  box-shadow: 0 4px 14px rgba(12, 61, 112, 0.05);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
  padding: 4px;
}

.search-box:focus-within {
  border-color: #0C3D70;
  box-shadow: 0 0 0 4px rgba(12, 61, 112, 0.1), 0 6px 18px rgba(12, 61, 112, 0.08);
}

.custom-search-input {
  flex: 1;
}

:deep(.custom-search-input .el-input__wrapper) {
  box-shadow: none !important;
  background: transparent;
  padding: 8px 12px;
}

:deep(.custom-search-input .el-input__inner) {
  font-size: 15px;
  color: #1E293B;
  height: 38px;
}

:deep(.custom-search-input .el-input__inner::placeholder) {
  color: #94A3B8;
  font-size: 14px;
}

.search-prefix-icon {
  font-size: 18px;
  color: #94A3B8;
  margin-right: 4px;
}

.search-submit-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #0C3D70 0%, #0284C7 100%);
  color: #FFFFFF;
  border: none;
  padding: 10px 22px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 2px 8px rgba(12, 61, 112, 0.25);
  flex-shrink: 0;
}

.search-submit-btn:hover {
  background: linear-gradient(135deg, #104e8d 0%, #0396e0 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(12, 61, 112, 0.35);
}

.search-submit-btn:active {
  transform: translateY(0) scale(0.98);
}

/* 热门搜索区域 */
.search-hot {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.search-hot-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  color: #0C3D70;
  white-space: nowrap;
  padding-top: 6px;
}

.hot-flame-icon {
  font-size: 15px;
  color: #0284C7;
}

.search-hot-tags {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hot-tag-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 11px;
  background: #F1F5F9;
  border-radius: 6px;
  font-size: 13px;
  color: #475569;
  cursor: pointer;
  border: 1px solid #E2E8F0;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}

.hot-tag-arrow {
  font-size: 12px;
  color: #94A3B8;
  transition: transform 0.2s ease, color 0.2s ease;
}

.hot-tag-chip:hover {
  background: #FFFFFF;
  color: #0C3D70;
  border-color: #0C3D70;
  box-shadow: 0 2px 6px rgba(12, 61, 112, 0.1);
  transform: translateY(-1px);
}

.hot-tag-chip:hover .hot-tag-arrow {
  color: #0284C7;
  transform: translate(1px, -1px);
}

@media (max-width: 768px) {
  .search-main {
    padding: 12px 14px;
  }

  .search-submit-btn span {
    display: none;
  }

  .search-submit-btn {
    padding: 10px 14px;
  }

  .search-hot {
    flex-direction: column;
    gap: 6px;
  }
}
</style>
