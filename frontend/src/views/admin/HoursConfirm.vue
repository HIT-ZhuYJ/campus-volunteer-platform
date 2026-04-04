<template>
  <div class="verify-page page-container">
    <el-card>
      <template #header>
        <div class="head">
          <h3>时长核销管理</h3>
          <p>仅可选择“已结束但未结项”的活动，且只核销已签到且未核销的记录。</p>
        </div>
      </template>

      <div class="toolbar">
        <span class="label">选择活动</span>
        <el-select
          v-model="selectedActivityId"
          filterable
          clearable
          placeholder="请选择已结束且未结项活动"
          style="width: min(520px, 100%)"
          :loading="endedLoading"
          @change="onActivityChange"
        >
          <el-option
            v-for="a in endedActivities"
            :key="a.id"
            :label="activityOptionLabel(a)"
            :value="a.id"
          />
        </el-select>
      </div>

      <template v-if="selectedActivityId">
        <div v-if="selectedActivity" class="current-activity">
          <span>当前活动：{{ selectedActivity.title }}</span>
          <span class="meta">结束时间 {{ formatDate(selectedActivity.endTime) }}</span>
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
            <el-table-column label="志愿时长" width="100">
              <template #default="{ row }">{{ row.volunteerHours }} 小时</template>
            </el-table-column>
            <el-table-column label="签到状态" width="110">
              <template #default="{ row }">
                <el-tag :type="row.checkInStatus === 1 ? 'success' : 'info'">{{ row.checkInStatus === 1 ? '已签到' : '未签到' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="核销状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.hoursConfirmed === 1 ? 'success' : 'warning'">{{ row.hoursConfirmed === 1 ? '已核销' : '待核销' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="核销时间" width="160">
              <template #default="{ row }">{{ row.confirmTime ? formatDate(row.confirmTime) : '--' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.hoursConfirmed === 0 && row.checkInStatus === 1"
                  type="primary"
                  size="small"
                  @click="handleConfirm(row)"
                >
                  核销
                </el-button>
                <el-tag v-else-if="row.hoursConfirmed === 1" type="success" size="small">已核销</el-tag>
                <span v-else class="tip-muted">需先签到</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>

      <el-empty v-else-if="!endedLoading && endedActivities.length === 0" description="暂无待核销活动" />
      <el-empty v-else-if="!endedLoading" description="请先在上方选择一个活动" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { confirmHours, getActivityRegistrations, getEndedActivitiesForAdmin } from '@/api/activity'
import dayjs from 'dayjs'

const endedLoading = ref(false)
const endedActivities = ref([])
const selectedActivityId = ref(null)
const loading = ref(false)
const registrations = ref([])

const selectedActivity = computed(() => endedActivities.value.find((a) => a.id === selectedActivityId.value))

const formatDate = (date) => {
  if (!date) return '--'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const activityOptionLabel = (a) => `${a.title}（${formatDate(a.endTime)} 结束）`

const fetchEndedActivities = async () => {
  endedLoading.value = true
  try {
    const res = await getEndedActivitiesForAdmin()
    endedActivities.value = res.data || []
  } catch (e) {
    console.error(e)
    endedActivities.value = []
  } finally {
    endedLoading.value = false
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

const handleConfirm = (row) => {
  ElMessageBox.confirm(
    `确定核销「${row.realName || '该志愿者'}」的 ${row.volunteerHours} 小时时长吗？`,
    '确认核销',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await confirmHours(row.id)
      ElMessage.success('核销成功')
      fetchRegistrations()
      fetchEndedActivities()
    } catch (error) {
      console.error('核销失败:', error)
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchEndedActivities()
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

.current-activity .meta {
  color: #80869d;
  font-size: 13px;
}

.tip-muted {
  font-size: 12px;
  color: #8990a8;
}

.reg-table {
  min-width: 930px;
}
</style>
