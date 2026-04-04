<template>
  <div class="register-shell">
    <div class="register-panel">
      <div class="register-left">
        <div class="left-brand">
          <div class="left-icon"><el-icon><UserFilled /></el-icon></div>
          <h1>Create Your Impact Account</h1>
          <p>加入校园志愿社区，积累可追踪的公益履历。</p>
        </div>
        <ul class="left-points">
          <li>统一管理报名记录与签到状态</li>
          <li>自动沉淀个人志愿时长</li>
          <li>支持后续活动数据追踪</li>
        </ul>
      </div>

      <div class="register-right">
        <h2>注册账号</h2>
        <el-form :model="form" :rules="rules" ref="formRef" class="register-form">
          <el-row :gutter="14">
            <el-col :xs="24" :sm="12">
              <el-form-item prop="username">
                <el-input v-model="form.username" placeholder="用户名" size="large" clearable>
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item prop="realName">
                <el-input v-model="form.realName" placeholder="真实姓名" size="large" clearable>
                  <template #prefix><el-icon><Avatar /></el-icon></template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="14">
            <el-col :xs="24" :sm="12">
              <el-form-item prop="password">
                <el-input v-model="form.password" type="password" placeholder="设置密码" size="large" show-password>
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
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
            <el-button type="primary" size="large" class="submit-btn" @click="handleRegister" :loading="loading">
              立即注册
            </el-button>
          </el-form-item>

          <div class="login-link">
            已有账号？
            <el-link type="primary" @click="$router.push('/login')" :underline="false">去登录</el-link>
          </div>
        </el-form>
      </div>
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
.register-shell {
  min-height: 100vh;
  padding: clamp(18px, 4vw, 28px) 14px;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at 8% 14%, #dbe8ff, transparent 42%),
    radial-gradient(circle at 88% 92%, #daf3e7, transparent 33%),
    var(--cv-bg);
}

.register-panel {
  width: min(1040px, 100%);
  display: grid;
  grid-template-columns: minmax(260px, 1fr) minmax(320px, 1.2fr);
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(201, 214, 243, 0.58);
  border-radius: 26px;
  box-shadow: 0 26px 40px rgba(13, 71, 217, 0.14);
  overflow: hidden;
}

.register-left {
  padding: clamp(24px, 3vw, 34px) clamp(20px, 3vw, 30px);
  background: linear-gradient(150deg, var(--cv-primary), var(--cv-primary-weak));
  color: #fff;
}

.left-brand h1 {
  margin: 14px 0 10px;
  font-size: clamp(28px, 3vw, 36px);
  line-height: 1.2;
}

.left-brand p {
  font-size: 14px;
  opacity: 0.94;
}

.left-icon {
  width: 54px;
  height: 54px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.18);
}

.left-points {
  margin-top: 24px;
  padding-left: 18px;
  line-height: 1.9;
  font-size: 14px;
}

.register-right {
  padding: clamp(22px, 3vw, 34px) clamp(16px, 3vw, 28px);
}

.register-right h2 {
  margin-bottom: 18px;
  font-size: clamp(24px, 3vw, 30px);
  color: var(--cv-text);
}

.register-form :deep(.el-form-item) {
  margin-bottom: 15px;
}

.submit-btn {
  width: 100%;
  height: 46px;
  border-radius: 999px;
  font-weight: 700;
}

.login-link {
  text-align: center;
  color: #6a6e82;
  font-size: 14px;
}

@media (max-width: 940px) {
  .register-panel {
    grid-template-columns: 1fr;
    width: min(680px, 100%);
  }
}

@media (max-width: 560px) {
  .left-points {
    margin-top: 18px;
    line-height: 1.75;
  }
}
</style>
