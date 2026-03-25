<template>
  <div>
    <el-card>
      <template #header>
        <h3>志愿时长查询</h3>
        <p class="header-desc">查看所有志愿者的累计时长，支持按姓名、学号或用户名搜索，按时长降序排列。</p>
      </template>

      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索姓名 / 学号 / 用户名"
          clearable
          style="width: min(300px, 100%)"
          @clear="fetchData"
          @keyup.enter="fetchData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="fetchData" :loading="loading">查询</el-button>
      </div>

      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="55" />
        <el-table-column label="姓名" prop="realName" width="110" />
        <el-table-column label="学号" prop="studentNo" width="120" />
        <el-table-column label="用户名" prop="username" width="130" />
        <el-table-column label="手机" prop="phone" width="130" />
        <el-table-column label="邮箱" prop="email" min-width="180" />
        <el-table-column label="累计时长" width="140" sortable :sort-method="sortByHours">
          <template #default="{ row }">
            <span :class="hoursClass(row.totalVolunteerHours)">
              {{ row.totalVolunteerHours ?? 0 }} 小时
            </span>
          </template>
        </el-table-column>
      </el-table>

      <div class="footer-stat" v-if="users.length > 0">
        共 <strong>{{ users.length }}</strong> 名志愿者 ·
        累计总时长 <strong>{{ totalHours }}</strong> 小时 ·
        人均 <strong>{{ avgHours }}</strong> 小时
      </div>

      <el-empty v-if="!loading && users.length === 0" description="未找到匹配的志愿者" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getVolunteerHoursList } from '@/api/user'

const loading = ref(false)
const keyword = ref('')
const users = ref([])

const totalHours = computed(() =>
  users.value.reduce((sum, u) => sum + Number(u.totalVolunteerHours ?? 0), 0).toFixed(2)
)

const avgHours = computed(() => {
  if (users.value.length === 0) return '0.00'
  return (Number(totalHours.value) / users.value.length).toFixed(2)
})

const hoursClass = (hours) => {
  const h = Number(hours ?? 0)
  if (h >= 20) return 'hours-high'
  if (h >= 8) return 'hours-mid'
  return ''
}

const sortByHours = (a, b) =>
  Number(b.totalVolunteerHours ?? 0) - Number(a.totalVolunteerHours ?? 0)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getVolunteerHoursList(keyword.value)
    users.value = res.data || []
  } catch (e) {
    console.error(e)
    users.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.header-desc {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  font-weight: normal;
}

.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.footer-stat {
  margin-top: 14px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.hours-high {
  color: var(--el-color-success);
  font-weight: bold;
}

.hours-mid {
  color: var(--el-color-warning);
  font-weight: 500;
}
</style>
