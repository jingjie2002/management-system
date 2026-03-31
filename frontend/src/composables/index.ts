// import { ref, onMounted, onUnmounted } from 'vue'

// /**
//  * 窗口大小监听组合函数
//  */
// export function useWindowSize() {
//   const width = ref(window.innerWidth)
//   const height = ref(window.innerHeight)

//   const updateSize = () => {
//     width.value = window.innerWidth
//     height.value = window.innerHeight
//   }

//   onMounted(() => {
//     window.addEventListener('resize', updateSize)
//   })

//   onUnmounted(() => {
//     window.removeEventListener('resize', updateSize)
//   })

//   return { width, height }
// }

// /**
//  * 本地存储组合函数
//  */
// export function useLocalStorage<T>(key: string, defaultValue: T) {
//   const value = ref<T>(defaultValue)

//   const read = () => {
//     try {
//       const item = localStorage.getItem(key)
//       if (item !== null) {
//         value.value = JSON.parse(item)
//       }
//     } catch (error) {
//       console.error(`Error reading localStorage key "${key}":`, error)
//     }
//   }

//   const write = () => {
//     try {
//       localStorage.setItem(key, JSON.stringify(value.value))
//     } catch (error) {
//       console.error(`Error writing localStorage key "${key}":`, error)
//     }
//   }

//   const remove = () => {
//     try {
//       localStorage.removeItem(key)
//       value.value = defaultValue
//     } catch (error) {
//       console.error(`Error removing localStorage key "${key}":`, error)
//     }
//   }

//   onMounted(read)

//   return {
//     value,
//     read,
//     write,
//     remove
//   }
// }

// /**
//  * 定时器组合函数
//  */
// export function useInterval(callback: () => void, delay: number) {
//   const intervalId = ref<NodeJS.Timeout | null>(null)

//   const start = () => {
//     if (intervalId.value !== null) return
//     intervalId.value = setInterval(callback, delay)
//   }

//   const stop = () => {
//     if (intervalId.value !== null) {
//       clearInterval(intervalId.value)
//       intervalId.value = null
//     }
//   }

//   onMounted(start)
//   onUnmounted(stop)

//   return { start, stop }
// }

// /**
//  * 异步数据加载组合函数
//  */
// export function useAsyncData<T>(
//   asyncFn: () => Promise<T>,
//   initialData: T
// ) {
//   const data = ref<T>(initialData)
//   const loading = ref(false)
//   const error = ref<string | null>(null)

//   const execute = async () => {
//     loading.value = true
//     error.value = null
//     try {
//       data.value = await asyncFn()
//     } catch (err) {
//       error.value = err instanceof Error ? err.message : 'Unknown error'
//     } finally {
//       loading.value = false
//     }
//   }

//   onMounted(execute)

//   return {
//     data,
//     loading,
//     error,
//     execute
//   }
// }

// /**
//  * 表单验证组合函数
//  */
// export function useFormValidation() {
//   const errors = ref<Record<string, string>>({})

//   const validate = (rules: Record<string, (value: any) => string | null>) => {
//     errors.value = {}
//     let isValid = true

//     for (const [field, rule] of Object.entries(rules)) {
//       const error = rule((this as any)[field])
//       if (error) {
//         errors.value[field] = error
//         isValid = false
//       }
//     }

//     return isValid
//   }

//   const clearErrors = () => {
//     errors.value = {}
//   }

//   const hasErrors = (field?: string) => {
//     if (field) {
//       return !!errors.value[field]
//     }
//     return Object.keys(errors.value).length > 0
//   }

//   return {
//     errors,
//     validate,
//     clearErrors,
//     hasErrors
//   }
// }



import { ref, onMounted, onUnmounted } from 'vue'  // 引入 vue 相关的 API（ref, onMounted, onUnmounted）

/**
 * 窗口大小监听组合函数
 */
export function useWindowSize() {
  const width = ref(window.innerWidth)  // 创建 ref 变量来存储窗口的宽度
  const height = ref(window.innerHeight)  // 创建 ref 变量来存储窗口的高度

  // 更新窗口尺寸的函数
  const updateSize = () => {
    width.value = window.innerWidth  // 更新宽度
    height.value = window.innerHeight  // 更新高度
  }

  // 在组件挂载时添加 resize 事件监听器
  onMounted(() => {
    window.addEventListener('resize', updateSize)  // 监听窗口尺寸变化事件
  })

  // 在组件卸载时移除 resize 事件监听器
  onUnmounted(() => {
    window.removeEventListener('resize', updateSize)  // 移除事件监听器
  })

  // 返回宽度和高度供外部访问
  return { width, height }
}

