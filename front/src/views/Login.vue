<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="logo-container">
        <img :src="logoUrl" alt="校徽" class="school-logo">
      </div>
      <h2>用户登录</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter.native="handleLogin"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-link type="primary" @click="$router.push('/register')">还没有账号？立即注册</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      logoUrl: '/4103920fcb585969e77a189b4f342387.png',
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.$http.post('/user/login', this.loginForm).then(res => {
            if (res.success) {
              localStorage.setItem('user', JSON.stringify(res.data))
              localStorage.setItem('token', 'token_' + res.data.id)
              this.$message.success('登录成功')
              this.$router.push('/')
            } else {
              this.$message.error(res.message)
            }
          }).catch(err => {
            this.$message.error('登录失败：' + (err.message || '网络错误'))
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 30px;
}

.logo-container {
  text-align: center;
  margin-bottom: 20px;
}

.school-logo {
  width: 120px;
  height: 120px;
  object-fit: contain;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>

