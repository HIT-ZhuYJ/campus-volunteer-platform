<template>
  <div class="verify-page page-container">
    <el-card>
      <template #header>
        <div class="head">
          <h3>活动签到</h3>
          <p>仅展示“已开始且未结束”的活动，未开始或已结束活动不在此页出现。</p>
        </div>
      </template>

      <div class="toolbar">
        <span class="label">选择活动</span>
        <el-select
          v-model="selectedActivityId"
          filterable
          clearable
          placeholder="请选择进行中的活动"
          style="width: min(520px, 100%)"
          :loading="activitiesLoading"
          @change="onActivityChange"
        >
          <el-option
            v-for="a in checkInActivities"
            :key="a.id"
            :label="activityOptionLabel(a)"
            :value="a.id"
          />
        </el-select>
      </div>

      <template v-if="selectedActivityId">
        <div v-if="selectedActivity" class="current-activity">
          <span>当前活动：{{ selectedActivity.title }}</span>
          <span class="meta">{{ formatDate(selectedActivity.startTime) }} ~ {{ formatDate(selectedActivity.endTime) }}</span>
        </div>

        <div class="table-scroll">
          <el-table :data="registrations" v-loading="loading" stripe class="reg-table" empty-text="暂无报名记录">
            <el-table-column label="姓名" width="120">
              <template #default="{ row }">{{ row.realName || '-' }}</template>
            </el-table-column>
            <el-table-column label="学号" width="120">
              <template #default="{ row }">{{ row.studentNo || '-' }}</template>
            </el-table-column>
            <el-table-column label="手机" prop="phone" width="130" />
            <el-table-column label="报名时间" width="170">
              <template #default="{ row }">{{ formatDate(row.registrationTime) }}</template>
            </el-table-column>
            <el-table-column label="签到状态" width="110">
              <template #default="{ row }">
                <el-tag :type="row.checkInStatus === 1 ? 'success' : 'info'">{{ row.checkInStatus === 1 ? '已签到' : '未签到' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="签到时间" width="170">
              <template #default="{ row }">{{ row.checkInTime ? formatDate(row.checkInTime) : '--' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.checkInStatus !== 1" type="primary" size="small" @click="handleCheckIn(row)">
                  标记签到
                </el-button>
                <el-tag v-else type="success" size="small">已签到</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>

      <el-empty v-else-if="!activitiesLoading && checkInActivities.length === 0" description="当前没有可签到活动" />
      <el-empty v-else-if="!activitiesLoading" description="请先在上方选择一个活动" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityRegistrations, getCheckInActivitiesForAdmin, adminCheckInRegistration } from '@/api/activity'
import dayjs from 'dayjs'

const activitiesLoading = ref(false)
const checkInActivities = ref([])
const selectedActivityId = ref(null)
const loading = ref(false)
const registrations = ref([])

const selectedActivity = computed(() => checkInActivities.value.find((a) => a.id === selectedActivityId.value))

const formatDate = (date) => {
  if (!date) return '--'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const activityOptionLabel = (a) => {
  const start = formatDate(a.startTime)
  const end = formatDate(a.endTime)
  return `${a.title}（${start} ~ ${end}）`
}

const fetchCheckInActivities = async () => {
  activitiesLoading.value = true
  try {
    const res = await getCheckInActivitiesForAdmin()
    checkInActivities.value = res.data || []
  } catch (e) {
    console.error(e)
    checkInActivities.value = []
  } finally {
    activitiesLoading.value = false
  }
}

const onActivityChange = () => {
  registrations.value = []
  if (!selectedActivityId.value) return
  fetchRegistrations()
}

const fetchRegistrations = async () => {
  if (!selectedActivityId.value) return
  loading.value = true
  try {
    const res = await getActivityRegistrations(selectedActivityId.value)
    registrations.value = res.data || []
  } catch (e) {
    console.error(e)
    registrations.value = []
  } finally {
    loading.value = false
  }
}

const handleCheckIn = (row) => {
  const name = row.realName || '该志愿者'
  ElMessageBox.confirm(`确定为「${name}」标记签到吗？`, '活动签到', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  })
    .then(async () => {
      try {
        await adminCheckInRegistration(row.id)
        ElMessage.success('签到成功')
        fetchRegistrations()
        fetchCheckInActivities()
      } catch (e) {
        console.error(e)
      }
    })
    .catch(() => {})
}

onMounted(() => {
  fetchCheckInActivities()
})
</script>

<style scoped>
.verify-page {
  width: 100%;
}

.head h3 {
  font-size: 22px;
}

.head p {
  margin-top: 6px;
  color: #737990;
  font-size: 13px;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.label {
  font-size: 14px;
  font-weight: 600;
  color: #4b5168;
}

.current-activity {
  margin-bottom: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.meta {
  color: #80869d;
  font-size: 13px;
}

.reg-table {
  min-width: 940px;
}
</style>