/**
 * 本地存储组合函数
 */
export function useLocalStorage<T>(key: string, defaultValue: T) {
  const value = ref<T>(defaultValue)  // 初始化值为默认值

  // 从 localStorage 读取数据的函数
  const read = () => {
    try {
      const item = localStorage.getItem(key)  // 从 localStorage 获取指定 key 的值
      if (item !== null) {
        value.value = JSON.parse(item)  // 如果数据存在，将其解析并存入 value
      }
    } catch (error) {
      console.error(`Error reading localStorage key "${key}":`, error)  // 如果读取出错，打印错误信息
    }
  }

  // 向 localStorage 写入数据的函数
  const write = () => {
    try {
      localStorage.setItem(key, JSON.stringify(value.value))  // 将 value 写入 localStorage
    } catch (error) {
      console.error(`Error writing localStorage key "${key}":`, error)  // 如果写入出错，打印错误信息
    }
  }

  // 移除 localStorage 中的数据的函数
  const remove = () => {
    try {
      localStorage.removeItem(key)  // 从 localStorage 删除指定 key 的值
      value.value = defaultValue  // 重置 value 为默认值
    } catch (error) {
      console.error(`Error removing localStorage key "${key}":`, error)  // 如果移除出错，打印错误信息
    }
  }

  // 在组件挂载时读取 localStorage 中的数据
  onMounted(read)

  // 返回相关方法和存储的值供外部访问
  return {
    value,
    read,
    write,
    remove
  }
}

/**
 * 定时器组合函数
 */
export function useInterval(callback: () => void, delay: number) {
  const intervalId = ref<NodeJS.Timeout | null>(null)  // 存储定时器的 ID，初始化为 null

  // 启动定时器的函数
  const start = () => {
    if (intervalId.value !== null) return  // 如果定时器已经启动，则不再启动新的定时器
    intervalId.value = setInterval(callback, delay)  // 设置定时器
  }

  // 停止定时器的函数
  const stop = () => {
    if (intervalId.value !== null) {
      clearInterval(intervalId.value)  // 清除定时器
      intervalId.value = null  // 重置定时器 ID
    }
  }

  // 在组件挂载时启动定时器
  onMounted(start)
  // 在组件卸载时停止定时器
  onUnmounted(stop)

  // 返回启动和停止定时器的函数
  return { start, stop }
}

/**
 * 异步数据加载组合函数
 */
export function useAsyncData<T>(
  asyncFn: () => Promise<T>,  // 异步数据加载函数
  initialData: T  // 初始化数据
) {
  const data = ref<T>(initialData)  // 存储数据
  const loading = ref(false)  // 加载状态，初始为 false
  const error = ref<string | null>(null)  // 错误信息，初始为 null

  // 执行异步加载的函数
  const execute = async () => {
    loading.value = true  // 开始加载
    error.value = null  // 清除旧的错误信息
    try {
      data.value = await asyncFn()  // 调用异步函数加载数据
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Unknown error'  // 如果出错，保存错误信息
    } finally {
      loading.value = false  // 加载完成
    }
  }

  // 在组件挂载时执行异步数据加载
  onMounted(execute)

  // 返回数据、加载状态、错误信息和执行函数
  return {
    data,
    loading,
    error,
    execute
  }
}

/**
 * 表单验证组合函数
 */
export function useFormValidation() {
  const errors = ref<Record<string, string>>({})  // 存储表单字段的错误信息

  // 验证表单数据的函数
  const validate = (rules: Record<string, (value: any) => string | null>) => {
    errors.value = {}  // 清空错误信息
    let isValid = true  // 表单是否有效

    // 遍历所有字段的验证规则
    for (const [field, rule] of Object.entries(rules)) {
      const error = rule((this as any)[field])  // 执行规则并获取错误信息
      if (error) {
        errors.value[field] = error  // 如果有错误，保存错误信息
        isValid = false  // 标记表单无效
      }
    }

    return isValid  // 返回验证结果
  }

  // 清除所有错误信息
  const clearErrors = () => {
    errors.value = {}
  }

  // 检查是否有错误
  const hasErrors = (field?: string) => {
    if (field) {
      return !!errors.value[field]  // 检查指定字段是否有错误
    }
    return Object.keys(errors.value).length > 0  // 检查是否有任何错误
  }

  // 返回错误信息、验证方法、清除错误和检查是否有错误的方法
  return {
    errors,
    validate,
    clearErrors,
    hasErrors
  }
}