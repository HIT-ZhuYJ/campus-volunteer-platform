<template>
  <Layout>
    <div class="activity-detail-container" v-loading="loading">
      <template v-if="activity.id">
        <!-- Hero 区域 -->
        <div class="activity-hero">
          <div class="hero-content">
            <div class="hero-tags">
              <el-tag :type="activityPhaseDisplay.type" size="large" class="hero-tag">
                {{ activityPhaseDisplay.text }}
              </el-tag>
              <el-tag :type="recruitmentDisplay.type" size="large" class="hero-tag">
                {{ recruitmentDisplay.text }}
              </el-tag>
              <el-tag effect="plain" size="large" class="hero-tag category-tag">
                {{ activity.category }}
              </el-tag>
            </div>
            <h1 class="hero-title">{{ activity.title }}</h1>
            <div class="hero-meta">
              <span class="meta-item">
                <el-icon><Location /></el-icon>{{ activity.location }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>{{ activity.volunteerHours }} 小时
              </span>
              <span class="meta-item">
                <el-icon><User /></el-icon>{{ activity.currentParticipants }} / {{ activity.maxParticipants }} 人
              </span>
            </div>
          </div>
        </div>

        <el-row :gutter="20" class="detail-body">
          <!-- 左栏：详情 -->
          <el-col :xs="24" :sm="24" :md="16" :lg="16">
            <!-- 活动详情 -->
            <el-card class="detail-card" shadow="never">
              <template #header>
                <div class="card-title">
                  <el-icon class="card-icon"><Document /></el-icon>
                  活动详情
                </div>
              </template>
              <div class="description-content">{{ activity.description || '暂无详情' }}</div>
            </el-card>

            <!-- 时间信息 -->
            <el-card class="detail-card" shadow="never">
              <template #header>
                <div class="card-title">
                  <el-icon class="card-icon"><Calendar /></el-icon>
                  时间安排
                </div>
              </template>
              <div class="time-grid">
                <div class="time-block">
                  <div class="time-label">招募开始</div>
                  <div class="time-value">{{ formatDate(activity.registrationStartTime) }}</div>
                </div>
                <div class="time-block time-block--deadline">
                  <div class="time-label">报名截止</div>
                  <div class="time-value">{{ formatDate(activity.registrationDeadline) }}</div>
                </div>
                <div class="time-block">
                  <div class="time-label">活动开始</div>
                  <div class="time-value">{{ formatDate(activity.startTime) }}</div>
                </div>
                <div class="time-block">
                  <div class="time-label">活动结束</div>
                  <div class="time-value">{{ formatDate(activity.endTime) }}</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 右栏：报名操作 -->
          <el-col :xs="24" :sm="24" :md="8" :lg="8">
            <!-- 名额进度 -->
            <el-card class="sidebar-card" shadow="never">
              <template #header>
                <div class="card-title">
                  <el-icon class="card-icon"><User /></el-icon>
                  报名情况
                </div>
              </template>
              <div class="slots-info">
                <div class="slots-numbers">
                  <span class="slots-current">{{ activity.currentParticipants }}</span>
                  <span class="slots-sep">/</span>
                  <span class="slots-max">{{ activity.maxParticipants }}</span>
                  <span class="slots-unit">人</span>
                </div>
                <el-progress
                  :percentage="Math.min(Math.round((activity.currentParticipants / activity.maxParticipants) * 100), 100)"
                  :color="slotsProgressColor"
                  :stroke-width="8"
                  style="margin-top: 12px"
                />
                <div class="slots-hint">
                  <template v-if="activity.availableSlots > 0">
                    剩余 <strong>{{ activity.availableSlots }}</strong> 个名额
                  </template>
                  <template v-else>
                    <el-text type="danger">名额已满</el-text>
                  </template>
                </div>
              </div>
            </el-card>

            <!-- 报名操作 -->
            <el-card class="sidebar-card action-card" shadow="never">
              <template v-if="canRegister">
                <el-button
                  type="primary"
                  size="large"
                  class="register-btn"
                  :disabled="activity.availableSlots <= 0"
                  @click="handleRegister"
                >
                  <el-icon><Check /></el-icon>
                  {{ activity.availableSlots > 0 ? '立即报名' : '名额已满' }}
                </el-button>
                <p class="action-hint">报名后请关注通知，按时参加活动</p>
              </template>
              <template v-else-if="activity.isRegistered">
                <div class="registered-badge">
                  <el-icon :size="32" color="#67c23a"><CircleCheck /></el-icon>
                  <span>您已成功报名</span>
                </div>
                <p class="action-hint">请按时参加活动并签到</p>
              </template>
              <template v-else>
                <div class="not-available">
                  <el-icon :size="28" color="#909399"><InfoFilled /></el-icon>
                  <span>{{ statusHint }}</span>
                </div>
              </template>
            </el-card>

            <!-- 志愿时长 -->
            <el-card class="sidebar-card hours-card" shadow="never">
              <div class="hours-display">
                <div class="hours-number">{{ activity.volunteerHours }}</div>
                <div class="hours-unit">志愿时长（小时）</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>

      <el-empty v-else-if="!loading" description="活动不存在或已被删除" />
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout.vue'
import { getActivityDetail, registerActivity } from '@/api/activity'
import { getRecruitmentDisplay } from '@/utils/recruitment'
import { getActivityPhaseDisplay } from '@/utils/activityPhase'
import dayjs from 'dayjs'

const route = useRoute()
const loading = ref(false)
const activity = ref({})

const activityPhaseDisplay = computed(() => getActivityPhaseDisplay(activity.value))
const recruitmentDisplay   = computed(() => getRecruitmentDisplay(activity.value))

const canRegister = computed(() => {
  const a = activity.value
  return !a.isRegistered && a.status === 'RECRUITING' && recruitmentDisplay.value.text === '招募中'
})

const statusHint = computed(() => {
  const a = activity.value
  if (a.status === 'CANCELLED') return '活动已取消'
  if (a.status === 'COMPLETED') return '活动已结项'
  if (recruitmentDisplay.value.text === '未开始') return '招募尚未开始'
  if (recruitmentDisplay.value.text === '已结束') return '招募已结束'
  return '活动暂不可报名'
})

const slotsProgressColor = computed(() => {
  const pct = (activity.value.currentParticipants / activity.value.maxParticipants) * 100
  if (pct >= 90) return '#f56c6c'
  if (pct >= 60) return '#e6a23c'
  return '#67c23a'
})

const formatDate = (date) => {
  if (!date) return '—'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const fetchActivity = async () => {
  loading.value = true
  try {
    const res = await getActivityDetail(route.params.id)
    activity.value = res.data || {}
  } catch (error) {
    console.error('获取活动详情失败:', error)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  try {
    await registerActivity(route.params.id)
    ElMessage.success('报名成功！')
    fetchActivity()
  } catch (error) {
    console.error('报名失败:', error)
  }
}

onMounted(() => { fetchActivity() })
</script>

<style scoped>
.activity-detail-container {
  max-width: 1100px;
  margin: 0 auto;
}

/* Hero */
.activity-hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 40px 44px;
  margin-bottom: 20px;
  color: white;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.hero-tag {
  border: 1px solid rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.18);
  color: white;
  font-weight: 500;
}

.category-tag {
  background: rgba(255, 255, 255, 0.25) !important;
}

.hero-title {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 20px 0;
  line-height: 1.4;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  opacity: 0.9;
}

/* Body */
.detail-body { margin-top: 0; }

.detail-card, .sidebar-card {
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
  font-size: 15px;
}

.card-icon {
  color: #667eea;
  font-size: 16px;
}

.description-content {
  line-height: 1.9;
  color: #555;
  white-space: pre-wrap;
  font-size: 14px;
}

/* 时间网格 */
.time-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.time-block {
  padding: 14px 16px;
  background: #f8f9fc;
  border-radius: 10px;
  border-left: 3px solid #e4e7ed;
}

.time-block--deadline {
  border-left-color: #f56c6c;
  background: #fff5f5;
}

.time-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.time-value {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

/* 名额 */
.slots-info { padding: 4px 0; }

.slots-numbers {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.slots-current {
  font-size: 36px;
  font-weight: 700;
  color: #667eea;
  line-height: 1;
}

.slots-sep, .slots-max {
  font-size: 20px;
  color: #909399;
}

.slots-unit {
  font-size: 14px;
  color: #909399;
  margin-left: 2px;
}

.slots-hint {
  margin-top: 10px;
  font-size: 13px;
  color: #909399;
}

/* 报名操作 */
.action-card { text-align: center; padding: 8px; }

.register-btn {
  width: 100%;
  height: 46px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.register-btn:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.action-hint {
  font-size: 12px;
  color: #909399;
  margin: 10px 0 0;
}

.registered-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  color: #67c23a;
  font-size: 16px;
  font-weight: 600;
}

.not-available {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  color: #909399;
  font-size: 14px;
}

/* 时长卡片 */
.hours-card {
  background: linear-gradient(135deg, #667eea10, #764ba215);
  border-color: #667eea30 !important;
}

.hours-display {
  text-align: center;
  padding: 8px 0;
}

.hours-number {
  font-size: 48px;
  font-weight: 800;
  color: #667eea;
  line-height: 1;
}

.hours-unit {
  font-size: 13px;
  color: #909399;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .activity-hero { padding: 28px 20px; }
  .hero-title { font-size: 20px; }
  .time-grid { grid-template-columns: 1fr; }
}
</style>
