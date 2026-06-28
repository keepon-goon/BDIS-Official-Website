<template>
  <section class="home">
    <ViewOne />
  </section>

  <el-dialog v-model="showNotice" title="首次加载说明" width="480px" :close-on-click-modal="false" align-center>
    <div class="notice-body">
      <p>受页面资源加载机制影响，首次访问时加载速度可能偏慢，部分图片与交互按钮可能出现响应延迟。您可尝试稍作等待或刷新页面恢复正常使用，我们正在持续推进页面性能优化。感谢您的理解与支持。</p>
    </div>
    <template #footer>
      <el-button type="primary" @click="closeNotice">我已知晓</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import ViewOne from '../components/viewOne.vue'
import { ref, onMounted } from 'vue'

const showNotice = ref(false)

onMounted(() => {
  if (!localStorage.getItem('bdis_notice_read')) {
    showNotice.value = true
  }
})

const closeNotice = () => {
  showNotice.value = false
  localStorage.setItem('bdis_notice_read', '1')
}
</script>

<style scoped>
.home {
  position: relative;
}

.notice-body p {
  margin: 0 0 12px;
  line-height: 1.8;
  color: #333;
  font-size: 15px;
}

.notice-body p:last-child {
  margin-bottom: 0;
}
</style>
