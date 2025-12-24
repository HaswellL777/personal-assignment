<template>
  <div class="ai-assistant">
    <div class="chat-container">
      <div class="chat-header">
        <el-icon><ChatDotRound /></el-icon>
        <span>AI 助手</span>
      </div>
      
      <div class="chat-messages" ref="messagesRef">
        <div v-for="(msg, idx) in messages" :key="idx" 
             :class="['message', msg.role]">
          <div class="message-content">
            <div class="avatar">
              <el-icon v-if="msg.role === 'user'"><User /></el-icon>
              <el-icon v-else><Monitor /></el-icon>
            </div>
            <div class="text">{{ msg.content }}</div>
          </div>
        </div>
        <div v-if="loading" class="message assistant">
          <div class="message-content">
            <div class="avatar"><el-icon><Monitor /></el-icon></div>
            <div class="text typing">思考中...</div>
          </div>
        </div>
      </div>
      
      <div class="chat-input">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="2"
          placeholder="输入消息..."
          @keydown.enter.ctrl="sendMessage"
        />
        <el-button type="primary" @click="sendMessage" :loading="loading">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ChatDotRound, User, Monitor } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request.js'

const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const messagesRef = ref(null)

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!inputText.value.trim() || loading.value) return

  const userMsg = inputText.value.trim()
  messages.value.push({ role: 'user', content: userMsg })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const data = await request('/api/v1/llm/chat', {
      method: 'POST',
      body: JSON.stringify({
        messages: messages.value.map(m => ({ role: m.role, content: m.content }))
      })
    })

    if (data?.code === 0 && data.data?.choices?.[0]?.message?.content) {
      messages.value.push({
        role: 'assistant',
        content: data.data.choices[0].message.content
      })
    } else {
      ElMessage.error(data?.message || 'AI服务暂不可用，请稍后再试')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
.ai-assistant {
  height: calc(100vh - 160px);
  display: flex;
  justify-content: center;
}

.chat-container {
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  overflow: hidden;
}

.chat-header {
  padding: 16px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message {
  margin-bottom: 16px;
}

.message-content {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.message.user .message-content {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message.user .avatar {
  background: #667eea;
  color: white;
}

.message.assistant .avatar {
  background: #f0f0f0;
  color: #666;
}

.text {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.message.user .text {
  background: #667eea;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .text {
  background: #f5f5f5;
  color: #333;
  border-bottom-left-radius: 4px;
}

.typing {
  color: #999;
  font-style: italic;
}

.chat-input {
  padding: 16px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-input .el-textarea {
  flex: 1;
}

.chat-input .el-button {
  height: 40px;
}
</style>
