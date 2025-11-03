<template>
  <div class="user-profile">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon"><User /></el-icon>
        个人信息
      </h1>
      <p class="page-description">查看和管理您的个人信息</p>
    </div>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <div v-else-if="error" class="error">
      <el-alert :title="error" type="error" show-icon />
    </div>

    <div v-else class="profile-content">
      <el-card class="profile-card">
        <div class="profile-avatar">
          <el-avatar :size="100" :src="userInfo.avatar" />
        </div>
        
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="用户名">
            {{ userInfo.username }}
          </el-descriptions-item>
          <el-descriptions-item label="昵称">
            {{ userInfo.nickname }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ userInfo.email }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ userInfo.phone }}
          </el-descriptions-item>
          <el-descriptions-item label="部门">
            {{ userInfo.department }}
          </el-descriptions-item>
          <el-descriptions-item label="职位">
            {{ userInfo.position }}
          </el-descriptions-item>
          <el-descriptions-item label="入职日期">
            {{ userInfo.joinDate }}
          </el-descriptions-item>
          <el-descriptions-item label="最后登录">
            {{ userInfo.lastLogin }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="userInfo.status === 'active' ? 'success' : 'danger'">
              {{ userInfo.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading, User } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const error = ref('')
const userInfo = ref({})

const fetchUserProfile = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const token = localStorage.getItem('auth_token')
    if (!token) {
      router.push('/')
      return
    }

    const res = await fetch('/api/v1/user/profile', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })

    if (res.status === 401) {
      localStorage.removeItem('auth_token')
      router.push('/')
      return
    }

    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`)
    }

    const data = await res.json()
    if (data.code === 0) {
      userInfo.value = data.data
    } else {
      error.value = data.message || '获取用户信息失败'
    }
  } catch (err) {
    console.error('获取用户信息失败:', err)
    error.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}



onMounted(() => {
  fetchUserProfile()
})
</script>

<style scoped>
.user-profile {
  width: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.title-icon {
  margin-right: 8px;
  color: #409eff;
}

.page-description {
  color: #6b7280;
  margin: 0;
  font-size: 14px;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #909399;
}

.loading .el-icon {
  margin-right: 8px;
}

.error {
  margin: 20px 0;
}

.profile-content {
  margin-top: 20px;
}

.profile-card {
  margin-bottom: 20px;
}

.profile-avatar {
  text-align: center;
  margin-bottom: 20px;
}

.el-descriptions {
  margin-top: 20px;
}
</style>