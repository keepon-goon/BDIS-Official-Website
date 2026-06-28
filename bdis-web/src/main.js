import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/iconfont/iconfont.css'
import './style.css'

import axios from 'axios'

axios.interceptors.request.use(config => {
  if (config.url?.startsWith('/api/admin/') && !config.url.includes('/api/admin/login')) {
    const token = localStorage.getItem('adminToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  }
  return config
})

axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401 && error.config.url?.startsWith('/api/admin/')) {
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminNickname')
      router.push('/admin/login')
    }
    return Promise.reject(error)
  }
)

if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK === 'true') {
  const { worker } = await import('./mocks/browser')
  await worker.start()
  console.log('MSW 已启动')
}

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
