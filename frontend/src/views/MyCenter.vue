<template>
  <Layout>
    <div class="my-page page-container">
      <section class="my-hero">
        <div>
          <p class="mini">My Records</p>
          <h1>我的志愿足迹</h1>
          <p>按活动时间倒序展示，清晰查看签到与时长核销状态。</p>
        </div>
        <div class="count-chip">共 {{ registrations.length }} 条</div>
      </section>

      <el-card class="table-card" shadow="never">
        <div class="table-scroll">
          <el-table :data="registrations" v-loading="loading" empty-text="暂无报名记录" stripe>
            <el-table-column label="活动名称" prop="activityTitle" min-width="200" show-overflow-tooltip />
            <el-table-column label="时长" width="90" align="center">
              <template #default="{ row }">
                <span class="hours-pill">{{ row.volunteerHours }}h</span>
              </template>
            </el-table-column>
            <el-table-column label="活动时间" width="140">
              <template #default="{ row }">{{ formatDate(row.startTime) }}</template>
            </el-table-column>
            <el-table-column label="报名时间" width="140">
              <template #default="{ row }">{{ formatDate(row.registrationTime) }}</template>
            </el-table-column>
            <el-table-column label="签到" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.checkInStatus === 1 ? 'success' : 'info'" effect="light">
                  {{ row.checkInStatus === 1 ? '已签到' : '未签到' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="时长核销" width="110" align="center">
              <template #default="{ row }">
                <el-tag :type="row.hoursConfirmed === 1 ? 'success' : 'warning'" effect="light">
                  {{ row.hoursConfirmed === 1 ? '已核销' : '待核销' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="96" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 'REGISTERED' ? 'primary' : 'info'" effect="plain">
                  {{ row.status === 'REGISTERED' ? '已报名' : '已取消' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-empty v-if="!loading && registrations.length === 0" description="还没有报名记录，快去参加志愿活动吧" />
      </el-card>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Layout from '@/components/Layout.vue'
import { getMyRegistrations } from '@/api/activity'
import dayjs from 'dayjs'

const loading = ref(false)
const registrations = ref([])

const formatDate = (date) => {
  if (!date) return '--'
  return dayjs(date).format('MM-DD HH:mm')
}

const fetchData = async () => {
  loading.value = true
  try {
    const regRes = await getMyRegistrations()
    const data = regRes.data || []
    registrations.value = data.sort((a, b) => {
      const timeA = new Date(a.startTime).getTime()
      const timeB = new Date(b.startTime).getTime()
      return timeB - timeA
    })
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.my-page {
  width: 100%;
  margin: 0 auto;
}

.my-hero {
  margin-bottom: 16px;
  border-radius: 24px;
  padding: 28px 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
  color: #fff;
  background: linear-gradient(135deg, var(--cv-primary), var(--cv-primary-weak));
}

.mini {
  font-size: 12px;
  letter-spacing: 1.4px;
  text-transform: uppercase;
  opacity: 0.9;
}

.my-hero h1 {
  font-size: clamp(26px, 4vw, 34px);
  margin: 8px 0;
  color: #fff;
}

.my-hero p {
  margin: 0;
  opacity: 0.92;
}

.count-chip {
  border-radius: 999px;
  padding: 8px 14px;
  background: rgba(255, 255, 255, 0.2);
  font-weight: 600;
  white-space: nowrap;
}

.table-card {
  width: 100%;
  border-radius: 18px;
}

.table-scroll {
  width: 100%;
}

.table-scroll :deep(.el-table) {
  width: 100%;
}

.hours-pill {
  border-radius: 999px;
  padding: 2px 8px;
  background: rgba(13, 71, 217, 0.12);
  color: var(--cv-primary);
  font-weight: 700;
}

@media (max-width: 768px) {
  .my-hero {
    padding: 22px 16px;
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
