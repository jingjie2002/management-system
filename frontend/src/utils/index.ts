// // 工具函数

// /**
//  * 格式化日期
//  */
// export const formatDate = (date: string | Date, format: string = 'YYYY-MM-DD'): string => {
//   const d = new Date(date)
//   const year = d.getFullYear()
//   const month = String(d.getMonth() + 1).padStart(2, '0')
//   const day = String(d.getDate()).padStart(2, '0')
//   const hours = String(d.getHours()).padStart(2, '0')
//   const minutes = String(d.getMinutes()).padStart(2, '0')
//   const seconds = String(d.getSeconds()).padStart(2, '0')

//   return format
//     .replace('YYYY', year.toString())
//     .replace('MM', month)
//     .replace('DD', day)
//     .replace('HH', hours)
//     .replace('mm', minutes)
//     .replace('ss', seconds)
// }

// /**
//  * 格式化日期时间
//  */
// export const formatDateTime = (date: string | Date): string => {
//   return formatDate(date, 'YYYY-MM-DD HH:mm:ss')
// }

// /**
//  * 防抖函数
//  */
// export const debounce = <T extends (...args: any[]) => any>(
//   func: T,
//   delay: number
// ): ((...args: Parameters<T>) => void) => {
//   let timeoutId: NodeJS.Timeout
//   return (...args: Parameters<T>) => {
//     clearTimeout(timeoutId)
//     timeoutId = setTimeout(() => func(...args), delay)
//   }
// }

// /**
//  * 节流函数
//  */
// export const throttle = <T extends (...args: any[]) => any>(
//   func: T,
//   limit: number
// ): ((...args: Parameters<T>) => void) => {
//   let inThrottle: boolean
//   return (...args: Parameters<T>) => {
//     if (!inThrottle) {
//       func(...args)
//       inThrottle = true
//       setTimeout(() => inThrottle = false, limit)
//     }
//   }
// }

// /**
//  * 深拷贝
//  */
// export const deepClone = <T>(obj: T): T => {
//   if (obj === null || typeof obj !== 'object') return obj
//   if (obj instanceof Date) return new Date(obj.getTime()) as unknown as T
//   if (obj instanceof Array) return obj.map(item => deepClone(item)) as unknown as T
//   if (typeof obj === 'object') {
//     const clonedObj = {} as { [key: string]: any }
//     for (const key in obj) {
//       if (obj.hasOwnProperty(key)) {
//         clonedObj[key] = deepClone(obj[key])
//       }
//     }
//     return clonedObj as T
//   }
//   return obj
// }

// /**
//  * 生成UUID
//  */
// export const generateUUID = (): string => {
//   return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
//     const r = Math.random() * 16 | 0
//     const v = c === 'x' ? r : (r & 0x3 | 0x8)
//     return v.toString(16)
//   })
// }

// /**
//  * 验证邮箱格式
//  */
// export const isValidEmail = (email: string): boolean => {
//   const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
//   return emailRegex.test(email)
// }

// /**
//  * 验证手机号格式（中国）
//  */
// export const isValidPhone = (phone: string): boolean => {
//   const phoneRegex = /^1[3-9]\d{9}$/
//   return phoneRegex.test(phone)
// }

// /**
//  * 文件大小格式化
//  */
// export const formatFileSize = (bytes: number): string => {
//   if (bytes === 0) return '0 B'
//   const k = 1024
//   const sizes = ['B', 'KB', 'MB', 'GB']
//   const i = Math.floor(Math.log(bytes) / Math.log(k))
//   return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
// }

// /**
//  * 下载文件
//  */
// export const downloadFile = (url: string, filename: string): void => {
//   const link = document.createElement('a')
//   link.href = url
//   link.download = filename
//   document.body.appendChild(link)
//   link.click()
//   document.body.removeChild(link)
// }

// /**
//  * 复制到剪贴板
//  */
// export const copyToClipboard = async (text: string): Promise<boolean> => {
//   try {
//     await navigator.clipboard.writeText(text)
//     return true
//   } catch (err) {
//     // 降级方案
//     const textArea = document.createElement('textarea')
//     textArea.value = text
//     document.body.appendChild(textArea)
//     textArea.select()
//     try {
//       document.execCommand('copy')
//       document.body.removeChild(textArea)
//       return true
//     } catch (fallbackErr) {
//       document.body.removeChild(textArea)
//       return false
//     }
//   }
// }



// 工具函数

/**
 * 格式化日期
 */
export const formatDate = (date: string | Date, format: string = 'YYYY-MM-DD'): string => {
  const d = new Date(date)  // 将输入的日期转换为 Date 类型
  const year = d.getFullYear()  // 获取年份
  const month = String(d.getMonth() + 1).padStart(2, '0')  // 获取月份并格式化为 2 位数
  const day = String(d.getDate()).padStart(2, '0')  // 获取日期并格式化为 2 位数
  const hours = String(d.getHours()).padStart(2, '0')  // 获取小时并格式化为 2 位数
  const minutes = String(d.getMinutes()).padStart(2, '0')  // 获取分钟并格式化为 2 位数
  const seconds = String(d.getSeconds()).padStart(2, '0')  // 获取秒钟并格式化为 2 位数

  // 返回根据传入的格式化字符串替换日期组件
  return format
    .replace('YYYY', year.toString())  // 替换年份
    .replace('MM', month)  // 替换月份
    .replace('DD', day)  // 替换日期
    .replace('HH', hours)  // 替换小时
    .replace('mm', minutes)  // 替换分钟
    .replace('ss', seconds)  // 替换秒钟
}

