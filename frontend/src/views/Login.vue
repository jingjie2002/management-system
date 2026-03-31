<template>
  <div class="login-container">
    <div class="login-background">
      <div class="login-form">
        <div class="logo-section">
          <el-icon class="logo-icon"><Management /></el-icon>
          <h1>企业员工管理系统</h1>
          <p>精细化管理平台</p>
        </div>

        <el-card class="login-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>用户登录</span>
            </div>
          </template>

          <el-form
            :model="form"
            :rules="rules"
            ref="formRef"
            @submit.prevent="onSubmit"
            label-position="top"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>

            <el-form-item label="验证码" prop="captcha">
              <div class="captcha-container">
                <el-input
                  v-model="form.captcha"
                  placeholder="请输入验证码"
                  size="large"
                  :prefix-icon="View"
                  clearable
                />
                <div class="captcha-image" @click="refreshCaptcha" :class="{ 'loading': captchaLoading }">
                  <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
                  <el-icon v-else><Loading /></el-icon>
                </div>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="onSubmit"
                style="width: 100%"
              >
                登录系统
              </el-button>
            </el-form-item>
          </el-form>

          <div class="demo-account">
            <el-divider>演示账号</el-divider>
            <div class="demo-actions">
              <el-button text type="primary" @click="useDemo('admin')">admin / 123456</el-button>
              <el-button text type="primary" @click="useDemo('hr')">hr / 123456</el-button>
              <el-button text type="primary" @click="useDemo('employee')">employee / 123456</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Management, View, Loading } from '@element-plus/icons-vue'
import http from '../api/http'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const captchaImage = ref('') // 存储验证码图片的Base64数据
const captchaLoading = ref(false) // 验证码加载状态

const form = reactive({
  username: '',
  password: '',
  captcha: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// 刷新验证码
const refreshCaptcha = async () => {
  captchaLoading.value = true
  try {
    // 调用后端接口获取验证码
    const res = await http.get('/auth/captcha', {
      params: { timestamp: Date.now() }
    })
    // 设置验证码图片
    captchaImage.value = res.data.image
  } catch (error) {
    ElMessage.error('获取验证码失败，请重试')
    console.error('获取验证码失败:', error)
  } finally {
    captchaLoading.value = false
  }
}

// 登录提交：校验表单后调用后端鉴权接口
const onSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const res = await http.post('/auth/login', form)
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('username', form.username)
        const profile = await http.get('/auth/profile')
        localStorage.setItem('roleKeys', JSON.stringify(profile.data.roles || []))
        ElMessage.success('登录成功！')
        router.push('/dashboard')
      } catch (error) {
        refreshCaptcha()
      } finally {
        loading.value = false
      }
    }
  })
}

// 使用演示账号
const useDemo = (username: string) => {
  form.username = username
  form.password = '123456'
  form.captcha = ''
  refreshCaptcha()
}

// 从“切换账号”动作回到登录页时自动填充目标账号
onMounted(() => {
  const switchTo = localStorage.getItem('switchTo')
  if (switchTo) {
    useDemo(switchTo)
    localStorage.removeItem('switchTo')
  }
  localStorage.removeItem('token')
  localStorage.removeItem('roleKeys')
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(120deg, #4f46e5, #0ea5e9, #14b8a6, #6366f1);
  background-size: 300% 300%;
  animation: login-bg-flow 16s ease infinite;
}

.login-background {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="10" r="0.5" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
}

.login-form {
  width: 400px;
  max-width: 90vw;
}

.logo-section {
  text-align: center;
  margin-bottom: 30px;
  color: white;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.logo-section h1 {
  font-size: 24px;
  margin-bottom: 5px;
  font-weight: 600;
}

.logo-section p {
  font-size: 14px;
  opacity: 0.8;
}

.login-card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-color-primary);
}

.captcha-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-container .el-input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
  overflow: hidden;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-image.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
}

.captcha-image.loading .el-icon {
  font-size: 20px;
  color: #409eff;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.demo-account {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  text-align: center;
}

.demo-account p {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.demo-actions {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

@keyframes login-bg-flow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 响应式布局 */
@media (max-width: 480px) {
  .login-form {
    width: 100%;
    padding: 20px;
  }

  .logo-section h1 {
    font-size: 20px;
  }

  .captcha-container {
    flex-direction: column;
    align-items: stretch;
  }

  .captcha-image {
    width: 100%;
    height: 50px;
  }
}
</style>
