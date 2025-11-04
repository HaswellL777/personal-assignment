<template>
  <div class="servers-page">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon"><Monitor /></el-icon>
        服务器管理
      </h1>
      <p class="page-description">监控和管理系统中的所有服务器</p>
    </div>

    <div class="servers-content">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-section">
          <el-input
            v-model="searchText"
            placeholder="搜索服务器名称或IP..."
            :prefix-icon="Search"
            style="width: 300px"
            clearable
          />
        </div>
        <div class="action-buttons">
          <el-button type="primary" :icon="Plus" @click="handleAddServer">
            添加服务器
          </el-button>
          <el-button :icon="Refresh" @click="handleRefresh">
            刷新状态
          </el-button>
        </div>
      </div>

      <!-- 服务器统计 -->
      <div class="stats-row">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon online">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ onlineServers }}</div>
              <div class="stat-label">在线服务器</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon offline">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ offlineServers }}</div>
              <div class="stat-label">离线服务器</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ warningServers }}</div>
              <div class="stat-label">告警服务器</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalServers }}</div>
              <div class="stat-label">总服务器数</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 服务器列表 -->
      <el-card class="servers-table-card">
        <el-table :data="filteredServers" style="width: 100%">
          <el-table-column prop="name" label="服务器名称" width="150">
            <template #default="{ row }">
              <div class="server-name">
                <el-icon class="server-icon"><Monitor /></el-icon>
                {{ row.name }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="ip" label="IP地址" width="130" />
          <el-table-column prop="os" label="操作系统" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="CPU使用率" width="120">
            <template #default="{ row }">
              <el-progress
                :percentage="row.cpuUsage"
                :color="getProgressColor(row.cpuUsage)"
                :stroke-width="8"
              />
            </template>
          </el-table-column>
          <el-table-column label="内存使用率" width="120">
            <template #default="{ row }">
              <el-progress
                :percentage="row.memoryUsage"
                :color="getProgressColor(row.memoryUsage)"
                :stroke-width="8"
              />
            </template>
          </el-table-column>
          <el-table-column label="磁盘使用率" width="120">
            <template #default="{ row }">
              <el-progress
                :percentage="row.diskUsage"
                :color="getProgressColor(row.diskUsage)"
                :stroke-width="8"
              />
            </template>
          </el-table-column>
          <el-table-column prop="lastCheck" label="最后检查" width="150" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                :icon="View"
                @click="handleViewDetails(row)"
              >
                详情
              </el-button>
              <el-button
                type="warning"
                size="small"
                :icon="Setting"
                @click="handleConfig(row)"
              >
                配置
              </el-button>
              <el-button
                type="success"
                size="small"
                :icon="Connection"
                @click="handleConnect(row)"
              >
                连接
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 空状态 -->
      <div v-if="filteredServers.length === 0" class="empty-state">
        <el-empty description="暂无服务器数据">
          <el-button type="primary" @click="handleAddServer">添加第一台服务器</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Monitor,
  Search,
  Plus,
  Refresh,
  View,
  Setting,
  Connection,
  CircleCheck,
  CircleClose,
  Warning
} from '@element-plus/icons-vue'

const searchText = ref('')

// 模拟服务器数据
const servers = ref([
  {
    id: 1,
    name: 'Web-Server-01',
    ip: '192.168.1.10',
    os: 'Ubuntu 20.04',
    status: 'online',
    cpuUsage: 45,
    memoryUsage: 68,
    diskUsage: 32,
    lastCheck: '2024-01-15 14:30'
  },
  {
    id: 2,
    name: 'DB-Server-01',
    ip: '192.168.1.20',
    os: 'CentOS 8',
    status: 'online',
    cpuUsage: 78,
    memoryUsage: 85,
    diskUsage: 56,
    lastCheck: '2024-01-15 14:29'
  },
  {
    id: 3,
    name: 'API-Server-01',
    ip: '192.168.1.30',
    os: 'Ubuntu 22.04',
    status: 'warning',
    cpuUsage: 92,
    memoryUsage: 76,
    diskUsage: 89,
    lastCheck: '2024-01-15 14:28'
  },
  {
    id: 4,
    name: 'Cache-Server-01',
    ip: '192.168.1.40',
    os: 'Redis OS',
    status: 'offline',
    cpuUsage: 0,
    memoryUsage: 0,
    diskUsage: 0,
    lastCheck: '2024-01-15 12:15'
  },
  {
    id: 5,
    name: 'Backup-Server-01',
    ip: '192.168.1.50',
    os: 'Ubuntu 20.04',
    status: 'online',
    cpuUsage: 25,
    memoryUsage: 42,
    diskUsage: 78,
    lastCheck: '2024-01-15 14:31'
  }
])

// 过滤服务器
const filteredServers = computed(() => {
  if (!searchText.value) return servers.value
  return servers.value.filter(server => 
    server.name.toLowerCase().includes(searchText.value.toLowerCase()) ||
    server.ip.includes(searchText.value)
  )
})

// 统计数据
const onlineServers = computed(() => 
  servers.value.filter(s => s.status === 'online').length
)
const offlineServers = computed(() => 
  servers.value.filter(s => s.status === 'offline').length
)
const warningServers = computed(() => 
  servers.value.filter(s => s.status === 'warning').length
)
const totalServers = computed(() => servers.value.length)

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    online: 'success',
    offline: 'danger',
    warning: 'warning'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    online: '在线',
    offline: '离线',
    warning: '告警'
  }
  return textMap[status] || '未知'
}

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage >= 90) return '#f56c6c'
  if (percentage >= 70) return '#e6a23c'
  return '#67c23a'
}

// 处理添加服务器
const handleAddServer = () => {
  ElMessage.info('添加服务器功能开发中...')
}

// 处理刷新
const handleRefresh = () => {
  ElMessage.success('服务器状态已刷新')
}

// 处理查看详情
const handleViewDetails = (server) => {
  ElMessage.info(`查看服务器 ${server.name} 的详细信息...`)
}

// 处理配置
const handleConfig = (server) => {
  ElMessage.info(`配置服务器 ${server.name}...`)
}

// 处理连接
const handleConnect = (server) => {
  if (server.status === 'offline') {
    ElMessage.error(`服务器 ${server.name} 离线，无法连接`)
    return
  }
  ElMessage.success(`正在连接到服务器 ${server.name}...`)
}
</script>

<style scoped>
.servers-page {
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

.servers-content {
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

.stat-icon.online {
  background: linear-gradient(135deg, #67c23a, #85ce61);
}

.stat-icon.offline {
  background: linear-gradient(135deg, #f56c6c, #f78989);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
}

.stat-icon.total {
  background: linear-gradient(135deg, #409eff, #66b1ff);
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

/* 服务器表格 */
.servers-table-card {
  border-radius: 8px;
}

.server-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.server-icon {
  color: #409eff;
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
  
  .search-section {
    width: 100%;
  }
  
  .search-section .el-input {
    width: 100% !important;
  }
  
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>