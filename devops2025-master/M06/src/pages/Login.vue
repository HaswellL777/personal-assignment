<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

// 默认填写测试账号，便于直接点击登录验证联通性
const form = reactive({ username: 'tester', password: '123456' })
const message = ref('')
const loading = ref(false)
const token = ref('')
const router = useRouter()

// 根据你的后端实际接口路径进行调整，例如：/api/v1/auth/login 或 /login
const LOGIN_PATH = '/api/v1/auth/login'

async function login() {
  message.value = ''
  token.value = ''
  if (!form.username || !form.password) {
    message.value = '请输入用户名和密码'
    return
  }
  loading.value = true
  try {
    const res = await fetch(LOGIN_PATH, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: form.username, password: form.password }),
    })
    const text = await res.text()
    // 尝试解析 JSON；后端可能返回纯文本或 JSON
    let data
    try { data = JSON.parse(text) } catch { data = text }

    if (res.ok) {
      message.value = '登录成功'
      // 常见返回格式为 { token: '...' } 或 { data: { token: '...' } }
      const maybeToken = data?.token || data?.data?.token || ''
      if (maybeToken) {
        token.value = maybeToken
        // 将 token 存储到本地，便于后续接口调用
        try { localStorage.setItem('auth_token', maybeToken) } catch {}
        // 跳转到 Home 页面
        router.push('/home')
      }
    } else {
      // 如果后端有错误消息，优先展示（兼容 FastAPI 的 { detail } 错误格式）
      let errMsg = '登录失败'
      if (typeof data === 'object') {
        errMsg = data.message || data.error || data.detail || errMsg
      } else if (typeof data === 'string') {
        errMsg = data
      }
      message.value = errMsg
      // 登录失败时清理本地可能存在的旧 token
      try { localStorage.removeItem('auth_token') } catch {}
    }
  } catch (e) {
    message.value = '网络错误或服务不可用'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-background">
      <div class="background-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">
            <i class="el-icon-s-platform"></i>
          </div>
          <h1 class="title">DevOps 管理平台</h1>
          <p class="subtitle">欢迎回来，请登录您的账户</p>
        </div>
        
        <div class="login-form">
          <div class="form-item">
            <label class="form-label">
              <i class="el-icon-user"></i>
              用户名
            </label>
            <input 
              v-model="form.username" 
              type="text" 
              placeholder="请输入用户名" 
              class="form-input"
            />
          </div>
          
          <div class="form-item">
            <label class="form-label">
              <i class="el-icon-lock"></i>
              密码
            </label>
            <input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码" 
              class="form-input"
              @keyup.enter="login"
            />
          </div>
          
          <button 
            :disabled="loading" 
            @click="login"
            class="login-button"
            :class="{ 'loading': loading }"
          >
            <span v-if="loading" class="loading-spinner"></span>
            {{ loading ? '登录中...' : '登录' }}
          </button>
          
          <div v-if="message" class="message" :class="{ 'success': message === '登录成功', 'error': message !== '登录成功' }">
            <i :class="message === '登录成功' ? 'el-icon-success' : 'el-icon-warning'"></i>
            {{ message }}
          </div>
          
          <div v-if="token" class="token-display">
            <div class="token-label">
              <i class="el-icon-key"></i>
              Token 已生成
            </div>
            <div class="token-value">{{ token }}</div>
          </div>
        </div>
        
        <div class="login-footer">
          <p class="footer-text">© 2024 DevOps 管理平台. All rights reserved.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 登录页面整体布局 */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 背景装饰 */
.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.background-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 登录容器 */
.login-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: slideUp 0.8s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 60px;
  height: 60px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 10px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 16px;
  color: #718096;
  margin: 0;
  font-weight: 400;
}

/* 表单样式 */
.login-form {
  margin-bottom: 30px;
}

.form-item {
  margin-bottom: 24px;
}

.form-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #4a5568;
  margin-bottom: 8px;
}

.form-label i {
  margin-right: 8px;
  color: #667eea;
}

.form-input {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: #f7fafc;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  transform: translateY(-1px);
}

.form-input::placeholder {
  color: #a0aec0;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  margin-bottom: 20px;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.login-button.loading {
  pointer-events: none;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
  margin-right: 8px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 消息提示 */
.message {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  margin-bottom: 16px;
  animation: fadeIn 0.3s ease;
}

.message i {
  margin-right: 8px;
  font-size: 16px;
}

.message.success {
  background: #f0fff4;
  color: #38a169;
  border: 1px solid #9ae6b4;
}

.message.error {
  background: #fed7d7;
  color: #e53e3e;
  border: 1px solid #feb2b2;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Token 显示 */
.token-display {
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.token-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #4a5568;
  margin-bottom: 8px;
}

.token-label i {
  margin-right: 8px;
  color: #38a169;
}

.token-value {
  font-size: 12px;
  color: #718096;
  word-break: break-all;
  background: white;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  font-family: 'Courier New', monospace;
}

/* 页脚 */
.login-footer {
  text-align: center;
  border-top: 1px solid #e2e8f0;
  padding-top: 20px;
}

.footer-text {
  font-size: 12px;
  color: #a0aec0;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-container {
    padding: 16px;
  }
  
  .login-card {
    padding: 30px 20px;
    border-radius: 16px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .subtitle {
    font-size: 14px;
  }
  
  .form-input {
    padding: 12px 14px;
    font-size: 14px;
  }
  
  .login-button {
    padding: 14px;
    font-size: 14px;
  }
}

/* 暗色模式支持 */
@media (prefers-color-scheme: dark) {
  .login-card {
    background: rgba(26, 32, 44, 0.95);
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .title {
    color: #f7fafc;
  }
  
  .subtitle {
    color: #a0aec0;
  }
  
  .form-label {
    color: #e2e8f0;
  }
  
  .form-input {
    background: #2d3748;
    border-color: #4a5568;
    color: #f7fafc;
  }
  
  .form-input:focus {
    background: #1a202c;
    border-color: #667eea;
  }
  
  .token-display {
    background: #2d3748;
    border-color: #4a5568;
  }
  
  .token-value {
    background: #1a202c;
    border-color: #4a5568;
    color: #e2e8f0;
  }
  
  .login-footer {
    border-top-color: #4a5568;
  }
}
</style>