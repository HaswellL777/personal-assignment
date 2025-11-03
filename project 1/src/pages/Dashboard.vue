<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon"><Odometer /></el-icon>
        仪表盘
      </h1>
      <p class="page-description">系统概览和关键指标监控</p>
    </div>

    <div class="dashboard-content">
      <!-- 关键指标卡片 -->
      <div class="metrics-grid">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-icon system">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-number">99.9%</div>
              <div class="metric-label">系统可用性</div>
            </div>
          </div>
        </el-card>

        <el-card class="metric-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-icon performance">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-number">1.2s</div>
              <div class="metric-label">平均响应时间</div>
            </div>
          </div>
        </el-card>

        <el-card class="metric-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-icon requests">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-number">15.6K</div>
              <div class="metric-label">今日请求数</div>
            </div>
          </div>
        </el-card>

        <el-card class="metric-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-icon errors">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-number">0.1%</div>
              <div class="metric-label">错误率</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 图表区域 -->
      <div class="charts-section">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-card class="chart-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>系统负载趋势</span>
                  <el-button type="text" size="small">查看详情</el-button>
                </div>
              </template>
              <div class="chart-placeholder">
                <el-icon class="chart-icon"><TrendCharts /></el-icon>
                <p>负载趋势图表</p>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>用户活跃度</span>
                  <el-button type="text" size="small">查看详情</el-button>
                </div>
              </template>
              <div class="chart-placeholder">
                <el-icon class="chart-icon"><DataAnalysis /></el-icon>
                <p>用户活跃度图表</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 系统状态 -->
      <div class="system-status">
        <el-card class="status-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统状态</span>
              <el-tag type="success">正常运行</el-tag>
            </div>
          </template>
          <div class="status-list">
            <div class="status-item">
              <div class="status-label">数据库连接</div>
              <el-tag type="success" size="small">正常</el-tag>
            </div>
            <div class="status-item">
              <div class="status-label">缓存服务</div>
              <el-tag type="success" size="small">正常</el-tag>
            </div>
            <div class="status-item">
              <div class="status-label">消息队列</div>
              <el-tag type="success" size="small">正常</el-tag>
            </div>
            <div class="status-item">
              <div class="status-label">文件存储</div>
              <el-tag type="warning" size="small">警告</el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 管理员操作区域 -->
      <div class="admin-actions">
        <el-card class="admin-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>管理员操作</span>
              <el-tag type="warning" size="small">需要权限验证</el-tag>
            </div>
          </template>
          <div class="admin-content">
            <p class="admin-description">以下操作需要管理员权限，点击按钮测试权限验证功能：</p>
            <div class="admin-buttons">
              <el-button 
                type="danger" 
                :icon="Delete" 
                :loading="deleteUserLoading"
                @click="handleDeleteUser"
              >
                删除用户
              </el-button>
              <el-button 
                type="primary" 
                :icon="Setting" 
                :loading="systemConfigLoading"
                @click="handleGetSystemConfig"
              >
                查看系统配置
              </el-button>
            </div>
            <div v-if="operationResult" class="operation-result">
              <el-alert
                :title="operationResult.title"
                :type="operationResult.type"
                :description="operationResult.message"
                show-icon
                :closable="false"
              />
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
  Odometer,
  Monitor,
  TrendCharts,
  DataAnalysis,
  Warning,
  Delete,
  Setting
} from '@element-plus/icons-vue'

// 响应式数据
const deleteUserLoading = ref(false)
const systemConfigLoading = ref(false)
const operationResult = ref(null)

// 处理删除用户操作
const handleDeleteUser = async () => {
  deleteUserLoading.value = true
  operationResult.value = null
  
  try {
    const token = localStorage.getItem('auth_token') || ''
    const response = await fetch('/api/v1/admin/delete-user', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      body: JSON.stringify({
        userId: 'test-user-123',
        reason: '测试权限验证功能'
      })
    })
    
    const text = await response.text()
    let data
    try { 
      data = JSON.parse(text) 
    } catch { 
      data = text 
    }
    
    if (response.ok) {
      operationResult.value = {
        type: 'success',
        title: '操作成功',
        message: data.message || '用户删除操作执行成功'
      }
      ElMessage.success('删除用户操作成功')
    } else {
      const errorMsg = typeof data === 'object' ? 
        (data.message || data.error || data.detail || '操作失败') : 
        String(data)
      
      operationResult.value = {
        type: response.status === 403 ? 'warning' : 'error',
        title: response.status === 403 ? '权限不足' : '操作失败',
        message: errorMsg
      }
      
      if (response.status === 403) {
        ElMessage.warning('您没有执行此操作的权限')
      } else if (response.status === 401) {
        ElMessage.error('请先登录')
      } else {
        ElMessage.error('操作失败')
      }
    }
  } catch (error) {
    operationResult.value = {
      type: 'error',
      title: '网络错误',
      message: '无法连接到服务器，请检查网络连接'
    }
    ElMessage.error('网络错误')
  } finally {
    deleteUserLoading.value = false
  }
}

