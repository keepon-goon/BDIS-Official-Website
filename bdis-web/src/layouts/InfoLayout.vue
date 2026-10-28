<template>
  <div class="info-layout-container">
    <div class="info-layout">
      <div class="way">
        <div class="box">首页</div>
        <div class="box-icon">
          <el-icon class="icon">
            <ArrowRightBold />
          </el-icon>
        </div>
        <div class="box">{{ route.meta.title }}</div>
      </div>
      <div class="name">{{ route.meta.title }}</div>
    </div>
    <main class="info-subpage-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ArrowRightBold } from '@element-plus/icons-vue';
import { useRoute } from 'vue-router';

const route = useRoute()
</script>

<style scoped>
.info-layout-container {
  width: 100%;
}

.info-layout {
  box-sizing: border-box;
  width: 100%;
  min-height: 155px;
  background-color: #F9F9FA;
  padding: 30px 50px 25px 50px;
  animation: infoHeaderIn 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes infoHeaderIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.info-subpage-main {
  width: 100%;
}

.way {
  display: flex;
  align-items: center;
}

.box {
  height: 17px;
  font-size: 16px;
  color: #ADADAD;
  display: flex;
  justify-content: center;
  align-items: center;
}

.box-icon {
  width: 20px;
  height: 17px;
  font-size: 14px;
  color: #ADADAD;
  display: flex;
  justify-content: center;
  align-items: center;
}

.icon {
  display: flex;
  color: #ADADAD;
  font-size: 16px;
}

.name {
  font-size: 32px;
  color: #0C3D70;
  font-family: 'MiSans';
  font-weight: 600;
  padding-right: 85px;
}

@media (max-width: 768px) {
  .info-layout {
    min-height: 120px;
    padding: 20px;
  }

  .name {
    padding-right: 0;
    font-size: 26px;
  }
}
</style>
