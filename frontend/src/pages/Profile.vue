<template>
  <div class="profile-page">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon"><User /></el-icon>
        个人信息
      </h1>
      <p class="page-description">查看和管理您的个人信息</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <!-- 错误信息 -->
    <div v-else-if="error" class="error">
      <el-alert :title="error" type="error" show-icon />
    </div>

    <!-- 个人信息卡片 -->
    <div v-else class="profile-content">
      <el-card class="profile-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
          </div>
        </template>

        <div class="profile-avatar">
          <el-avatar :size="100" :src="userInfo.avatar" />
        </div>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">
            {{ userInfo.username || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ userInfo.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="部门">
            {{ userInfo.department || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="职位">
            {{ userInfo.position || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="角色" :span="2">
            <el-tag :type="userInfo.role === 'admin' ? 'danger' : 'primary'">
              {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 管理员操作区域 -->
      <el-card class="action-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>权限操作</span>
            <el-tag type="info" size="small">用于测试权限验证</el-tag>
          </div>
        </template>

        <div class="action-content">
          <p class="action-description">
            点击下方按钮测试权限验证功能。如果您是普通用户，将收到"权限不足"的提示。
          </p>
          <el-button
            type="danger"
            :icon="Delete"
            :loading="adminActionLoading"
            @click="handleAdminAction"
          >
            管理员操作（删除用户）
          </el-button>

          <!-- 权限提示消息 -->
          <div v-if="permissionMessage" class="permission-message">
            <el-alert
              :title="permissionMessage"
              :type="permissionMessageType"
              show-icon
              closable
              @close="permissionMessage = ''"
            />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Loading, Delete } from '@element-plus/icons-vue'
import request from '../utils/request.js'
const loading = ref(false)
const error = ref('')
const userInfo = ref({})
const adminActionLoading = ref(false)
const permissionMessage = ref('')
const permissionMessageType = ref('success')

// 获取个人信息
const fetchUserProfile = async () => {
  loading.value = true
  error.value = ''

  try {
    const data = await request('/api/v1/users/profile', {
      method: 'GET'
    })

    if (data && data.code === 0) {
      userInfo.value = data.data
      // 生成头像
      if (userInfo.value.username && !userInfo.value.avatar) {
        userInfo.value.avatar = `https://api.dicebear.com/7.x/avataaars/svg?seed=${userInfo.value.username}`
      }
    } else {
      error.value = data?.message || '获取用户信息失败'
    }
  } catch (err) {
    console.error('获取用户信息失败:', err)
    error.value = '网络错误或服务不可用'
  } finally {
    loading.value = false
  }
}

// 处理管理员操作（权限验证）
const handleAdminAction = async () => {
  adminActionLoading.value = true
  permissionMessage.value = ''

  try {
    const data = await request('/api/v1/admin/delete-user', {
      method: 'POST',
      body: JSON.stringify({
        userId: 'test-user-id'
      })
    })

    if (data && data.code === 0) {
      permissionMessage.value = '操作成功！您拥有管理员权限'
      permissionMessageType.value = 'success'
      ElMessage.success('管理员操作执行成功')
    } else if (data && data.code === 403) {
      // 权限不足
      permissionMessage.value = '权限不足：您没有执行此操作的权限'
      permissionMessageType.value = 'error'
      ElMessage.error('权限不足')
    } else {
      permissionMessage.value = data?.message || '操作失败'
      permissionMessageType.value = 'error'
    }
  } catch (err) {
    console.error('管理员操作失败:', err)
    permissionMessage.value = '网络错误或服务不可用'
    permissionMessageType.value = 'error'
  } finally {
    adminActionLoading.value = false
  }
}

onMounted(() => {
  fetchUserProfile()
})
</script>

<style scoped>
.profile-page {
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
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #64748b;
}

.loading .el-icon {
  margin-bottom: 12px;
  font-size: 32px;
  color: #667eea;
}

.error {
  margin: 20px 0;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-card,
.action-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.profile-avatar {
  text-align: center;
  margin-bottom: 24px;
}

.el-descriptions {
  margin-top: 0;
}

.action-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.action-description {
  color: #6b7280;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border-left: 3px solid #667eea;
}

.permission-message {
  margin-top: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .el-descriptions {
    font-size: 12px;
  }
}
</style>
