import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  // 核心：代理配置（必须和后端接口匹配）
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/uploads': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      }
    }
  }
})


// import { defineConfig } from 'vite'
// import vue from '@vitejs/plugin-vue'
// import path from 'path'

// export default defineConfig(({ mode }) => {
//   const isMock = mode === 'mock'

//   return {
//     plugins: [vue()],
//     resolve: {
//       alias: {
//         '@': path.resolve(__dirname, 'src'),
//       },
//     },
//     server: {
//       proxy: isMock
//         ? {}
//         : {
//           '/api': {
//             target: 'http://localhost:8080',
//             changeOrigin: true,
//           },
//         },
//     },
//   }
// })
