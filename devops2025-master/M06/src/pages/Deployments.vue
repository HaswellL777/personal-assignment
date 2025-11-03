<template>
  <div class="deployments-page">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon"><Upload /></el-icon>
        部署管理
      </h1>
      <p class="page-description">管理应用的部署流程和版本发布</p>
    </div>

    <div class="deployments-content">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="filter-section">
          <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 150px" clearable>
            <el-option label="全部状态" value="" />
            <el-option label="部署中" value="deploying" />
            <el-option label="成功" value="success" />
            <el-option label="失败" value="failed" />
            <el-option label="回滚中" value="rolling_back" />
          </el-select>
          <el-select v-model="envFilter" placeholder="筛选环境" style="width: 150px" clearable>
            <el-option label="全部环境" value="" />
            <el-option label="开发环境" value="dev" />
            <el-option label="测试环境" value="test" />
            <el-option label="生产环境" value="prod" />
          </el-select>
        </div>
        <div class="action-buttons">
          <el-button type="primary" :icon="Plus" @click="handleNewDeployment">
            新建部署
          </el-button>
          <el-button :icon="Refresh" @click="handleRefresh">
            刷新
          </el-button>
        </div>
      </div>

      <!-- 部署统计 -->
      <div class="stats-row">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon deploying">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ deployingCount }}</div>
              <div class="stat-label">部署中</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ successCount }}</div>
              <div class="stat-label">成功部署</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon failed">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ failedCount }}</div>
              <div class="stat-label">失败部署</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Upload /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalCount }}</div>
              <div class="stat-label">总部署数</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 部署列表 -->
      <el-card class="deployments-table-card">
        <el-table :data="filteredDeployments" style="width: 100%">
          <el-table-column prop="id" label="部署ID" width="100" />
          <el-table-column prop="appName" label="应用名称" width="150">
            <template #default="{ row }">
              <div class="app-name">
                <el-icon class="app-icon"><Grid /></el-icon>
                {{ row.appName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="version" label="版本" width="120" />
          <el-table-column prop="environment" label="环境" width="100">
            <template #default="{ row }">
              <el-tag :type="getEnvType(row.environment)">
                {{ getEnvText(row.environment) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                <el-icon v-if="row.status === 'deploying'" class="loading-icon">
                  <Loading />
                </el-icon>
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="进度" width="150">
            <template #default="{ row }">
              <el-progress
                v-if="row.status === 'deploying' || row.status === 'rolling_back'"
                :percentage="row.progress"
                :status="row.status === 'rolling_back' ? 'warning' : 'success'"
                :stroke-width="8"
              />
              <span v-else-if="row.status === 'success'" class="progress-text success">
                部署完成
              </span>
              <span v-else-if="row.status === 'failed'" class="progress-text failed">
                部署失败
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="deployer" label="部署人" width="120" />
          <el-table-column prop="startTime" label="开始时间" width="150" />
          <el-table-column prop="duration" label="耗时" width="100" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                :icon="View"
                @click="handleViewLogs(row)"
              >
                日志
              </el-button>
              <el-button
                v-if="row.status === 'success'"
                type="warning"
                size="small"
                :icon="RefreshLeft"
                @click="handleRollback(row)"
              >
                回滚
              </el-button>
              <el-button
                v-if="row.status === 'deploying'"
                type="danger"
                size="small"
                :icon="Close"
                @click="handleCancel(row)"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 空状态 -->
      <div v-if="filteredDeployments.length === 0" class="empty-state">
        <el-empty description="暂无部署记录">
          <el-button type="primary" @click="handleNewDeployment">创建第一个部署</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Upload,
  Plus,
  Refresh,
  View,
  RefreshLeft,
  Close,
  Loading,
  CircleCheck,
  CircleClose,
  Grid
} from '@element-plus/icons-vue'

const statusFilter = ref('')
const envFilter = ref('')

// 模拟部署数据
const deployments = ref([
  {
    id: 'D001',
    appName: 'Web前端',
    version: 'v1.2.3',
    environment: 'prod',
    status: 'success',
    progress: 100,
    deployer: '张三',
    startTime: '2024-01-15 14:30',
    duration: '3m 45s'
  },
  {
    id: 'D002',
    appName: 'API服务',
    version: 'v2.1.0',
    environment: 'test',
    status: 'deploying',
    progress: 65,
    deployer: '李四',
    startTime: '2024-01-15 14:45',
    duration: '2m 15s'
  },
  {
    id: 'D003',
    appName: '数据处理',
    version: 'v1.5.2',
    environment: 'dev',
    status: 'failed',
    progress: 0,
    deployer: '王五',
    startTime: '2024-01-15 13:20',
    duration: '1m 30s'
  },
  {
    id: 'D004',
    appName: '缓存服务',
    version: 'v3.0.1',
    environment: 'prod',
    status: 'rolling_back',
    progress: 40,
    deployer: '赵六',
    startTime: '2024-01-15 12:10',
    duration: '5m 20s'
  },
  {
    id: 'D005',
    appName: '监控系统',
    version: 'v2.3.0',
    environment: 'test',
    status: 'success',
    progress: 100,
    deployer: '钱七',
    startTime: '2024-01-15 11:30',
    duration: '4m 10s'
  }
])

// 过滤部署记录
const filteredDeployments = computed(() => {
  let filtered = deployments.value
  
  if (statusFilter.value) {
    filtered = filtered.filter(d => d.status === statusFilter.value)
  }
  
  if (envFilter.value) {
    filtered = filtered.filter(d => d.environment === envFilter.value)
  }
  
  return filtered
})

// 统计数据
const deployingCount = computed(() => 
  deployments.value.filter(d => d.status === 'deploying' || d.status === 'rolling_back').length
)
const successCount = computed(() => 
  deployments.value.filter(d => d.status === 'success').length
)
const failedCount = computed(() => 
  deployments.value.filter(d => d.status === 'failed').length
)
const totalCount = computed(() => deployments.value.length)

// 获取环境类型
const getEnvType = (env) => {
  const typeMap = {
    dev: 'info',
    test: 'warning',
    prod: 'danger'
  }
  return typeMap[env] || 'info'
}

// 获取环境文本
const getEnvText = (env) => {
  const textMap = {
    dev: '开发',
    test: '测试',
    prod: '生产'
  }
  return textMap[env] || env
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    deploying: 'primary',
    success: 'success',
    failed: 'danger',
    rolling_back: 'warning'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    deploying: '部署中',
    success: '成功',
    failed: '失败',
    rolling_back: '回滚中'
  }
  return textMap[status] || status
}

// 处理新建部署
const handleNewDeployment = () => {
  ElMessage.info('新建部署功能开发中...')
}

// 处理刷新
const handleRefresh = () => {
  ElMessage.success('部署列表已刷新')
}

// 处理查看日志
const handleViewLogs = (deployment) => {
  ElMessage.info(`查看部署 ${deployment.id} 的日志...`)
}

// 处理回滚
const handleRollback = async (deployment) => {
  try {
    await ElMessageBox.confirm(
      `确定要回滚应用 ${deployment.appName} 的版本 ${deployment.version} 吗？`,
      '确认回滚',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    deployment.status = 'rolling_back'
    deployment.progress = 0
    ElMessage.success('开始回滚部署...')
    
    // 模拟回滚进度
    const timer = setInterval(() => {
      deployment.progress += 10
      if (deployment.progress >= 100) {
        deployment.status = 'success'
        deployment.progress = 100
        clearInterval(timer)
        ElMessage.success('回滚完成')
      }
    }, 500)
  } catch {
    ElMessage.info('已取消回滚')
  }
}

// 处理取消部署
const handleCancel = async (deployment) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消部署 ${deployment.id} 吗？`,
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    deployment.status = 'failed'
    deployment.progress = 0
    ElMessage.warning('部署已取消')
  } catch {
    ElMessage.info('继续部署')
  }
}
</script>

<style scoped>
.deployments-page {
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

.deployments-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.filter-section {
  display: flex;
  gap: 12px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
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

.stat-icon.deploying {
  background: linear-gradient(135deg, #409eff, #66b1ff);
}

.stat-icon.success {
  background: linear-gradient(135deg, #67c23a, #85ce61);
}

.stat-icon.failed {
  background: linear-gradient(135deg, #f56c6c, #f78989);
}

.stat-icon.total {
  background: linear-gradient(135deg, #909399, #b1b3b8);
}

.stat-info {
  flex: 1;
}

.stat-value {
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

/* 部署表格 */
.deployments-table-card {
  border-radius: 8px;
}

.app-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.app-icon {
  color: #409eff;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  margin-right: 4px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.progress-text {
  font-size: 14px;
  font-weight: 500;
}

.progress-text.success {
  color: #67c23a;
}

.progress-text.failed {
  color: #f56c6c;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .filter-section {
    width: 100%;
    justify-content: space-between;
  }
  
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 12px;
  }
}
</style>