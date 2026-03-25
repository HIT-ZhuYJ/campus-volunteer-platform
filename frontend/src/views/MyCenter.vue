<template>
  <Layout>
    <div class="my-center-container">
      <el-row :gutter="20">
        <!-- 左栏：个人信息 -->
        <el-col :xs="24" :sm="24" :md="7" :lg="6">
          <!-- 个人卡片 -->
          <el-card class="profile-card" shadow="never">
            <div class="profile-gradient-header">
              <el-avatar :size="72" class="profile-avatar">
                {{ userInfo.realName?.charAt(0) }}
              </el-avatar>
              <h3 class="profile-name">{{ userInfo.realName }}</h3>
              <span class="profile-no">{{ userInfo.studentNo }}</span>
              <el-tag
                :type="userInfo.role === 'ADMIN' ? 'danger' : 'primary'"
                size="small"
                effect="light"
                class="role-tag"
              >
                {{ userInfo.role === 'ADMIN' ? '管理员' : '志愿者' }}
              </el-tag>
            </div>

            <div class="profile-fields">
              <div class="profile-field">
                <el-icon class="field-icon"><User /></el-icon>
                <span class="field-label">账号</span>
                <span class="field-value">{{ userInfo.username }}</span>
              </div>
              <div class="profile-field">
                <el-icon class="field-icon"><Phone /></el-icon>
                <span class="field-label">手机</span>
                <span class="field-value">{{ userInfo.phone || '未填写' }}</span>
              </div>
              <div class="profile-field">
                <el-icon class="field-icon"><Message /></el-icon>
                <span class="field-label">邮箱</span>
                <span class="field-value email">{{ userInfo.email || '未填写' }}</span>
              </div>
            </div>
          </el-card>

          <!-- 统计卡片组 -->
          <div class="stat-cards">
            <div class="stat-card stat-card--primary">
              <div class="stat-icon-wrap"><el-icon :size="22"><Timer /></el-icon></div>
              <div class="stat-body">
                <div class="stat-num">{{ userInfo.totalVolunteerHours || 0 }}</div>
                <div class="stat-desc">累计时长（小时）</div>
              </div>
            </div>
            <div class="stat-card stat-card--success">
              <div class="stat-icon-wrap"><el-icon :size="22"><Flag /></el-icon></div>
              <div class="stat-body">
                <div class="stat-num">{{ registrationCount }}</div>
                <div class="stat-desc">参与活动</div>
              </div>
            </div>
            <div class="stat-card stat-card--warning">
              <div class="stat-icon-wrap"><el-icon :size="22"><Select /></el-icon></div>
              <div class="stat-body">
                <div class="stat-num">{{ checkedInCount }}</div>
                <div class="stat-desc">已签到</div>
              </div>
            </div>
            <div class="stat-card stat-card--orange">
              <div class="stat-icon-wrap"><el-icon :size="22"><Trophy /></el-icon></div>
              <div class="stat-body">
                <div class="stat-num">{{ confirmedCount }}</div>
                <div class="stat-desc">已核销</div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 右栏：志愿足迹 -->
        <el-col :xs="24" :sm="24" :md="17" :lg="18">
          <el-card class="footprint-card" shadow="never">
            <template #header>
              <div class="footprint-header">
                <div class="header-left">
                  <el-icon class="header-icon"><Stamp /></el-icon>
                  <span>我的志愿足迹</span>
                </div>
                <span class="footprint-total">共 {{ registrations.length }} 条记录</span>
              </div>
            </template>

            <el-table
              :data="registrations"
              v-loading="loading"
              empty-text="暂无报名记录"
              stripe
            >
              <el-table-column label="活动名称" prop="activityTitle" min-width="180" show-overflow-tooltip>
                <template #default="{ row }">
                  <span class="activity-title-text">{{ row.activityTitle }}</span>
                </template>
              </el-table-column>
              <el-table-column label="时长" width="80" align="center">
                <template #default="{ row }">
                  <span class="hours-pill">{{ row.volunteerHours }}h</span>
                </template>
              </el-table-column>
              <el-table-column label="活动时间" width="120">
                <template #default="{ row }">
                  <span class="date-sm">{{ formatDate(row.startTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="报名时间" width="120">
                <template #default="{ row }">
                  <span class="date-sm">{{ formatDate(row.registrationTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="签到" width="90" align="center">
                <template #default="{ row }">
                  <el-tag
                    :type="row.checkInStatus === 1 ? 'success' : 'info'"
                    size="small"
                    effect="light"
                  >
                    {{ row.checkInStatus === 1 ? '已签到' : '未签到' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="时长核销" width="100" align="center">
                <template #default="{ row }">
                  <el-tag
                    :type="row.hoursConfirmed === 1 ? 'success' : 'warning'"
                    size="small"
                    effect="light"
                  >
                    {{ row.hoursConfirmed === 1 ? '已核销' : '待核销' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="90" align="center">
                <template #default="{ row }">
                  <el-tag
                    :type="row.status === 'REGISTERED' ? 'primary' : 'info'"
                    size="small"
                    effect="plain"
                  >
                    {{ row.status === 'REGISTERED' ? '已报名' : '已取消' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="!loading && registrations.length === 0" description="还没有报名记录，快去参加志愿活动吧！" />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import Layout from '@/components/Layout.vue'
import { getUserInfo } from '@/api/user'
import { getMyRegistrations } from '@/api/activity'
import dayjs from 'dayjs'

const loading = ref(false)
const userInfo = ref({})
const registrations = ref([])

const registrationCount = computed(() => registrations.value.length)
const checkedInCount    = computed(() => registrations.value.filter(r => r.checkInStatus === 1).length)
const confirmedCount    = computed(() => registrations.value.filter(r => r.hoursConfirmed === 1).length)

const formatDate = (date) => {
  if (!date) return '—'
  return dayjs(date).format('MM-DD HH:mm')
}

const fetchData = async () => {
  loading.value = true
  try {
    const [userRes, regRes] = await Promise.all([getUserInfo(), getMyRegistrations()])
    userInfo.value = userRes.data || {}
    registrations.value = regRes.data || []
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.my-center-container {
  max-width: 1280px;
  margin: 0 auto;
}

/* 个人卡片 */
.profile-card {
  border-radius: 14px;
  border: 1px solid #f0f0f0;
  overflow: hidden;
  margin-bottom: 16px;
}

.profile-gradient-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 28px 20px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: white;
  margin: -20px -20px 0;
}

.profile-avatar {
  background: rgba(255,255,255,0.3) !important;
  color: white !important;
  font-size: 28px;
  font-weight: 700;
  border: 3px solid rgba(255,255,255,0.5);
}

.profile-name {
  margin: 4px 0 0;
  font-size: 18px;
  font-weight: 700;
  color: white;
}

.profile-no {
  font-size: 13px;
  opacity: 0.85;
}

.role-tag {
  margin-top: 2px;
  border-radius: 12px;
}

.profile-fields {
  padding: 16px 4px 4px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.profile-field {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.field-icon { color: #909399; flex-shrink: 0; }
.field-label { color: #909399; width: 30px; flex-shrink: 0; }
.field-value { color: #303133; flex: 1; word-break: break-all; }
.field-value.email { font-size: 12px; }

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.stat-card {
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: transform 0.2s;
}

.stat-card:hover { transform: translateY(-2px); }

.stat-card--primary {
  background: linear-gradient(135deg, #667eea15, #667eea25);
  border: 1px solid #667eea30;
}
.stat-card--primary .stat-icon-wrap { color: #667eea; }
.stat-card--primary .stat-num { color: #667eea; }

.stat-card--success {
  background: linear-gradient(135deg, #67c23a15, #67c23a25);
  border: 1px solid #67c23a30;
}
.stat-card--success .stat-icon-wrap { color: #67c23a; }
.stat-card--success .stat-num { color: #67c23a; }

.stat-card--warning {
  background: linear-gradient(135deg, #e6a23c15, #e6a23c25);
  border: 1px solid #e6a23c30;
}
.stat-card--warning .stat-icon-wrap { color: #e6a23c; }
.stat-card--warning .stat-num { color: #e6a23c; }

.stat-card--orange {
  background: linear-gradient(135deg, #f5622115, #f5622125);
  border: 1px solid #f5622130;
}
.stat-card--orange .stat-icon-wrap { color: #f56221; }
.stat-card--orange .stat-num { color: #f56221; }

.stat-icon-wrap { flex-shrink: 0; }

.stat-num {
  font-size: 22px;
  font-weight: 700;
  line-height: 1;
}

.stat-desc {
  font-size: 11px;
  color: #909399;
  margin-top: 3px;
}

/* 足迹卡片 */
.footprint-card {
  border-radius: 14px;
  border: 1px solid #f0f0f0;
}

.footprint-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.header-icon { color: #667eea; }

.footprint-total {
  font-size: 13px;
  color: #909399;
}

.activity-title-text {
  font-weight: 500;
  color: #303133;
}

.hours-pill {
  display: inline-block;
  background: linear-gradient(135deg, #667eea20, #764ba220);
  color: #667eea;
  font-weight: 600;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
}

.date-sm {
  font-size: 12px;
  color: #606266;
}

@media (max-width: 768px) {
  .stat-cards { grid-template-columns: 1fr 1fr; }
  .footprint-card { margin-top: 16px; }
}

@media (max-width: 480px) {
  .stat-cards { grid-template-columns: 1fr; }
}
</style>
