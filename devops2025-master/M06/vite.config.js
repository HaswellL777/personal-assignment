import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // 优先将认证相关的接口代理到本地 FastAPI 模拟后端
      '/api/v1/auth': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
      // 菜单接口也走本地模拟后端
      '/api/v1/menus': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
      // 用户信息接口走本地模拟后端
      '/api/v1/user': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
      // 管理员接口走本地模拟后端
      '/api/v1/admin': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
      // 其他 /api 路径仍可按需转发到远端后端
      '/api': {
        target: 'http://10.129.2.112:8080',
        changeOrigin: true,
      },
    },
  },
})
