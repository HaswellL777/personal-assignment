<template>
  <div class="home-page">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon"><House /></el-icon>
        首页
      </h1>
      <p class="page-description">欢迎使用DevOps管理系统</p>
    </div>

    <div class="dashboard-content">
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon users">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">128</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon roles">
              <el-icon><Setting /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">12</div>
              <div class="stat-label">角色数量</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon apps">
              <el-icon><Grid /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">8</div>
              <div class="stat-label">应用数量</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon online">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">45</div>
              <div class="stat-label">在线用户</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <el-card class="action-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="action-buttons">
            <el-button type="primary" :icon="Plus" @click="handleQuickAction('add-user')">
              添加用户
            </el-button>
            <el-button type="success" :icon="Setting" @click="handleQuickAction('manage-roles')">
              管理角色
            </el-button>
            <el-button type="info" :icon="View" @click="handleQuickAction('view-logs')">
              查看日志
            </el-button>
            <el-button type="warning" :icon="Tools" @click="handleQuickAction('system-config')">
              系统配置
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 最近活动 -->
      <div class="recent-activity">
        <el-card class="activity-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
              <el-button type="text" size="small">查看全部</el-button>
            </div>
          </template>
          <div class="activity-list">
            <div class="activity-item" v-for="item in recentActivities" :key="item.id">
              <div class="activity-avatar">
                <el-avatar :size="32" :src="item.avatar" />
              </div>
              <div class="activity-content">
                <div class="activity-text">{{ item.text }}</div>
                <div class="activity-time">{{ item.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  House,
  UserFilled,
  Setting,
  Grid,
  Connection,
  Plus,
  View,
  Tools
} from '@element-plus/icons-vue'

// 最近活动数据
const recentActivities = ref([
  {
    id: 1,
    text: '管理员 admin 创建了新用户 "张三"',
    time: '2分钟前',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin'
  },
  {
    id: 2,
    text: '用户 "李四" 登录了系统',
    time: '5分钟前',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=lisi'
  },
  {
    id: 3,
    text: '管理员 admin 修改了角色权限',
    time: '10分钟前',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin'
  },
  {
    id: 4,
    text: '用户 "王五" 更新了个人信息',
    time: '15分钟前',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=wangwu'
  }
])

// 处理快捷操作
const handleQuickAction = (action) => {
  switch (action) {
    case 'add-user':
      ElMessage.info('跳转到添加用户页面')
      break
    case 'manage-roles':
      ElMessage.info('跳转到角色管理页面')
      break
    case 'view-logs':
      ElMessage.info('跳转到日志查看页面')
      break
    case 'system-config':
      ElMessage.info('跳转到系统配置页面')
      break
    default:
      ElMessage.warning('功能开发中...')
  }
}
</script>

<style scoped>
.home-page {
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

.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.roles {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.apps {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.online {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

/* 快捷操作和最近活动 */
.quick-actions,
.recent-activity {
  width: 100%;
}

.action-card,
.activity-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.activity-content {
  flex: 1;
}

.activity-text {
  color: #374151;
  font-size: 14px;
  line-height: 1.5;
}

.activity-time {
  color: #9ca3af;
  font-size: 12px;
  margin-top: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style>