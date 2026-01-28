// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/iconfont/iconfont.css'
import './style.css'

// 先在开发环境里启动 MSW
if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK === 'true') {
  const { worker } = await import('./mocks/browser')
  await worker.start()
  console.log('MSW 已启动')
}

// 再创建应用
const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
