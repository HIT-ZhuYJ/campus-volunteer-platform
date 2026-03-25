<template>
  <div class="register-container">
    <div class="register-background">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <div class="register-box">
      <div class="register-header">
        <div class="logo">
          <el-icon :size="36" color="white"><UserFilled /></el-icon>
        </div>
        <h1>创建账号</h1>
        <p>加入校园志愿服务平台，让爱心传递</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="register-form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名" size="large" clearable>
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="realName">
              <el-input v-model="form.realName" placeholder="真实姓名" size="large" clearable>
                <template #prefix><el-icon><Avatar /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="设置密码" size="large" show-password>
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" show-password @keyup.enter="handleRegister">
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="学号" size="large" clearable>
            <template #prefix><el-icon><Tickets /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" size="large" clearable>
            <template #prefix><el-icon><Phone /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱地址" size="large" clearable>
            <template #prefix><el-icon><Message /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" class="submit-button" @click="handleRegister" :loading="loading">
            <span v-if="!loading">立即注册</span>
          </el-button>
        </el-form-item>

        <div class="login-link">
          已有账号？
          <el-link type="primary" @click="$router.push('/login')" :underline="false">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  studentNo: '',
  phone: '',
  email: ''
})

const validatePassword = (_rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const { confirmPassword, ...data } = form
    await register(data)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px 0;
}

.register-background {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  animation: float 20s infinite ease-in-out;
}

.shape-1 { width: 350px; height: 350px; top: -120px; left: -120px; animation-delay: 0s; }
.shape-2 { width: 220px; height: 220px; bottom: -60px; right: -60px; animation-delay: 5s; }
.shape-3 { width: 160px; height: 160px; top: 40%; right: 8%; animation-delay: 10s; }

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(25px, -25px) scale(1.08); }
  66% { transform: translate(-18px, 18px) scale(0.92); }
}

.register-box {
  position: relative;
  width: 90%;
  max-width: 520px;
  padding: 44px 40px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.28);
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to   { opacity: 1; transform: translateY(0); }
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 18px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  box-shadow: 0 10px 28px rgba(102, 126, 234, 0.4);
}

.register-header h1 {
  font-size: 26px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0 0 8px 0;
}

.register-header p {
  font-size: 13px;
  color: #888;
  margin: 0;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.register-form :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #e4e7ed inset;
  border-radius: 10px;
  padding: 6px 14px;
  transition: all 0.3s;
}

.register-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea inset;
}

.register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #667eea inset;
}

.submit-button {
  width: 100%;
  height: 46px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 22px rgba(102, 126, 234, 0.42);
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: #888;
  margin-top: 4px;
}

@media (max-width: 576px) {
  .register-box { padding: 32px 20px; }
  .register-header h1 { font-size: 22px; }
  .shape { display: none; }
}
</style>
