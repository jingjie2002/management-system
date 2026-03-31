import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// simple health check before mounting
import http from './api/http'
http.get('/health').catch(() => {
  const { ElMessage } = require('element-plus')
  ElMessage.error('后端服务未响应，请确保后端已启动')
})

app.mount('#app')
