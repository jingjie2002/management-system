// import axios from 'axios'
// import { ElMessage } from 'element-plus'

// const http = axios.create({
//   baseURL: '/api',
//   timeout: 10000
// })

// http.interceptors.request.use(config => {
//   const token = localStorage.getItem('token')
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`
//   }
//   return config
// })

// http.interceptors.response.use(
//   res => {
//     // 后端统一返回 Result 对象
//     if (res.data?.code !== 200) {
//       const msg = res.data?.message || '请求失败'
//       if (res.data?.code === 401) {
//         localStorage.removeItem('token')
//         localStorage.removeItem('roleKeys')
//         if (location.pathname !== '/login') {
//           location.href = '/login'
//         }
//       }
//       ElMessage.error(msg)
//       return Promise.reject(new Error(msg))
//     }
//     return res.data
//   },
//   err => {
//     // 网络或服务器不可达
//     if (!err.response) {
//       ElMessage.error('无法连接到服务器，请检查后端是否已启动')
//     } else {
//       if (err.response.status === 401) {
//         localStorage.removeItem('token')
//         localStorage.removeItem('roleKeys')
//         if (location.pathname !== '/login') {
//           location.href = '/login'
//         }
//       }
//       const msg = err.response.data?.message || '请求失败'
//       ElMessage.error(msg)
//     }
//     return Promise.reject(err)
//   }
// )

// export default http


import axios from 'axios'  // 引入 axios 库用于发送 HTTP 请求
import { ElMessage } from 'element-plus'  // 引入 Element Plus 提示框组件，用于弹出消息

// 创建 axios 实例，设置请求的基础配置
const http = axios.create({
  baseURL: '/api',  // 使用相对路径，通过vite代理访问后端
  timeout: 10000,  // 设置请求的超时时间为 10 秒
  withCredentials: true  // 允许携带cookies，用于共享session
})

// 请求拦截器，在请求发送之前进行一些处理
http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')  // 从 localStorage 获取保存的 token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`  // 如果 token 存在，设置请求头中的 Authorization 字段
  }
  return config  // 返回配置后的请求参数
})

// 响应拦截器，对服务器返回的响应进行处理
http.interceptors.response.use(
  res => {
    // 对于blob类型的响应，直接返回，不进行JSON解析
    if (res.config.responseType === 'blob') {
      return res.data
    }
    // 后端统一返回 Result 对象，检查返回的数据是否成功
    if (res.data?.code !== 200) {  // 如果返回的 code 不是 200，表示请求失败
      const msg = res.data?.message || '请求失败'  // 获取错误信息，如果没有则默认 '请求失败'
      if (res.data?.code === 401) {  // 如果返回的 code 是 401，表示用户未登录或 token 过期
        localStorage.removeItem('token')  // 移除本地存储中的 token
        localStorage.removeItem('roleKeys')  // 移除本地存储中的 roleKeys
        if (location.pathname !== '/login') {  // 如果当前路径不是登录页
          location.href = '/login'  // 跳转到登录页
        }
      }
      ElMessage.error(msg)  // 使用 Element Plus 的提示框显示错误信息
      return Promise.reject(new Error(msg))  // 返回一个 rejected 状态的 Promise，终止后续操作
    }
    return res.data  // 如果请求成功，返回响应的数据
  },
  err => {
    // 如果没有响应（网络问题或服务器不可达）
    if (!err.response) {
      ElMessage.error('无法连接到服务器，请检查后端是否已启动')  // 提示无法连接到服务器
    } else {
      // 如果响应状态码是 401，表示未登录或 token 过期
      if (err.response.status === 401) {
        localStorage.removeItem('token')  // 移除本地存储中的 token
        localStorage.removeItem('roleKeys')  // 移除本地存储中的 roleKeys
        if (location.pathname !== '/login') {  // 如果当前路径不是登录页
          location.href = '/login'  // 跳转到登录页
        }
      }
      const msg = err.response.data?.message || '请求失败'  // 获取错误信息，如果没有则默认 '请求失败'
      ElMessage.error(msg)  // 使用 Element Plus 的提示框显示错误信息
    }
    return Promise.reject(err)  // 返回一个 rejected 状态的 Promise，终止后续操作
  }
)

// 导出封装的 http 实例，方便其他文件引用
export default http