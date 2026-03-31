import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^api/, '') // 移除 rewrite，因为后端 controller 路径确实包含 /api
      }
    }
  },
  define: {
    __APP_TITLE__: JSON.stringify(process.env.VITE_APP_TITLE || '企业员工管理系统')
  }
})
