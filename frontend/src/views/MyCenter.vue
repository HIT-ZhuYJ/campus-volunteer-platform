<template>
  <Layout>
    <div class="my-center-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="profile-card">
            <div class="profile-header">
              <el-avatar :size="80" style="background: #409eff">
                {{ userInfo.realName?.charAt(0) }}
              </el-avatar>
              <h3>{{ userInfo.realName }}</h3>
              <p>{{ userInfo.studentNo }}</p>
            </div>
            <el-divider />
            <div class="profile-info">
              <div class="info-item">
                <span class="label">账号：</span>
                <span>{{ userInfo.username }}</span>
              </div>
              <div class="info-item">
                <span class="label">手机：</span>
                <span>{{ userInfo.phone }}</span>
              </div>
              <div class="info-item">
                <span class="label">邮箱：</span>
                <span>{{ userInfo.email }}</span>
              </div>
              <div class="info-item">
                <span class="label">角色：</span>
                <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'primary'">
                  {{ userInfo.role === 'ADMIN' ? '管理员' : '志愿者' }}
                </el-tag>
              </div>
            </div>
          </el-card>

          <el-card class="stats-card">
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.totalVolunteerHours || 0 }}</div>
              <div class="stat-label">累计志愿时长(小时)</div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="18">
          <el-card>
            <template #header>
              <div class="card-header">
                <h3>我的志愿足迹</h3>
              </div>
            </template>

            <el-table :data="registrations" v-loading="loading">
              <el-table-column label="活动名称" prop="activityTitle" min-width="200" />
              <el-table-column label="服务地点" prop="location" width="150" />
              <el-table-column label="志愿时长" width="100">
                <template #default="{ row }">
                  {{ row.volunteerHours }} 小时
                </template>
              </el-table-column>
              <el-table-column label="活动时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.startTime) }}
                </template>
              </el-table-column>
              <el-table-column label="报名时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.registrationTime) }}
                </template>
              </el-table-column>
              <el-table-column label="签到状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.checkInStatus === 1 ? 'success' : 'info'">
                    {{ row.checkInStatus === 1 ? '已签到' : '未签到' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="时长核销" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.hoursConfirmed === 1 ? 'success' : 'warning'">
                    {{ row.hoursConfirmed === 1 ? '已核销' : '待核销' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'REGISTERED' ? 'success' : 'info'">
                    {{ row.status === 'REGISTERED' ? '已报名' : '已取消' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Layout from '@/components/Layout.vue'
import { getUserInfo } from '@/api/user'
import { getMyRegistrations } from '@/api/activity'
import dayjs from 'dayjs'

const loading = ref(false)
const userInfo = ref({})
const registrations = ref([])

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const fetchData = async () => {
  loading.value = true
  try {
    const [userRes, regRes] = await Promise.all([
      getUserInfo(),
      getMyRegistrations()
    ])
    userInfo.value = userRes.data || {}
    registrations.value = regRes.data || []
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.my-center-container {
  padding: 20px;
}

.profile-card {
  margin-bottom: 20px;
}

.profile-header {
  text-align: center;
}

.profile-header h3 {
  margin: 15px 0 5px;
  color: #333;
}

.profile-header p {
  color: #999;
  font-size: 14px;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.info-item .label {
  color: #999;
  width: 60px;
}

.stats-card {
  text-align: center;
}

.stat-item {
  padding: 20px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.card-header h3 {
  margin: 0;
}
</style>
