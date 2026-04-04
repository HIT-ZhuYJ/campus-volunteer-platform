<template>
  <Layout>
    <div class="profile-page page-container" v-loading="loading">
      <el-row :gutter="16">
        <el-col :xs="24" :md="8" :lg="6">
          <el-card class="profile-card" shadow="never">
            <div class="profile-head">
              <el-avatar :size="78" class="avatar">{{ userInfo.realName?.charAt(0) || 'U' }}</el-avatar>
              <h3>{{ userInfo.realName || '未命名用户' }}</h3>
              <p>{{ userInfo.studentNo || '--' }}</p>
              <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'primary'" effect="light">
                {{ userInfo.role === 'ADMIN' ? '管理员' : '志愿者' }}
              </el-tag>
            </div>

            <div class="fields">
              <div class="field"><span>账号</span><b>{{ userInfo.username || '--' }}</b></div>
              <div class="field"><span>手机</span><b>{{ userInfo.phone || '--' }}</b></div>
              <div class="field"><span>邮箱</span><b>{{ userInfo.email || '--' }}</b></div>
            </div>

            <div class="actions">
              <el-button type="primary" @click="showEditDialog = true">
                <el-icon><Edit /></el-icon>编辑资料
              </el-button>
              <el-button @click="showPasswordDialog = true">
                <el-icon><Lock /></el-icon>修改密码
              </el-button>
            </div>
          </el-card>

          <div class="stats-grid">
            <div class="mini-stat">
              <div class="num">{{ userInfo.totalVolunteerHours || 0 }}</div>
              <div class="label">累计时长</div>
            </div>
            <div class="mini-stat">
              <div class="num">{{ registrationCount }}</div>
              <div class="label">参与活动</div>
            </div>
            <div class="mini-stat">
              <div class="num">{{ checkedInCount }}</div>
              <div class="label">已签到</div>
            </div>
            <div class="mini-stat">
              <div class="num">{{ confirmedCount }}</div>
              <div class="label">已核销</div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :md="16" :lg="18">
          <el-card class="guide-card" shadow="never">
            <template #header><h3>功能说明</h3></template>
            <div class="guide-item">
              <div class="title">编辑资料</div>
              <div class="desc">修改姓名、手机号、邮箱。保存后立即生效。</div>
            </div>
            <div class="guide-item">
              <div class="title">修改密码</div>
              <div class="desc">修改成功后会清除登录状态并跳转登录页。</div>
            </div>
            <div class="guide-item">
              <div class="title">查看志愿足迹</div>
              <div class="desc">在导航栏进入“我的志愿足迹”查看报名、签到、核销记录。</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-dialog
        v-model="showEditDialog"
        title="编辑资料"
        width="min(500px, 94vw)"
        :close-on-click-modal="false"
      >
        <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="editForm.realName" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="editForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="editForm.email" type="email" placeholder="请输入邮箱" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateProfile" :loading="updateLoading">确定</el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="showPasswordDialog"
        title="修改密码"
        width="min(500px, 94vw)"
        :close-on-click-modal="false"
      >
        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入旧密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码（6-20位）"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpdatePassword" :loading="updateLoading">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const updateLoading = ref(false)
const userInfo = ref({})

const showEditDialog = ref(false)
const showPasswordDialog = ref(false)
const editFormRef = ref()
const passwordFormRef = ref()

const editForm = ref({
  realName: '',
  phone: '',
  email: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const registrationCount = computed(() => 0)
const checkedInCount = computed(() => 0)
const confirmedCount = computed(() => 0)

const editRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value === passwordForm.value.newPassword) {
          callback()
        } else {
          callback(new Error('两次输入的密码不一致'))
        }
      },
      trigger: 'blur'
    }
  ]
}

const fetchData = async () => {
  loading.value = true
  try {
    const userRes = await getUserInfo()
    userInfo.value = userRes.data || {}
    editForm.value = {
      realName: userInfo.value.realName || '',
      phone: userInfo.value.phone || '',
      email: userInfo.value.email || ''
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleUpdateProfile = async () => {
  await editFormRef.value.validate()
  updateLoading.value = true
  try {
    await updateUserInfo(editForm.value)
    ElMessage.success('资料更新成功')
    showEditDialog.value = false
    userInfo.value.realName = editForm.value.realName
    userInfo.value.phone = editForm.value.phone
    userInfo.value.email = editForm.value.email
    editForm.value = {
      realName: '',
      phone: '',
      email: ''
    }
  } catch (error) {
    console.error('更新失败:', error)
  } finally {
    updateLoading.value = false
  }
}

const handleUpdatePassword = async () => {
  await passwordFormRef.value.validate()
  updateLoading.value = true
  try {
    await updatePassword(passwordForm.value)
    ElMessage.success('密码修改成功，请重新登录')
    showPasswordDialog.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    setTimeout(() => {
      router.push('/login')
    }, 1000)
  } catch (error) {
    console.error('密码修改失败:', error)
  } finally {
    updateLoading.value = false
  }
}

onMounted(() => { fetchData() })

watch(showEditDialog, (newValue) => {
  if (newValue && userInfo.value.realName) {
    editForm.value = {
      realName: userInfo.value.realName,
      phone: userInfo.value.phone || '',
      email: userInfo.value.email || ''
    }
  }
})
</script>

<style scoped>
.profile-page {
  width: 100%;
  margin: 0 auto;
}

.profile-card {
  overflow: hidden;
}

.profile-head {
  margin: -20px -20px 0;
  padding: 24px 20px;
  text-align: center;
  background: linear-gradient(135deg, var(--cv-primary), var(--cv-primary-weak));
  color: white;
}

.avatar {
  border: 3px solid rgba(255, 255, 255, 0.45);
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
  font-size: 28px;
  font-weight: 800;
}

.profile-head h3 {
  margin-top: 10px;
  font-size: 22px;
}

.profile-head p {
  margin: 3px 0 8px;
  opacity: 0.88;
}

.fields {
  padding: 14px 2px 8px;
}

.field {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px dashed rgba(196, 197, 216, 0.6);
}

.field span {
  color: #80869d;
}

.field b {
  max-width: 62%;
  text-align: right;
  color: #33384d;
  word-break: break-all;
}

.actions {
  margin-top: 12px;
  display: grid;
  gap: 10px;
}

.actions .el-button {
  width: 100%;
}

.stats-grid {
  margin-top: 14px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.mini-stat {
  border-radius: 14px;
  padding: 12px;
  background: #f3f2ff;
}

.mini-stat .num {
  font-size: 24px;
  font-weight: 800;
  color: #003dca;
  line-height: 1;
}

.mini-stat .label {
  font-size: 12px;
  margin-top: 4px;
  color: #70758c;
}

.guide-card h3 {
  font-size: 22px;
}

.guide-item {
  border-radius: 14px;
  padding: 14px;
  margin-bottom: 10px;
  background: #f3f2ff;
}

.guide-item .title {
  font-weight: 700;
  margin-bottom: 4px;
}

.guide-item .desc {
  color: #61667d;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>




