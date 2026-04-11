<template>
  <Layout>
    <div class="my-feedback page-container">
      <section class="page-head">
        <div>
          <p>Tickets</p>
          <h1>我的反馈</h1>
          <span>查看处理进度，继续补充信息，或在问题解决后确认关闭。</span>
        </div>
        <el-button type="primary" @click="router.push('/feedback')">提交反馈</el-button>
      </section>

      <el-card shadow="never">
        <div class="toolbar">
          <el-select v-model="statusFilter" placeholder="反馈状态" clearable @change="handleFilterChange">
            <el-option label="处理中" value="OPEN" />
            <el-option label="管理员已回复" value="REPLIED" />
            <el-option label="已关闭" value="CLOSED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </div>

        <el-table :data="feedbackList" v-loading="loading" stripe @row-click="row => router.push(`/feedback/${row.id}`)">
          <el-table-column label="标题" prop="title" min-width="220" show-overflow-tooltip />
          <el-table-column label="分类" width="110">
            <template #default="{ row }">{{ categoryText(row.category) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="130">
            <template #default="{ row }">
              <el-tag :type="statusDisplay(row.status).type">{{ statusDisplay(row.status).text }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="优先级" width="100">
            <template #default="{ row }">{{ priorityText(row.priority) }}</template>
          </el-table-column>
          <el-table-column label="最近更新" width="170">
            <template #default="{ row }">{{ formatDate(row.lastMessageTime || row.updateTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click.stop="router.push(`/feedback/${row.id}`)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="!loading && feedbackList.length === 0" description="暂无反馈记录" />

        <div class="pagination-bar">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="sizes, prev, pager, next, jumper"
            background
            @size-change="handleSizeChange"
            @current-change="fetchFeedback"
          />
        </div>
      </el-card>
    </div>
  </Layout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import Layout from '@/components/Layout.vue'
import { getMyFeedback } from '@/api/feedback'

const router = useRouter()
const loading = ref(false)
const feedbackList = ref([])
const statusFilter = ref('')
const pagination = reactive({ page: 1, size: 10, total: 0 })

const statusMap = {
  OPEN: { text: '处理中', type: 'warning' },
  REPLIED: { text: '管理员已回复', type: 'success' },
  CLOSED: { text: '已关闭', type: 'info' },
  REJECTED: { text: '已驳回', type: 'danger' }
}

const categoryMap = {
  QUESTION: '问题咨询',
  SUGGESTION: '建议',
  BUG: '系统问题',
  COMPLAINT: '投诉',
  OTHER: '其他'
}

const priorityMap = {
  LOW: '低',
  NORMAL: '普通',
  HIGH: '高',
  URGENT: '紧急'
}

const statusDisplay = (status) => statusMap[status] || { text: status || '--', type: 'info' }
const categoryText = (value) => categoryMap[value] || value || '--'
const priorityText = (value) => priorityMap[value] || value || '--'
const formatDate = (date) => date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '--'

const fetchFeedback = async () => {
  loading.value = true
  try {
    const res = await getMyFeedback({
      page: pagination.page,
      size: pagination.size,
      status: statusFilter.value
    })
    feedbackList.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('获取我的反馈失败:', error)
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  pagination.page = 1
  fetchFeedback()
}

const handleSizeChange = () => {
  pagination.page = 1
  fetchFeedback()
}

onMounted(fetchFeedback)
</script>

<style scoped>
.my-feedback {
  max-width: 1180px;
  margin: 0 auto;
}

.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.page-head p {
  margin: 0 0 6px;
  color: var(--cv-secondary);
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.page-head h1 {
  font-size: clamp(26px, 4vw, 38px);
  margin-bottom: 6px;
}

.page-head span {
  color: #66708f;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 14px;
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.my-feedback :deep(.el-table__row) {
  cursor: pointer;
}

@media (max-width: 700px) {
  .page-head {
    align-items: stretch;
    flex-direction: column;
  }

  .pagination-bar,
  .toolbar {
    justify-content: center;
  }
}
</style>
