<template>
  <div class="login-page">
    <div class="login-bg-glow glow-1"></div>
    <div class="login-bg-glow glow-2"></div>

    <div class="login-card">
      <div class="login-header">
        <div class="login-badge">BDIS STUDIO</div>
        <h1 class="login-title">管理后台系统</h1>
        <p class="login-subtitle">Big Data Intelligence Studio CMS</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入管理员账号"
            :prefix-icon="User"
            size="large"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入访问凭证"
            :prefix-icon="Lock"
            size="large"
            show-password
            class="custom-input"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登 录 系 统
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <router-link to="/" class="back-link">
          <span>← 返回官网首页</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await axios.post('/api/admin/login', {
      username: form.username,
      password: form.password
    })
    if (res.data?.code === 0) {
      const { token, nickname } = res.data.data
      localStorage.setItem('adminToken', token)
      localStorage.setItem('adminNickname', nickname || form.username)
      ElMessage.success('登录成功')
      router.push('/admin/dashboard')
    } else {
      ElMessage.error(res.data?.message || '登录失败')
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.message || '登录失败，请检查网络')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at 20% 20%, #0F2D54 0%, #081B33 60%, #040D1A 100%);
  position: relative;
  overflow: hidden;
  padding: 24px;
  box-sizing: border-box;
}

.login-bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
  opacity: 0.5;
}

.glow-1 {
  width: 400px;
  height: 400px;
  background: #0284C7;
  top: -100px;
  left: -100px;
}

.glow-2 {
  width: 500px;
  height: 500px;
  background: #0C3D70;
  bottom: -150px;
  right: -150px;
}

.login-card {
  width: 420px;
  max-width: 100%;
  padding: 44px 36px 36px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 24px 64px -12px rgba(0, 0, 0, 0.35), 0 0 0 1px rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 10;
  animation: bdisScaleIn 0.35s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-badge {
  display: inline-block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1.5px;
  color: #0284C7;
  background: #E0F2FE;
  padding: 3px 10px;
  border-radius: 20px;
  margin-bottom: 12px;
  border: 1px solid #BAE6FD;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: #0C3D70;
  margin: 0 0 6px;
  letter-spacing: 0.5px;
}

.login-subtitle {
  font-size: 13px;
  color: #64748B;
  margin: 0;
  letter-spacing: 0.3px;
}

:deep(.custom-input .el-input__wrapper) {
  border-radius: 10px;
  padding: 8px 14px;
  background: #F8FAFC;
}

.login-btn {
  width: 100%;
  height: 46px;
  background: linear-gradient(135deg, #0C3D70 0%, #0284C7 100%) !important;
  border: none !important;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
  border-radius: 10px !important;
  box-shadow: 0 4px 16px rgba(12, 61, 112, 0.25);
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  margin-top: 8px;
}

.login-btn:hover {
  background: linear-gradient(135deg, #104e8d 0%, #0396e0 100%) !important;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(12, 61, 112, 0.35);
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}

.back-link {
  font-size: 13px;
  color: #64748B;
  text-decoration: none;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
}

.back-link:hover {
  color: #0C3D70;
  transform: translateX(-2px);
}
</style>
