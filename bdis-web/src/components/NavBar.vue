<!-- src/components/NavBar.vue -->
<template>
  <header class="nav">
    <!-- 左：logo -->
    <div class="nav-left">
      <img class="nav-logo" :src="logoImg" alt="BDIS 大数据智能工作室" />
    </div>

    <!-- 中：占位空白 -->
    <div class="nav-center"></div>

    <!-- 右：主菜单 + 搜索按钮 -->
    <div class="nav-right">
      <div class="nav-right-top">
        <button><a href="https://www.cqut.edu.cn/">Unversity</a></button>
        <button><a href="https://cs.cqut.edu.cn/">School</a></button>
        <button><a href="https://www.tipdm.com/">Partners</a></button>
        <button><a href="/wip">Members</a></button>
        <button><a href="/wip">EN</a></button>
      </div>
      <div class="nav-right-foot">
        <el-menu mode="horizontal" :default-active="activeMain" class="nav-menu-right" :ellipsis="false"
          @select="handleMainSelect">
          <el-menu-item v-for="item in mainMenus" :key="item.index" :index="item.index">
            {{ item.label }}
          </el-menu-item>
        </el-menu>
        <el-button circle class="nav-search-btn">
          <el-icon>
            <Search />
          </el-icon>
        </el-button>
      </div>

    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import logoImg from '../assets/logo.jpg'
import router from '../router'

const activeMain = ref('home')

// 右侧菜单配置
const mainMenus = [
  { index: 'home', label: '首页', path: "/" },
  { index: 'about', label: '关于我们', path: "/info/about" },
  { index: 'research', label: '项目进展', path: "/info/research" },
  { index: 'platform', label: '创新竞赛', path: "/info/platform" },
  { index: 'news', label: '动态公告', path: "/info/news" },
  { index: 'education', label: '学习资料', path: "/info/education" },
  { index: 'join', label: '加入我们', path: "/info/join" },
]

// 菜单点击
const handleMainSelect = (key) => {
  activeMain.value = key
  console.log('主菜单点击：', key)
  const target = mainMenus.find(item => {
    return item.index === key
  })
  if (target) {
    router.push(target.path)
  }
}
</script>

<style scoped>
/* 整体一行：左 logo，中间空白，右侧菜单+搜索 */
.nav {
  display: flex;
  align-items: center;
  height: 135px;
  width: 100%;
  padding: 0 32px;
  box-sizing: border-box;
  background-color: #ffffff;
}

/* 左边固定宽度内容（logo） */
.nav-left {
  flex: 0 0 auto;
}

/* 中间吃掉剩余空间，形成“空白”效果 */
.nav-center {
  flex: 1 1 auto;
}

/* 右边：菜单 + 搜索 */
.nav-right {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 30px;
  /* 菜单和搜索按钮之间的间距 */
}

/* logo 等比缩放 */
.nav-logo {
  max-height: 66px;
  height: auto;
  width: auto;
}

.nav-right-foot {
  display: flex;
}

/* .nav-right-top {
  
} */

.nav-right-top button {
  background-color: #F1F1F1;
  border-radius: 0px;
  height: 37px;
  margin: 1px;
}

.nav-right-top button a {
  color: #23415A;
}

.nav-right-top button:last-child {
  background-color: #0C3D70;
}

.nav-right-top button:last-child a {
  color: white;
}

:deep(.el-button.is-circle) {
  padding: 20px;
  margin: -10px 2px -10px 5px;
  background-color: #F1F1F1;
}

/* 右侧菜单：去掉默认下划线 */
:deep(.nav-menu-right.el-menu--horizontal) {
  border-bottom: none;
}

/* 右侧菜单的所有项：去掉默认下划线 */
:deep(.nav-menu-right .el-menu-item) {
  display: block;
  border-bottom: none;
  font-size: 18px;
  font-weight: 550 !important;
  padding: 0px 5px 0px 0px;
  margin: 0 30px;
  transition: border-bottom 0.3s ease;
  color: #0C3D70;
}

/* 右侧菜单的激活项：去掉默认下划线 */
:deep(.nav-menu-right .el-menu-item.is-active) {
  border-bottom: none;
  color: #0C3D70 !important;
  background: transparent !important;
}

/* 右侧菜单的点击项：去掉默认下划线 */
:deep(.nav-menu-right .el-menu-item:hover) {
  border-bottom: 3px solid black;
  color: #0C3D70 !important;
  background: transparent !important;
}

/* 搜索圆形按钮 */
.nav-search-btn {
  border-radius: 50%;
}

:deep(.el-menu) {
  margin: -18px 0px 10px 0px;
}
</style>
