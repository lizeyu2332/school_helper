<template>
  <Layout>
    <div class="chat-list-page">
      <el-card>
        <div slot="header">
          <span>我的消息</span>
        </div>
        <div v-if="chatList.length === 0" class="empty-chats">
          <el-empty description="暂无聊天记录"></el-empty>
        </div>
        <div v-else>
          <div 
            v-for="chat in chatList" 
            :key="chat.userId"
            class="chat-item"
            @click="openChat(chat.userId)">
            <div class="chat-avatar">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="chat-info">
              <div class="chat-name">{{ chat.userName }}</div>
              <div class="chat-preview">{{ chat.lastMessage }}</div>
            </div>
            <div class="chat-meta">
              <div class="chat-time">{{ formatTime(chat.lastMessageTime) }}</div>
              <el-badge v-if="chat.unreadCount > 0" :value="chat.unreadCount" class="unread-badge"></el-badge>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </Layout>
</template>

<script>
import Layout from '../components/Layout.vue'

export default {
  name: 'ChatList',
  components: {
    Layout
  },
  data() {
    return {
      chatList: [],
      user: null,
      timer: null
    }
  },
  mounted() {
    this.user = JSON.parse(localStorage.getItem('user') || 'null')
    if (!this.user) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }
    this.loadChatList()
    // 定时刷新聊天列表
    this.timer = setInterval(() => {
      this.loadChatList()
    }, 5000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    loadChatList() {
      // 获取所有未读消息
      this.$http.get(`/chat/unread/${this.user.id}`).then(res => {
        if (res.success) {
          const unreadMessages = res.data || []
          // 按发送者分组
          const chatMap = new Map()
          
          unreadMessages.forEach(msg => {
            const otherUserId = msg.fromUserId === this.user.id ? msg.toUserId : msg.fromUserId
            if (!chatMap.has(otherUserId)) {
              chatMap.set(otherUserId, {
                userId: otherUserId,
                userName: '用户' + otherUserId, // 临时，需要获取用户名
                unreadCount: 0,
                lastMessage: '',
                lastMessageTime: null
              })
            }
            const chat = chatMap.get(otherUserId)
            if (msg.toUserId === this.user.id && msg.isRead === 0) {
              chat.unreadCount++
            }
            if (!chat.lastMessageTime || new Date(msg.createTime) > new Date(chat.lastMessageTime)) {
              chat.lastMessage = msg.content
              chat.lastMessageTime = msg.createTime
            }
          })
          
          // 获取所有聊天记录，找出所有聊天对象
          this.$http.get(`/chat/all/${this.user.id}`).then(res2 => {
            if (res2.success) {
              // 处理所有聊天记录，找出所有聊天对象
              res2.data.forEach(msg => {
                const otherUserId = msg.fromUserId === this.user.id ? msg.toUserId : msg.fromUserId
                if (!chatMap.has(otherUserId)) {
                  chatMap.set(otherUserId, {
                    userId: otherUserId,
                    userName: '用户' + otherUserId,
                    unreadCount: 0,
                    lastMessage: msg.content,
                    lastMessageTime: msg.createTime
                  })
                } else {
                  const chat = chatMap.get(otherUserId)
                  if (!chat.lastMessageTime || new Date(msg.createTime) > new Date(chat.lastMessageTime)) {
                    chat.lastMessage = msg.content
                    chat.lastMessageTime = msg.createTime
                  }
                }
              })
              
              // 加载用户信息
              const userIds = Array.from(chatMap.keys())
              const loadUserPromises = userIds.map(userId => {
                return this.$http.get(`/user/${userId}`).then(userRes => {
                  if (userRes.success) {
                    const chat = chatMap.get(userId)
                    if (chat) {
                      chat.userName = userRes.data.realName
                    }
                  }
                })
              })
              
              Promise.all(loadUserPromises).then(() => {
                this.chatList = Array.from(chatMap.values()).sort((a, b) => {
                  const timeA = a.lastMessageTime ? new Date(a.lastMessageTime) : new Date(0)
                  const timeB = b.lastMessageTime ? new Date(b.lastMessageTime) : new Date(0)
                  return timeB - timeA
                })
              })
            }
          }).catch(() => {
            // 如果接口不存在，使用简化版本
            this.chatList = Array.from(chatMap.values())
          })
        }
      })
    },
    openChat(userId) {
      this.$router.push(`/chat/${userId}`)
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
    }
  }
}
</script>

<style scoped>
.chat-list-page {
  width: 100%;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.3s;
}

.chat-item:hover {
  background: #f5f5f5;
}

.chat-item:last-child {
  border-bottom: none;
}

.chat-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #409EFF;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 15px;
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.chat-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 5px;
  color: #333;
}

.chat-preview {
  font-size: 14px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.chat-time {
  font-size: 12px;
  color: #999;
}

.empty-chats {
  padding: 40px 0;
}
</style>

