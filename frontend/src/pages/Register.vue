<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const form = reactive({ username: '', password: '', confirmPassword: '', email: '' })
const message = ref('')
const loading = ref(false)
const router = useRouter()

const REGISTER_PATH = '/api/v1/auth/register'

async function register() {
  message.value = ''
  if (!form.username || !form.password || !form.email) {
    message.value = '请填写所有必填项'
    return
  }
  if (form.password !== form.confirmPassword) {
    message.value = '两次输入的密码不一致'
    return
  }
  loading.value = true
  try {
    const res = await fetch(REGISTER_PATH, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: form.username, password: form.password, email: form.email }),
    })
    const text = await res.text()
    let data
    try { data = JSON.parse(text) } catch { data = text }

    if (res.ok && data?.code === 0) {
      message.value = '注册成功，即将跳转登录页...'
      setTimeout(() => router.push('/'), 1500)
    } else {
      let errMsg = '注册失败'
      if (typeof data === 'object') {
        errMsg = data.message || data.error || data.detail || errMsg
      } else if (typeof data === 'string') {
        errMsg = data
      }
      message.value = errMsg
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
          <p class="subtitle">创建您的账户</p>
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
              <i class="el-icon-message"></i>
              邮箱
            </label>
            <input
              v-model="form.email"
              type="email"
              placeholder="请输入邮箱"
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
            />
          </div>

          <div class="form-item">
            <label class="form-label">
              <i class="el-icon-lock"></i>
              确认密码
            </label>
            <input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              class="form-input"
              @keyup.enter="register"
            />
          </div>

          <button
            :disabled="loading"
            @click="register"
            class="login-button"
            :class="{ 'loading': loading }"
          >
            <span v-if="loading" class="loading-spinner"></span>
            {{ loading ? '注册中...' : '注册' }}
          </button>

          <div v-if="message" class="message" :class="{ 'success': message.includes('成功'), 'error': !message.includes('成功') }">
            <i :class="message.includes('成功') ? 'el-icon-success' : 'el-icon-warning'"></i>
            {{ message }}
          </div>

          <div class="register-link">
            已有账户？<router-link to="/">立即登录</router-link>
          </div>
        </div>

        <div class="login-footer">
          <p class="footer-text">© 2025 DevOps 管理平台. All rights reserved.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

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

.login-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

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

.login-header {
  text-align: center;
  margin-bottom: 30px;
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

.login-form {
  margin-bottom: 30px;
}

.form-item {
  margin-bottom: 20px;
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

.register-link {
  text-align: center;
  font-size: 14px;
  color: #718096;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.register-link a:hover {
  text-decoration: underline;
}

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

  .register-link {
    color: #a0aec0;
  }

  .login-footer {
    border-top-color: #4a5568;
  }
}
</style>
