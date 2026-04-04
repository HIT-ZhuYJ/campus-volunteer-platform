<template>
  <div class="auth-shell">
    <div class="auth-bg">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
    </div>

    <div class="auth-card">
      <div class="auth-brand">
        <div class="brand-icon">
          <el-icon :size="24"><Trophy /></el-icon>
        </div>
        <h1>Campus Volunteer Service</h1>
        <p>连接校园热情，记录真实公益行动</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="auth-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="submit-btn"
            @click="handleLogin"
            :loading="loading"
          >
            登录
          </el-button>
        </el-form-item>

        <el-form-item>
          <el-button
            size="large"
            class="ghost-btn"
            @click="$router.push('/register')"
          >
            注册新账号
          </el-button>
        </el-form-item>
      </el-form>

      <div class="quick-login">
        <el-divider>快捷登录</el-divider>
        <div class="quick-tags">
          <el-tag
            v-for="account in testAccounts"
            :key="account.username"
            :type="account.type"
            effect="plain"
            class="quick-tag"
            @click="quickLogin(account)"
          >
            {{ account.label }}
          </el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const testAccounts = [
  { username: 'admin', password: 'password123', label: '管理员', type: 'danger' },
  { username: 'student01', password: 'password123', label: '学生01', type: 'success' },
  { username: 'student02', password: 'password123', label: '学生02', type: 'warning' }
]

const quickLogin = (account) => {
  form.username = account.username
  form.password = account.password
  handleLogin()
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true

  try {
    const res = await login(form)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.userInfo)

    ElMessage.success('登录成功')

    if (res.data.userInfo.role === 'ADMIN') {
      router.push('/admin/activities')
    } else {
      router.push('/home')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-shell {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: clamp(18px, 5vw, 30px) 14px;
  background:
    radial-gradient(circle at 0% 0%, #dce8ff 0%, transparent 38%),
    radial-gradient(circle at 100% 100%, #d9f4e7 0%, transparent 30%),
    var(--cv-bg);
  position: relative;
  overflow: hidden;
}

.auth-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 999px;
}

.orb-1 {
  width: clamp(180px, 26vw, 280px);
  height: clamp(180px, 26vw, 280px);
  background: rgba(47, 126, 244, 0.2);
  top: -60px;
  right: -40px;
}

.orb-2 {
  width: clamp(220px, 30vw, 340px);
  height: clamp(220px, 30vw, 340px);
  background: rgba(15, 139, 95, 0.14);
  left: -120px;
  bottom: -130px;
}

.auth-card {
  position: relative;
  z-index: 2;
  width: min(520px, 100%);
  padding: clamp(22px, 4vw, 36px) clamp(16px, 4vw, 30px);
  background: rgba(255, 255, 255, 0.84);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(201, 214, 243, 0.56);
  border-radius: 24px;
  box-shadow: 0 26px 44px rgba(13, 71, 217, 0.16);
}

.auth-brand {
  text-align: center;
  margin-bottom: 24px;
}

.brand-icon {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  margin: 0 auto 14px;
  display: grid;
  place-items: center;
  color: #fff;
  background: linear-gradient(135deg, var(--cv-primary), var(--cv-primary-weak));
}

.auth-brand h1 {
  font-size: clamp(22px, 4.8vw, 30px);
  line-height: 1.2;
  margin-bottom: 8px;
  color: var(--cv-text);
}

.auth-brand p {
  font-size: 13px;
  color: var(--cv-text-soft);
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.submit-btn,
.ghost-btn {
  width: 100%;
  height: 46px;
  border-radius: 999px;
  font-weight: 700;
}

.ghost-btn {
  color: var(--cv-primary);
  border: 1px solid rgba(13, 71, 217, 0.28);
  background: #fff;
}

.quick-login {
  margin-top: 6px;
}

.quick-tags {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
}

.quick-tag {
  cursor: pointer;
  border-radius: 999px;
  padding: 8px 12px;
}

@media (max-width: 520px) {
  .quick-tags {
    display: grid;
    grid-template-columns: 1fr 1fr;
  }

  .quick-tag {
    text-align: center;
  }
}

@media (max-width: 380px) {
  .quick-tags {
    grid-template-columns: 1fr;
  }
}
</style>