// 处理获取系统配置操作
const handleGetSystemConfig = async () => {
  systemConfigLoading.value = true
  operationResult.value = null
  
  try {
    const token = localStorage.getItem('auth_token') || ''
    const response = await fetch('/api/v1/admin/system-config', {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      }
    })
    
    const text = await response.text()
    let data
    try { 
      data = JSON.parse(text) 
    } catch { 
      data = text 
    }
    
    if (response.ok) {
      operationResult.value = {
        type: 'success',
        title: '获取成功',
        message: `系统配置获取成功：${data.data?.systemName || '智能体创作平台'} v${data.data?.version || '1.0.0'}`
      }
      ElMessage.success('系统配置获取成功')
    } else {
      const errorMsg = typeof data === 'object' ? 
        (data.message || data.error || data.detail || '操作失败') : 
        String(data)
      
      operationResult.value = {
        type: response.status === 403 ? 'warning' : 'error',
        title: response.status === 403 ? '权限不足' : '操作失败',
        message: errorMsg
      }
      
      if (response.status === 403) {
        ElMessage.warning('您没有查看系统配置的权限')
      } else if (response.status === 401) {
        ElMessage.error('请先登录')
      } else {
        ElMessage.error('操作失败')
      }
    }
  } catch (error) {
    operationResult.value = {
      type: 'error',
      title: '网络错误',
      message: '无法连接到服务器，请检查网络连接'
    }
    ElMessage.error('网络错误')
  } finally {
    systemConfigLoading.value = false
  }
}
</script>

<style scoped>
.dashboard-page {
  padding: 0;
  background: transparent;
  animation: fadeInUp 0.6s ease-out;
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
  padding: 24px 32px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 252, 0.9) 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 32px;
  color: #667eea;
  filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.3));
}

.page-description {
  margin: 0;
  color: #64748b;
  font-size: 16px;
  font-weight: 400;
}

/* 仪表盘内容 */
.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 指标卡片网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.metric-card {
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 252, 0.9) 100%);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.metric-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.metric-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
}

.metric-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

.metric-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.metric-icon.system {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.metric-icon.performance {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.metric-icon.requests {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.metric-icon.errors {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.metric-info {
  flex: 1;
}

.metric-number {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
  margin-bottom: 4px;
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.metric-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 图表区域 */
.charts-section {
  width: 100%;
}

.chart-card {
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 252, 0.9) 100%);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  height: 320px;
  overflow: hidden;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
}

.chart-card .el-card__header {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  padding: 20px 24px;
}

.chart-placeholder {
  height: 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  margin: 16px;
  border: 2px dashed #e2e8f0;
  transition: all 0.3s ease;
}

.chart-placeholder:hover {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.chart-icon {
  font-size: 56px;
  margin-bottom: 12px;
  color: #667eea;
  opacity: 0.7;
}

.chart-placeholder p {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

/* 系统状态 */
.system-status {
  width: 100%;
}

.status-card {
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 252, 0.9) 100%);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.status-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
}

.status-card .el-card__header {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.05) 0%, rgba(16, 185, 129, 0.05) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  padding: 20px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #1e293b;
}

.status-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 8px 0;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(248, 250, 252, 0.5) 0%, rgba(241, 245, 249, 0.5) 100%);
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.6);
  transition: all 0.3s ease;
}

.status-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateX(4px);
}

.status-label {
  color: #374151;
  font-size: 15px;
  font-weight: 500;
}

/* 管理员操作区域 */
.admin-actions {
  width: 100%;
}

.admin-card {
  border-radius: 16px;
  border: 2px solid rgba(251, 191, 36, 0.3);
  background: linear-gradient(135deg, rgba(254, 243, 199, 0.9) 0%, rgba(253, 230, 138, 0.9) 100%);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(251, 191, 36, 0.15);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.admin-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #f59e0b 0%, #d97706 100%);
}

.admin-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(251, 191, 36, 0.25);
  border-color: rgba(251, 191, 36, 0.5);
}

.admin-card .el-card__header {
  background: linear-gradient(135deg, rgba(251, 191, 36, 0.1) 0%, rgba(245, 158, 11, 0.1) 100%);
  border-bottom: 1px solid rgba(251, 191, 36, 0.3);
  padding: 20px 24px;
}

.admin-content {
  padding: 8px 0;
}

.admin-description {
  margin: 0 0 20px 0;
  color: #92400e;
  font-size: 15px;
  line-height: 1.6;
  font-weight: 500;
}

.admin-buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.admin-buttons .el-button {
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.admin-buttons .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.operation-result {
  margin-top: 20px;
}

.operation-result .el-alert {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.metric-card {
  animation: slideInLeft 0.6s ease-out;
}

.metric-card:nth-child(1) { animation-delay: 0.1s; }
.metric-card:nth-child(2) { animation-delay: 0.2s; }
.metric-card:nth-child(3) { animation-delay: 0.3s; }
.metric-card:nth-child(4) { animation-delay: 0.4s; }

.chart-card {
  animation: fadeInUp 0.6s ease-out;
  animation-delay: 0.3s;
  animation-fill-mode: both;
}

.status-card {
  animation: fadeInUp 0.6s ease-out;
  animation-delay: 0.4s;
  animation-fill-mode: both;
}

.admin-card {
  animation: fadeInUp 0.6s ease-out;
  animation-delay: 0.5s;
  animation-fill-mode: both;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px 24px;
    margin-bottom: 24px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .dashboard-content {
    gap: 24px;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .metric-content {
    padding: 20px;
    gap: 16px;
  }
  
  .metric-icon {
    width: 56px;
    height: 56px;
    font-size: 24px;
  }
  
  .metric-number {
    font-size: 28px;
  }
  
  .charts-section .el-col {
    margin-bottom: 20px;
  }
  
  .chart-card {
    height: 280px;
  }
  
  .chart-placeholder {
    height: 180px;
    margin: 12px;
  }
  
  .admin-buttons {
    flex-direction: column;
  }
  
  .admin-buttons .el-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .page-header {
    padding: 16px 20px;
  }
  
  .page-title {
    font-size: 20px;
    gap: 8px;
  }
  
  .title-icon {
    font-size: 24px;
  }
  
  .metric-content {
    padding: 16px;
    gap: 12px;
  }
  
  .metric-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  
  .metric-number {
    font-size: 24px;
  }
  
  .chart-card {
    height: 240px;
  }
  
  .status-item {
    padding: 12px 16px;
  }
}
</style>