<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <header class="layout-header">
      <div class="header-content">
        <div class="system-title">
          <el-icon class="system-icon"><Platform /></el-icon>
          <span>智能体创作平台</span>
        </div>
        <div class="header-actions">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar" />
              <span class="username">{{ currentUser }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体内容区域 -->
    <div class="layout-body">
      <!-- 左侧菜单 -->
      <aside class="layout-sidebar">
        <div class="sidebar-content">
          <div v-if="loading" class="menu-loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载菜单中...</span>
          </div>
          <div v-else-if="error" class="menu-error">
            <el-alert :title="error" type="error" show-icon />
          </div>
          <el-menu
            v-else
            :default-active="activeMenu"
            class="sidebar-menu"
            @select="handleMenuSelect"
            router
          >
            <el-menu-item
              v-for="item in menus"
              :key="item.path"
              :index="item.path"
            >
              <el-icon>
                <component :is="getMenuIcon(item.icon)" />
              </el-icon>
              <span>{{ item.name }}</span>
            </el-menu-item>
          </el-menu>
        </div>
      </aside>

      <!-- 右侧内容区域 -->
      <main class="layout-main">
        <div class="main-content">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Platform,
  ArrowDown,
  Loading,
  House,
  Grid,
  User,
  UserFilled,
  Setting,
  Lock,
  ChatDotRound
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const menus = ref([])
const loading = ref(false)
const error = ref('')
const currentUser = ref('')
const userAvatar = ref('')

// 计算当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 获取菜单图标组件
const getMenuIcon = (iconName) => {
  const iconMap = {
    dashboard: House,
    apps: Grid,
    user: User,
    users: UserFilled,
    roles: Setting,
    shield: Lock,
    chat: ChatDotRound
  }
  return iconMap[iconName] || House
}

// 获取菜单数据
const fetchMenus = async () => {
  error.value = ''
  loading.value = true
  try {
    const token = localStorage.getItem('token') || ''
    const res = await fetch('/api/v1/menus', {
      headers: {
        'Accept': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      }
    })

    const text = await res.text()
    let data
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }

    if (res.ok) {
      menus.value = data?.data || []
      // 从token中提取用户名（支持mock-token和真实JWT）
      if (token.startsWith('mock-token-')) {
        currentUser.value = token.replace('mock-token-', '')
      } else if (token) {
        // 解析JWT token获取用户名
        try {
          const payload = JSON.parse(atob(token.split('.')[1]))
          currentUser.value = payload.sub || payload.username || '用户'
        } catch {
          currentUser.value = '用户'
        }
      }
      userAvatar.value = `https://api.dicebear.com/7.x/avataaars/svg?seed=${currentUser.value}`
    } else {
      const msg = typeof data === 'object' ?
        (data.message || data.error || data.detail || '菜单加载失败') :
        String(data)
      error.value = msg
      if (res.status === 401) {
        // 无权限/未登录，跳回登录页
        handleLogout()
      }
    }
  } catch (e) {
    error.value = '网络错误或服务不可用'
  } finally {
    loading.value = false
  }
}

// 处理菜单选择
const handleMenuSelect = (index) => {
  // 路由导航由 el-menu 的 router 属性自动处理
  console.log('Menu selected:', index)
}

// 处理用户下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/home/profile')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    localStorage.removeItem('token')
    ElMessage.success('已退出登录')
    router.push('/')
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  fetchMenus()
})
</script>

<style scoped>
.layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 顶部导航栏 */
.layout-header {
  height: 64px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  position: relative;
}

.layout-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  pointer-events: none;
}

.header-content {
  height: 100%;
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  z-index: 1;
}

.system-title {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
  letter-spacing: 0.5px;
}

.system-icon {
  margin-right: 12px;
  font-size: 28px;
  color: #667eea;
  filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.3));
}

.header-actions {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 10px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.95);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.username {
  margin: 0 12px;
  color: #2c3e50;
  font-weight: 500;
}

/* 主体内容区域 */
.layout-body {
  flex: 1;
  display: flex;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

/* 左侧菜单 */
.layout-sidebar {
  width: 260px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  border-right: 1px solid rgba(226, 232, 240, 0.8);
  overflow-y: auto;
}

.sidebar-content {
  height: 100%;
  padding: 20px 0;
}

.menu-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #64748b;
}

.menu-loading .el-icon {
  margin-bottom: 12px;
  font-size: 24px;
  color: #667eea;
}

.menu-error {
  padding: 20px;
  margin: 0 16px;
}

.sidebar-menu {
  border-right: none;
  background: transparent;
  padding: 0 16px;
}

.sidebar-menu .el-menu-item {
  height: 52px;
  line-height: 52px;
  margin-bottom: 8px;
  border-radius: 12px;
  transition: all 0.3s ease;
  color: #475569;
  font-weight: 500;
}

.sidebar-menu .el-menu-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  transform: translateX(4px);
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.sidebar-menu .el-menu-item .el-icon {
  margin-right: 12px;
  font-size: 18px;
}

/* 右侧内容区域 */
.layout-main {
  flex: 1;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  overflow-y: auto;
}

.main-content {
  padding: 32px;
  min-height: calc(100vh - 64px - 32px - 32px);
}

/* 自定义滚动条 */
.layout-sidebar::-webkit-scrollbar,
.layout-main::-webkit-scrollbar {
  width: 6px;
}

.layout-sidebar::-webkit-scrollbar-track,
.layout-main::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.layout-sidebar::-webkit-scrollbar-thumb,
.layout-main::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 3px;
  transition: background 0.3s ease;
}

.layout-sidebar::-webkit-scrollbar-thumb:hover,
.layout-main::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .layout-sidebar {
    width: 220px;
  }
  
  .main-content {
    padding: 24px;
  }
}

@media (max-width: 768px) {
  .layout-sidebar {
    width: 200px;
  }
  
  .header-content {
    padding: 0 20px;
  }
  
  .main-content {
    padding: 20px;
  }
  
  .system-title {
    font-size: 18px;
  }
  
  .system-title span {
    display: none;
  }
  
  .sidebar-menu {
    padding: 0 12px;
  }
}

@media (max-width: 480px) {
  .layout-sidebar {
    width: 180px;
  }
  
  .header-content {
    padding: 0 16px;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .user-info {
    padding: 8px 12px;
  }
  
  .username {
    display: none;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.layout-main {
  animation: fadeInUp 0.6s ease-out;
}

.sidebar-menu .el-menu-item {
  animation: fadeInUp 0.4s ease-out;
}

.sidebar-menu .el-menu-item:nth-child(1) { animation-delay: 0.1s; }
.sidebar-menu .el-menu-item:nth-child(2) { animation-delay: 0.2s; }
.sidebar-menu .el-menu-item:nth-child(3) { animation-delay: 0.3s; }
.sidebar-menu .el-menu-item:nth-child(4) { animation-delay: 0.4s; }
.sidebar-menu .el-menu-item:nth-child(5) { animation-delay: 0.5s; }
</style>
