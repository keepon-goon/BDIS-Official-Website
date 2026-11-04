<template>
  <div class="page-container">
    <div class="page-header">
      <h2>关于与招新</h2>
    </div>

    <el-card v-loading="loading" shadow="never">
      <div v-if="loadError" class="load-error">
        <el-alert :title="loadError" type="error" :closable="false" show-icon />
        <el-button @click="loadInfo">重新加载</el-button>
      </div>
      <el-form v-else :model="form" :disabled="loading || saving" label-position="top" @submit.prevent="saveInfo">
        <p class="form-hint">介绍支持换行。地点、邮箱和 QQ 群号会同步显示在两个页面和官网页脚，留空可隐藏对应项。</p>
        <el-form-item label="关于我们 · 工作室简介">
          <el-input v-model="form.introShort" type="textarea" :rows="5" :maxlength="16000" />
        </el-form-item>
        <el-form-item label="加入我们 · 写给未来的你">
          <el-input v-model="form.joinIntro" type="textarea" :rows="9" :maxlength="16000" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="form.address" :maxlength="200" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model.trim="form.email" type="email" :maxlength="100" />
        </el-form-item>
        <el-form-item label="QQ 群号">
          <el-input v-model.trim="form.qqGroup" inputmode="numeric" :maxlength="50" />
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="saving">保存内容</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const endpoint = '/api/admin/lab-info'
const form = reactive({ introShort: '', joinIntro: '', address: '', email: '', qqGroup: '' })
const loading = ref(true)
const saving = ref(false)
const loadError = ref('')

async function loadInfo() {
  loading.value = true
  loadError.value = ''
  try {
    const { data } = await axios.get(endpoint)
    if (data.code !== 0 || !data.data) {
      loadError.value = data.code === 0 ? '未找到实验室信息，请检查数据库初始化状态' : data.message || '读取内容失败'
      return
    }
    for (const key of Object.keys(form)) {
      form[key] = data.data[key] ?? ''
    }
  } catch {
    loadError.value = '读取内容失败，请检查网络或服务器状态'
  } finally {
    loading.value = false
  }
}

async function saveInfo() {
  if (loading.value || saving.value || loadError.value) return
  if (!/^[0-9]*$/.test(form.qqGroup)) {
    ElMessage.warning('QQ 群号只能填写数字')
    return
  }
  saving.value = true
  try {
    const { data } = await axios.put(endpoint, { ...form, address: form.address.trim() })
    if (data.code === 0) {
      form.address = data.data.address
      ElMessage.success('已保存，刷新官网即可查看更新')
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.status === 400 ? '请检查邮箱、QQ群号和内容长度' : '保存失败，请重试')
  } finally {
    saving.value = false
  }
}

onMounted(loadInfo)
</script>

<style scoped>
.page-container {
  max-width: 900px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #0c3d70;
}

.form-hint {
  margin: 0 0 20px;
  color: #606266;
  line-height: 1.6;
}

.load-error {
  display: grid;
  gap: 12px;
  justify-items: start;
}
</style>
