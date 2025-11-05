/**
 * HTTP 请求封装工具
 *
 * 功能：
 * 1. 自动添加 Authorization header（从 localStorage 获取 token）
 * 2. 统一处理 401 错误（token 失效时清除并跳转登录页）
 * 3. 统一的错误处理
 */

const request = async (url, options = {}) => {
  // 从 localStorage 获取 token
  const token = localStorage.getItem('token')

  // 构建请求头
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
    ...options.headers,
  }

  try {
    // 发送请求
    const response = await fetch(url, {
      ...options,
      headers,
    })

    // 处理 401 未授权错误
    if (response.status === 401) {
      // 清除 token
      localStorage.removeItem('token')
      // 跳转到登录页
      window.location.href = '/'
      return
    }

    // 返回 JSON 数据
    return response.json()
  } catch (error) {
    console.error('Request error:', error)
    throw error
  }
}

export default request