/**
 * 格式化日期时间
 */
export const formatDateTime = (date: string | Date): string => {
  return formatDate(date, 'YYYY-MM-DD HH:mm:ss')  // 调用 formatDate 函数并指定格式化为日期时间格式
}

/**
 * 防抖函数
 */
export const debounce = <T extends (...args: any[]) => any>(
  func: T,  // 需要防抖的函数
  delay: number  // 延迟时间
): ((...args: Parameters<T>) => void) => {  // 返回一个防抖后的函数
  let timeoutId: NodeJS.Timeout  // 定义 timeoutId 用于清除定时器
  return (...args: Parameters<T>) => {  // 定义防抖函数
    clearTimeout(timeoutId)  // 清除上一个定时器
    timeoutId = setTimeout(() => func(...args), delay)  // 设置新的定时器
  }
}

/**
 * 节流函数
 */
export const throttle = <T extends (...args: any[]) => any>(
  func: T,  // 需要节流的函数
  limit: number  // 执行的时间间隔
): ((...args: Parameters<T>) => void) => {  // 返回一个节流后的函数
  let inThrottle: boolean  // 标识函数是否正在节流
  return (...args: Parameters<T>) => {  // 定义节流函数
    if (!inThrottle) {  // 如果不在节流中
      func(...args)  // 执行函数
      inThrottle = true  // 设置为节流状态
      setTimeout(() => inThrottle = false, limit)  // 设置定时器来解除节流状态
    }
  }
}

/**
 * 深拷贝
 */
export const deepClone = <T>(obj: T): T => {  // 深拷贝函数
  if (obj === null || typeof obj !== 'object') return obj  // 如果不是对象或为 null，直接返回
  if (obj instanceof Date) return new Date(obj.getTime()) as unknown as T  // 如果是 Date 对象，返回新实例
  if (obj instanceof Array) return obj.map(item => deepClone(item)) as unknown as T  // 如果是数组，递归拷贝数组中的每一项
  if (typeof obj === 'object') {  // 如果是对象
    const clonedObj = {} as { [key: string]: any }  // 创建新对象
    for (const key in obj) {  // 遍历对象的每一项
      if (obj.hasOwnProperty(key)) {  // 确保该属性是对象的自身属性
        clonedObj[key] = deepClone(obj[key])  // 递归深拷贝属性值
      }
    }
    return clonedObj as T  // 返回深拷贝的对象
  }
  return obj  // 返回原对象
}

/**
 * 生成UUID
 */
export const generateUUID = (): string => {  // 生成 UUID 函数
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {  // 使用正则替换生成 UUID
    const r = Math.random() * 16 | 0  // 生成随机数
    const v = c === 'x' ? r : (r & 0x3 | 0x8)  // 根据规则生成字符
    return v.toString(16)  // 转为十六进制
  })
}

/**
 * 验证邮箱格式
 */
export const isValidEmail = (email: string): boolean => {  // 验证邮箱格式的函数
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/  // 定义邮箱的正则表达式
  return emailRegex.test(email)  // 测试邮箱是否符合正则表达式
}

/**
 * 验证手机号格式（中国）
 */
export const isValidPhone = (phone: string): boolean => {  // 验证手机号格式（中国）的函数
  const phoneRegex = /^1[3-9]\d{9}$/  // 定义手机号的正则表达式
  return phoneRegex.test(phone)  // 测试手机号是否符合正则表达式
}

/**
 * 文件大小格式化
 */
export const formatFileSize = (bytes: number): string => {  // 格式化文件大小函数
  if (bytes === 0) return '0 B'  // 如果大小为 0，返回 '0 B'
  const k = 1024  // 设置单位换算的基数
  const sizes = ['B', 'KB', 'MB', 'GB']  // 文件大小的单位
  const i = Math.floor(Math.log(bytes) / Math.log(k))  // 计算文件大小的单位索引
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]  // 返回格式化后的文件大小
}

/**
 * 下载文件
 */
export const downloadFile = (url: string, filename: string): void => {  // 下载文件的函数
  const link = document.createElement('a')  // 创建一个下载链接
  link.href = url  // 设置文件的 URL
  link.download = filename  // 设置下载文件的名称
  document.body.appendChild(link)  // 将链接添加到文档中
  link.click()  // 模拟点击下载
  document.body.removeChild(link)  // 下载后移除链接
}

/**
 * 复制到剪贴板
 */
export const copyToClipboard = async (text: string): Promise<boolean> => {  // 复制文本到剪贴板的函数
  try {
    await navigator.clipboard.writeText(text)  // 使用 Clipboard API 复制文本
    return true  // 复制成功，返回 true
  } catch (err) {  // 如果失败
    // 降级方案，使用 textarea 元素作为临时方式
    const textArea = document.createElement('textarea')  // 创建文本区域
    textArea.value = text  // 将文本赋值给文本区域
    document.body.appendChild(textArea)  // 将文本区域添加到文档中
    textArea.select()  // 选中文本
    try {
      document.execCommand('copy')  // 尝试执行复制操作
      document.body.removeChild(textArea)  // 复制成功后移除文本区域
      return true  // 复制成功，返回 true
    } catch (fallbackErr) {  // 如果降级方案失败
      document.body.removeChild(textArea)  // 移除文本区域
      return false  // 返回 false
    }
  }
}
