<template>
  <Layout>
    <div class="home-wrap page-container">
      <section class="hero-card">
        <div>
          <p class="hero-label">Campus Volunteer</p>
          <h1>以志愿行动连接校园影响力</h1>
          <p class="hero-desc">发现适合你的公益活动，沉淀可追踪的服务轨迹。</p>
        </div>
        <el-button type="primary" class="hero-btn" @click="$router.push('/activities')">查看活动</el-button>
      </section>

      <el-row :gutter="16" class="stats-row">
        <el-col :xs="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-title">志愿时长</div>
            <div class="stat-value">{{ userInfo.totalVolunteerHours || 0 }}</div>
            <div class="stat-unit">小时</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-title">参与活动</div>
            <div class="stat-value">{{ registrationCount }}</div>
            <div class="stat-unit">场</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-title">已签到</div>
            <div class="stat-value">{{ completedCount }}</div>
            <div class="stat-unit">次</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-title">已核销</div>
            <div class="stat-value">{{ confirmedCount }}</div>
            <div class="stat-unit">次</div>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="latest-card">
        <template #header>
          <div class="latest-header">
            <h2>最新活动</h2>
            <el-button type="primary" plain @click="$router.push('/activities')">查看更多</el-button>
          </div>
        </template>

        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :lg="8" v-for="activity in activities" :key="activity.id">
            <div class="activity-item" @click="goToDetail(activity.id)">
              <div class="item-tags">
                <el-tag :type="getRecruitmentDisplay(activity).type" effect="light">{{ getRecruitmentDisplay(activity).text }}</el-tag>
                <el-tag type="info" effect="plain">{{ activity.category }}</el-tag>
              </div>
              <h3 class="text-ellipsis-2">{{ activity.title }}</h3>
              <div class="meta-row"><el-icon><Location /></el-icon>{{ activity.location }}</div>
              <div class="meta-row"><el-icon><Clock /></el-icon>{{ activity.volunteerHours }} 小时</div>
              <div class="meta-row"><el-icon><User /></el-icon>{{ activity.currentParticipants }} / {{ activity.maxParticipants }}</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { getActivityList, getMyRegistrations } from '@/api/activity'
import { getRecruitmentDisplay } from '@/utils/recruitment'
import { getUserInfo } from '@/api/user'

const router = useRouter()
const activities = ref([])
const registrations = ref([])
const userInfo = ref({})

const registrationCount = computed(() => registrations.value.length)
const completedCount = computed(() => {
  return registrations.value.filter(r => r.checkInStatus === 1).length
})
const confirmedCount = computed(() => {
  return registrations.value.filter(r => r.hoursConfirmed === 1).length
})

const goToDetail = (id) => {
  router.push(`/activity/${id}`)
}

const fetchData = async () => {
  try {
    const [actRes, regRes, userRes] = await Promise.all([
      getActivityList({ page: 1, size: 6, recruitmentPhase: 'RECRUITING' }),
      getMyRegistrations(),
      getUserInfo()
    ])

    activities.value = actRes.data.records || []
    registrations.value = regRes.data || []
    userInfo.value = userRes.data || {}
  } catch (error) {
    console.error('获取数据失败:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.home-wrap {
  width: 100%;
  margin: 0 auto;
}

.hero-card {
  border-radius: 24px;
  padding: 34px 30px;
  margin-bottom: 18px;
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: flex-end;
  background: linear-gradient(135deg, var(--cv-primary), var(--cv-primary-weak));
  color: #fff;
}

.hero-label {
  opacity: 0.86;
  font-size: 12px;
  letter-spacing: 1.6px;
  text-transform: uppercase;
}

.hero-card h1 {
  font-size: clamp(28px, 4.8vw, 38px);
  line-height: 1.18;
  margin: 8px 0 10px;
}

.hero-desc {
  max-width: 560px;
  opacity: 0.92;
}

.hero-btn {
  border-radius: 999px;
  min-width: 124px;
  background: rgba(255, 255, 255, 0.22);
}

.stats-row {
  margin-bottom: 6px;
}

.stat-card {
  margin-bottom: 14px;
}

.stat-title {
  color: #6c7084;
  font-size: 13px;
}

.stat-value {
  margin-top: 7px;
  font-size: 34px;
  font-weight: 800;
  color: #1f2230;
  line-height: 1;
}

.stat-unit {
  margin-top: 5px;
  color: #8f93a8;
  font-size: 12px;
}

.latest-card {
  margin-top: 4px;
}

.latest-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.latest-header h2 {
  font-size: 26px;
  font-weight: 700;
}

.activity-item {
  border-radius: 16px;
  background: #f3f2ff;
  padding: 18px;
  margin-bottom: 14px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.activity-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 20px rgba(0, 20, 83, 0.1);
}

.item-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.activity-item h3 {
  font-size: 17px;
  line-height: 1.4;
  min-height: 48px;
  margin-bottom: 12px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #5f647a;
  font-size: 14px;
  margin-bottom: 6px;
}

@media (max-width: 768px) {
  .hero-card {
    padding: 24px 18px;
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-card h1 {
    font-size: 26px;
  }

  .hero-btn {
    width: 100%;
  }
}
</style>




