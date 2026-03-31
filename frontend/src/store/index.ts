// import { defineStore } from 'pinia'
// import { ref, computed } from 'vue'

// export const useUserStore = defineStore('user', () => {
//   const token = ref(localStorage.getItem('token') || '')
//   const userInfo = ref(null)

//   const isLoggedIn = computed(() => !!token.value)

//   const setToken = (newToken: string) => {
//     token.value = newToken
//     localStorage.setItem('token', newToken)
//   }

//   const clearToken = () => {
//     token.value = ''
//     userInfo.value = null
//     localStorage.removeItem('token')
//   }

//   const setUserInfo = (info: any) => {
//     userInfo.value = info
//   }

//   return {
//     token,
//     userInfo,
//     isLoggedIn,
//     setToken,
//     clearToken,
//     setUserInfo
//   }
// })

// export const useAppStore = defineStore('app', () => {
//   const loading = ref(false)
//   const sidebarCollapsed = ref(false)

//   const setLoading = (value: boolean) => {
//     loading.value = value
//   }

//   const toggleSidebar = () => {
//     sidebarCollapsed.value = !sidebarCollapsed.value
//   }

//   return {
//     loading,
//     sidebarCollapsed,
//     setLoading,
//     toggleSidebar
//   }
// })




import { defineStore } from 'pinia'  // 引入 Pinia 的定义 store 函数
import { ref, computed } from 'vue'  // 引入 Vue 的 ref 和 computed 函数

// 定义用户相关的 store
export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')  // 用于存储 token，初始值从 localStorage 获取
  const userInfo = ref(null)  // 用于存储用户信息，初始为 null

  // 计算属性，判断用户是否登录
  const isLoggedIn = computed(() => !!token.value)  // 如果 token 存在则为 true，表示已登录

  // 设置 token
  const setToken = (newToken: string) => {
    token.value = newToken  // 更新 token 的值
    localStorage.setItem('token', newToken)  // 将 token 存入 localStorage
  }

  // 清除 token
  const clearToken = () => {
    token.value = ''  // 清空 token
    userInfo.value = null  // 清空用户信息
    localStorage.removeItem('token')  // 移除 localStorage 中的 token
  }

  // 设置用户信息
  const setUserInfo = (info: any) => {
    userInfo.value = info  // 设置用户信息
  }

  // 返回 store 的状态和方法
  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    clearToken,
    setUserInfo
  }
})

// 定义应用相关的 store
export const useAppStore = defineStore('app', () => {
  const loading = ref(false)  // 用于存储加载状态，初始为 false
  const sidebarCollapsed = ref(false)  // 用于存储侧边栏是否折叠，初始为 false

  // 设置加载状态
  const setLoading = (value: boolean) => {
    loading.value = value  // 更新 loading 的值
  }

  // 切换侧边栏的折叠状态
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value  // 取反侧边栏的折叠状态
  }

  // 返回 store 的状态和方法
  return {
    loading,
    sidebarCollapsed,
    setLoading,
    toggleSidebar
  }
})