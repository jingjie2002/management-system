<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon class="logo-icon"><Management /></el-icon>
        <span v-show="!isCollapse">HR System</span>
      </div>
      <el-menu
        router
        :default-active="$route.path"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        class="el-menu-vertical"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>图表数据</template>
        </el-menu-item>

        <el-menu-item v-if="canAccess('department')" index="/department">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>组织架构</template>
        </el-menu-item>
        <el-menu-item v-if="canAccess('employee')" index="/employee">
          <el-icon><User /></el-icon>
          <template #title>员工管理</template>
        </el-menu-item>
        <el-menu-item v-if="canAccess('jobLevel')" index="/job-level">
          <el-icon><Medal /></el-icon>
          <template #title>职级管理</template>
        </el-menu-item>
        <el-menu-item v-if="canAccess('employeeChange')" index="/employee-change">
          <el-icon><Switch /></el-icon>
          <template #title>异动管理</template>
        </el-menu-item>
        <el-menu-item v-if="canAccess('myChange')" index="/my-change">
          <el-icon><Promotion /></el-icon>
          <template #title>我的异动</template>
        </el-menu-item>
        <el-menu-item index="/attendance">
          <el-icon><Calendar /></el-icon>
          <template #title>考勤管理</template>
        </el-menu-item>
        <el-menu-item index="/leave">
          <el-icon><Memo /></el-icon>
          <template #title>请假管理</template>
        </el-menu-item>
        <el-menu-item v-if="canAccess('project')" index="/project">
          <el-icon><List /></el-icon>
          <template #title>项目管理</template>
        </el-menu-item>
        <el-menu-item index="/performance">
          <el-icon><TrendCharts /></el-icon>
          <template #title>绩效管理</template>
        </el-menu-item>
        <el-menu-item index="/training">
          <el-icon><Reading /></el-icon>
          <template #title>培训管理</template>
        </el-menu-item>
        <el-menu-item index="/notice">
          <el-icon><Bell /></el-icon>
          <template #title>通知公告</template>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><UserFilled /></el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="trigger" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <div class="system-title">企业员工精细化人事管理系统</div>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title || $route.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="clock">{{ nowText }}</span>
          <el-dropdown trigger="click">
            <div class="avatar-wrapper">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">{{ currentUsername }}</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="switchAccount('admin')">切换为管理员</el-dropdown-item>
                <el-dropdown-item @click="switchAccount('hr')">切换为人事</el-dropdown-item>
                <el-dropdown-item @click="switchAccount('employee')">切换为员工</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Bell,
  Calendar,
  CaretBottom,
  Expand,
  Fold,
  List,
  Management,
  Medal,
  Memo,
  Odometer,
  OfficeBuilding,
  Promotion,
  Reading,
  Switch,
  TrendCharts,
  User,
  UserFilled
} from '@element-plus/icons-vue'
import { canAccessModule, readRoleKeys } from '../utils/access'

const router = useRouter()
const isCollapse = ref(false)
const currentUsername = ref(localStorage.getItem('username') || 'admin')
const nowText = ref('')
const roleKeys = ref<string[]>([])
let timer = 0

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('roleKeys')
  router.push('/login')
}

const switchAccount = (username: string) => {
  localStorage.removeItem('token')
  localStorage.removeItem('roleKeys')
  localStorage.setItem('switchTo', username)
  router.push('/login')
}

const canAccess = (
  module: 'department' | 'employee' | 'project' | 'jobLevel' | 'myChange' | 'employeeChange'
) => canAccessModule(roleKeys.value, module)

const refreshTime = () => {
  nowText.value = new Date().toLocaleString('zh-CN')
}

onMounted(() => {
  roleKeys.value = readRoleKeys()
  refreshTime()
  timer = window.setInterval(refreshTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.28s;
  overflow: hidden;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
}

.logo {
  height: 50px;
  line-height: 50px;
  text-align: center;
  color: #fff;
  font-weight: 600;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
}

.logo-icon {
  margin-right: 8px;
  font-size: 24px;
  color: #409EFF;
}

.el-menu-vertical {
  border-right: none;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 50px;
}

.header-left {
  display: flex;
  align-items: center;
}

.system-title {
  margin-right: 16px;
  font-size: 14px;
  color: #606266;
}

.trigger {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.clock {
  font-size: 13px;
  color: #909399;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 8px;
  font-size: 14px;
}

.main {
  background-color: transparent;
  padding: 20px;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
