<template>
  <Layout>
    <div class="chat-container">
      <el-card>
        <div slot="header" class="chat-header">
          <el-button @click="$router.push('/chats')" icon="el-icon-arrow-left" size="small">返回</el-button>
          <span class="chat-title">{{ targetUser ? targetUser.realName : '聊天' }}</span>
        </div>
        <div class="chat-main">
          <div class="chat-messages" ref="messagesContainer">
            <div
              v-for="message in messages"
              :key="message.id"
              :class="['message-item', message.fromUserId === currentUserId ? 'message-right' : 'message-left']">
              <div class="message-content">
                <div class="message-text">{{ message.content }}</div>
                <div class="message-time">{{ formatTime(message.createTime) }}</div>
              </div>
            </div>
          </div>
          <div class="chat-input">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="输入消息..."
              @keyup.ctrl.enter.native="sendMessage">
            </el-input>
            <el-button type="primary" @click="sendMessage" style="margin-top: 10px;">发送</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </Layout>
</template>

<script>
import Layout from '../components/Layout.vue'

export default {
  components: {
    Layout
  },
  name: 'Chat',
  data() {
    return {
      messages: [],
      inputMessage: '',
      targetUserId: null,
      currentUserId: null,
      targetUser: null,
      productId: null,
      helpId: null,
      timer: null
    }
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem('user') || 'null')
    if (!user) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }
    this.currentUserId = user.id
    this.targetUserId = parseInt(this.$route.params.userId)
    this.productId = this.$route.query.productId ? parseInt(this.$route.query.productId) : null
    this.helpId = this.$route.query.helpId ? parseInt(this.$route.query.helpId) : null
    
    this.loadTargetUser()
    this.loadMessages()
    // 定时轮询消息（每3秒）
    this.timer = setInterval(() => {
      this.loadMessages()
    }, 3000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    loadTargetUser() {
      this.$http.get(`/user/${this.targetUserId}`).then(res => {
        if (res.success) {
          this.targetUser = res.data
        }
      })
    },
    loadMessages() {
      this.$http.get('/chat/history', {
        params: {
          userId1: this.currentUserId,
          userId2: this.targetUserId
        }
      }).then(res => {
        if (res.success) {
          const oldLength = this.messages.length
          this.messages = res.data
          // 标记已读
          if (this.messages.length > 0) {
            this.$http.put('/chat/readAll', null, {
              params: {
                fromUserId: this.targetUserId,
                toUserId: this.currentUserId
              }
            })
          }
          // 如果是新消息，滚动到底部
          if (this.messages.length > oldLength) {
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          }
        }
      })
    },
    sendMessage() {
      if (!this.inputMessage.trim()) {
        return
      }
      const message = {
        fromUserId: this.currentUserId,
        toUserId: this.targetUserId,
        productId: this.productId,
        helpId: this.helpId,
        content: this.inputMessage.trim()
      }
      this.$http.post('/chat/send', message).then(res => {
        if (res.success) {
          this.inputMessage = ''
          this.loadMessages()
        } else {
          this.$message.error(res.message)
        }
      })
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      if (diff < 60000) {
        return '刚刚'
      } else if (diff < 3600000) {
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) {
        return Math.floor(diff / 3600000) + '小时前'
      } else {
        return date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
      }
    },
    scrollToBottom() {
      const container = this.$refs.messagesContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    }
  }
}
</script>

<style scoped>
.chat-container {
  width: 100%;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 15px;
}

.chat-title {
  font-size: 18px;
  font-weight: bold;
}

.chat-main {
  display: flex;
  flex-direction: column;
  height: 600px;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f5;
}

.message-item {
  margin-bottom: 15px;
  display: flex;
}

.message-right {
  justify-content: flex-end;
}

.message-left {
  justify-content: flex-start;
}

.message-content {
  max-width: 60%;
  padding: 10px 15px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.message-right .message-content {
  background: #409EFF;
  color: #fff;
}

.message-text {
  word-wrap: break-word;
  margin-bottom: 5px;
}

.message-time {
  font-size: 12px;
  color: #999;
  text-align: right;
}

.message-right .message-time {
  color: rgba(255,255,255,0.7);
}

.chat-input {
  padding: 15px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
}
</style>
