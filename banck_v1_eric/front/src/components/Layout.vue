<template>
  <div class="layout-container">
    <el-header class="main-header">
      <div class="header-content">
        <div class="logo-section" @click="$router.push('/')">
          <img :src="logoUrl" alt="校徽" class="logo-img" @error="handleImageError">
          <span class="site-title">Umi校园二手交易平台</span>
        </div>
        <div class="nav-section">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            @select="handleMenuSelect"
            class="main-menu">
            <el-menu-item index="products">商品</el-menu-item>
            <el-menu-item index="helps">求助</el-menu-item>
            <el-menu-item index="publish" v-if="user">发布</el-menu-item>
            <el-menu-item index="chats" v-if="user">消息</el-menu-item>
          </el-menu>
        </div>
        <div class="user-section">
          <el-button v-if="!user" @click="$router.push('/login')" type="primary" size="small">登录</el-button>
          <el-button v-if="!user" @click="$router.push('/register')" size="small">注册</el-button>
          <el-dropdown v-if="user" @command="handleCommand" trigger="click">
            <span class="user-info">
              <i class="el-icon-user"></i>
              {{ user.realName }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="center">个人中心</el-dropdown-item>
              <el-dropdown-item command="my-products">我的商品</el-dropdown-item>
              <el-dropdown-item command="my-helps">我的求助</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <el-main class="main-content">
      <slot></slot>
    </el-main>
  </div>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      user: null,
      logoUrl: '/4103920fcb585969e77a189b4f342387.png'
    }
  },
  computed: {
    activeMenu() {
      const path = this.$route.path
      if (path.startsWith('/product')) return 'products'
      if (path.startsWith('/help')) return 'helps'
      if (path.startsWith('/chat')) return 'chats'
      if (path.includes('publish')) return 'publish'
      return 'products'
    }
  },
  mounted() {
    this.user = JSON.parse(localStorage.getItem('user') || 'null')
  },
  watch: {
    $route() {
      this.user = JSON.parse(localStorage.getItem('user') || 'null')
    }
  },
  methods: {
    handleMenuSelect(key) {
      if (key === 'products') {
        this.$router.push('/')
      } else if (key === 'helps') {
        this.$router.push('/helps')
      } else if (key === 'publish') {
        this.$router.push('/product/publish')
      } else if (key === 'chats') {
        this.$router.push('/chats')
      }
    },
    handleCommand(command) {
      if (command === 'center') {
        this.$router.push('/user/center')
      } else if (command === 'my-products') {
        this.$router.push('/user/center?tab=products')
      } else if (command === 'my-helps') {
        this.$router.push('/user/center?tab=helps')
      } else if (command === 'logout') {
        localStorage.removeItem('user')
        localStorage.removeItem('token')
        this.user = null
        this.$message.success('已退出登录')
        this.$router.push('/')
      }
    },
    handleImageError() {
      // 如果图片加载失败，使用默认占位符
      this.logoUrl = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTA%2FJSIgeT0iNTAlJSIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuaXoOWbvueJhzwvdGV4dD48L3N2Zz4='
    }
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #bbdefb 0%, #ce93d8 30%, #a5d6a7 60%, #90caf9 100%);
}

.main-header {
  background: linear-gradient(120deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: 0 10px 30px rgba(0,0,0,0.06);
  padding: 0;
  height: 88px !important;
  line-height: 88px;
  position: sticky;
  top: 0;
  z-index: 99;
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0,0,0,0.03);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 28px;
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
}

.logo-section {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 10px;
}

.logo-img {
  width: 88px;
  height: 88px;
  object-fit: contain;
}

.site-title {
  font-size: 24px;
  font-weight: bold;
  color: #2b3a67;
}

.nav-section {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 0 20px;
}

.main-menu {
  border-bottom: none;
  background: transparent !important;
  display: flex;
  gap: 10px;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
}

.user-info {
  cursor: pointer;
  color: #333;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.main-content {
  flex: 1;
  background: transparent;
  padding: 24px;
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
}
</style>

