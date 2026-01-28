<template>
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
  <router-view />
</template>

<script setup>
import { ArrowRightBold } from '@element-plus/icons-vue';
import router from '../router';
import { useRoute } from 'vue-router';

const route = useRoute()
</script>

<style scoped>
.info-layout {
  width: 100%;
  height: 100px;
  background-color: #F9F9FA;
  padding: 30px 50px 25px 50px;
}

.way {
  display: flex;
  align-items: center;
}

.box {
  /* width: 56px; */
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
</style>