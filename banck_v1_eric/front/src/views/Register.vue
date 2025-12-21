<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2>用户注册</h2>
      <el-form :model="registerForm" :rules="rules" ref="registerForm" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码"></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="registerForm.studentId" placeholder="请输入学号"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" style="width: 100%">注册</el-button>
        </el-form-item>
        <el-form-item>
          <el-link type="primary" @click="$router.push('/login')">已有账号？立即登录</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        studentId: '',
        phone: '',
        email: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        studentId: [
          { required: true, message: '请输入学号', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleRegister() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          const { confirmPassword, ...formData } = this.registerForm
          this.$http.post('/user/register', formData).then(res => {
            if (res.success) {
              this.$message.success('注册成功，请登录')
              this.$router.push('/login')
            } else {
              this.$message.error(res.message)
            }
          }).catch(err => {
            this.$message.error('注册失败：' + (err.message || '网络错误'))
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 500px;
  max-width: 100%;
  padding: 30px;
}

.register-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>

