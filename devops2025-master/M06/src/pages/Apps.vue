<template>
  <div class="apps-page">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon"><Grid /></el-icon>
        应用管理
      </h1>
      <p class="page-description">管理和监控系统中的所有应用</p>
    </div>

    <div class="apps-content">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-section">
          <el-input
            v-model="searchText"
            placeholder="搜索应用名称..."
            :prefix-icon="Search"
            style="width: 300px"
            clearable
          />
        </div>
        <div class="action-buttons">
          <el-button type="primary" :icon="Plus" @click="handleAddApp">
            添加应用
          </el-button>
          <el-button :icon="Refresh" @click="handleRefresh">
            刷新
          </el-button>
        </div>
      </div>

      <!-- 应用列表 -->
      <div class="apps-grid">
        <el-card
          v-for="app in filteredApps"
          :key="app.id"
          class="app-card"
          shadow="hover"
        >
          <div class="app-header">
            <div class="app-icon">
              <el-icon><component :is="app.icon" /></el-icon>
            </div>
            <div class="app-info">
              <h3 class="app-name">{{ app.name }}</h3>
              <p class="app-description">{{ app.description }}</p>
            </div>
            <div class="app-status">
              <el-tag :type="app.status === 'running' ? 'success' : app.status === 'stopped' ? 'danger' : 'warning'">
                {{ getStatusText(app.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="app-details">
            <div class="detail-item">
              <span class="detail-label">版本:</span>
              <span class="detail-value">{{ app.version }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">端口:</span>
              <span class="detail-value">{{ app.port }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">最后更新:</span>
              <span class="detail-value">{{ app.lastUpdate }}</span>
            </div>
          </div>

          <div class="app-actions">
            <el-button
              v-if="app.status === 'running'"
              type="warning"
              size="small"
              :icon="VideoPause"
              @click="handleStopApp(app)"
            >
              停止
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              :icon="VideoPlay"
              @click="handleStartApp(app)"
            >
              启动
            </el-button>
            <el-button
              type="primary"
              size="small"
              :icon="Setting"
              @click="handleConfigApp(app)"
            >
              配置
            </el-button>
            <el-button
              type="info"
              size="small"
              :icon="View"
              @click="handleViewLogs(app)"
            >
              日志
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredApps.length === 0" class="empty-state">
        <el-empty description="暂无应用数据">
          <el-button type="primary" @click="handleAddApp">添加第一个应用</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Grid,
  Search,
  Plus,
  Refresh,
  Setting,
  View,
  VideoPlay,
  VideoPause,
  Monitor,
  DataBoard,
  Connection
} from '@element-plus/icons-vue'

const searchText = ref('')

// 模拟应用数据
const apps = ref([
  {
    id: 1,
    name: 'Web前端',
    description: 'Vue.js前端应用',
    version: 'v1.2.3',
    port: '5173',
    status: 'running',
    icon: 'Monitor',
    lastUpdate: '2024-01-15 14:30'
  },
  {
    id: 2,
    name: 'API服务',
    description: 'FastAPI后端服务',
    version: 'v2.1.0',
    port: '8000',
    status: 'running',
    icon: 'DataBoard',
    lastUpdate: '2024-01-15 10:20'
  },
  {
    id: 3,
    name: '数据库',
    description: 'PostgreSQL数据库',
    version: 'v13.8',
    port: '5432',
    status: 'running',
    icon: 'Connection',
    lastUpdate: '2024-01-14 16:45'
  },
  {
    id: 4,
    name: '缓存服务',
    description: 'Redis缓存服务',
    version: 'v7.0.5',
    port: '6379',
    status: 'stopped',
    icon: 'DataBoard',
    lastUpdate: '2024-01-13 09:15'
  }
])

// 过滤应用
const filteredApps = computed(() => {
  if (!searchText.value) return apps.value
  return apps.value.filter(app => 
    app.name.toLowerCase().includes(searchText.value.toLowerCase()) ||
    app.description.toLowerCase().includes(searchText.value.toLowerCase())
  )
})

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    running: '运行中',
    stopped: '已停止',
    error: '错误'
  }
  return statusMap[status] || '未知'
}

// 处理添加应用
const handleAddApp = () => {
  ElMessage.info('添加应用功能开发中...')
}

// 处理刷新
const handleRefresh = () => {
  ElMessage.success('应用列表已刷新')
}

// 处理启动应用
const handleStartApp = (app) => {
  app.status = 'running'
  ElMessage.success(`应用 ${app.name} 启动成功`)
}

// 处理停止应用
const handleStopApp = (app) => {
  app.status = 'stopped'
  ElMessage.warning(`应用 ${app.name} 已停止`)
}

// 处理配置应用
const handleConfigApp = (app) => {
  ElMessage.info(`配置应用 ${app.name}...`)
}

// 处理查看日志
const handleViewLogs = (app) => {
  ElMessage.info(`查看应用 ${app.name} 的日志...`)
}
</script>

<style scoped>
/* 应用管理页面整体样式 */
.apps-page {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 60px);
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
  text-align: center;
  padding: 24px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 12px 0;
  animation: slideInDown 0.8s ease-out;
}

.title-icon {
  margin-right: 12px;
  font-size: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-description {
  color: #718096;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  animation: slideInUp 0.8s ease-out 0.2s both;
}

@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.apps-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 操作栏样式 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: fadeInScale 0.6s ease-out 0.4s both;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.search-section :deep(.el-input) {
  border-radius: 12px;
}

.search-section :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.search-section :deep(.el-input__wrapper:hover) {
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

.search-section :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
  border-color: #667eea;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-buttons :deep(.el-button) {
  border-radius: 12px;
  padding: 12px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-buttons :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.action-buttons :deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

/* 应用网格样式 */
.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 24px;
  animation: fadeInGrid 0.8s ease-out 0.6s both;
}

@keyframes fadeInGrid {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 应用卡片样式 */
.app-card {
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;
}

.app-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.app-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.app-card :deep(.el-card__body) {
  padding: 24px;
}

/* 应用头部样式 */
.app-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
}

.app-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  flex-shrink: 0;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.app-card:hover .app-icon {
  transform: rotate(5deg) scale(1.1);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.4);
}

.app-info {
  flex: 1;
  min-width: 0;
}

.app-name {
  margin: 0 0 6px 0;
  font-size: 18px;
  font-weight: 700;
  color: #2d3748;
  line-height: 1.3;
}

.app-description {
  margin: 0;
  font-size: 14px;
  color: #718096;
  line-height: 1.5;
}

.app-status {
  flex-shrink: 0;
}

.app-status :deep(.el-tag) {
  border-radius: 20px;
  padding: 6px 12px;
  font-weight: 600;
  font-size: 12px;
  border: none;
}

.app-status :deep(.el-tag--success) {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  color: white;
}

.app-status :deep(.el-tag--danger) {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
  color: white;
}

.app-status :deep(.el-tag--warning) {
  background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
  color: white;
}

/* 应用详情样式 */
.app-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.detail-label {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  color: #2d3748;
  font-weight: 600;
  background: rgba(102, 126, 234, 0.1);
  padding: 4px 8px;
  border-radius: 6px;
}

/* 应用操作样式 */
.app-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.app-actions :deep(.el-button) {
  flex: 1;
  min-width: 70px;
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.app-actions :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.app-actions :deep(.el-button--success) {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  color: white;
}

.app-actions :deep(.el-button--warning) {
  background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
  color: white;
}

.app-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.app-actions :deep(.el-button--info) {
  background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
  color: white;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.empty-state :deep(.el-empty__description) {
  color: #718096;
  font-size: 16px;
  margin-bottom: 24px;
}

.empty-state :deep(.el-button) {
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.empty-state :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .apps-page {
    padding: 16px;
  }
  
  .page-header {
    padding: 20px 16px;
    margin-bottom: 24px;
  }
  
  .page-title {
    font-size: 28px;
  }
  
  .title-icon {
    font-size: 32px;
  }
  
  .action-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    padding: 20px 16px;
  }
  
  .search-section {
    width: 100%;
  }
  
  .search-section :deep(.el-input) {
    width: 100% !important;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .apps-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .app-card :deep(.el-card__body) {
    padding: 20px;
  }
  
  .app-icon {
    width: 48px;
    height: 48px;
    font-size: 24px;
  }
  
  .app-name {
    font-size: 16px;
  }
  
  .app-actions :deep(.el-button) {
    min-width: 60px;
    padding: 8px 12px;
    font-size: 12px;
  }
  
  .empty-state {
    padding: 60px 16px;
  }
}

/* 暗色模式支持 */
@media (prefers-color-scheme: dark) {
  .apps-page {
    background: linear-gradient(135deg, #1a202c 0%, #2d3748 100%);
  }
  
  .page-header,
  .action-bar,
  .app-card,
  .empty-state {
    background: rgba(26, 32, 44, 0.95);
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .page-description {
    color: #a0aec0;
  }
  
  .app-name {
    color: #f7fafc;
  }
  
  .app-description {
    color: #a0aec0;
  }
  
  .app-details {
    background: linear-gradient(135deg, #2d3748 0%, #4a5568 100%);
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .detail-label {
    color: #a0aec0;
  }
  
  .detail-value {
    color: #f7fafc;
    background: rgba(102, 126, 234, 0.2);
  }
}

/* 加载动画 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.loading {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
</style>