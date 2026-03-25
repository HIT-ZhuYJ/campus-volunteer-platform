<template>
  <Layout>
    <div class="activity-list-container">
      <!-- 顶部 Banner -->
      <div class="page-banner">
        <div class="banner-content">
          <h1>志愿活动列表</h1>
          <p>发现适合你的志愿机会，用行动传递温暖</p>
        </div>
      </div>

      <!-- 筛选栏 -->
      <el-card class="filter-card" shadow="never">
        <div class="filter-row">
          <div class="filter-group">
            <span class="filter-label">活动状态</span>
            <div class="filter-pills">
              <span
                v-for="s in statusOptions"
                :key="s.value"
                :class="['pill', filters.status === s.value && 'pill-active']"
                @click="setFilter('status', s.value)"
              >{{ s.label }}</span>
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">招募阶段</span>
            <div class="filter-pills">
              <span
                v-for="p in phaseOptions"
                :key="p.value"
                :class="['pill', filters.recruitmentPhase === p.value && 'pill-active']"
                @click="setFilter('recruitmentPhase', p.value)"
              >{{ p.label }}</span>
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">活动类型</span>
            <div class="filter-pills">
              <span
                v-for="c in categoryOptions"
                :key="c.value"
                :class="['pill', filters.category === c.value && 'pill-active']"
                @click="setFilter('category', c.value)"
              >{{ c.label }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 活动表格 -->
      <el-card class="table-card" shadow="never">
        <el-table
          :data="activities"
          v-loading="loading"
          row-class-name="activity-row"
          empty-text="暂无符合条件的活动"
          @row-click="(row) => goToDetail(row.id)"
          style="cursor: pointer"
        >
          <el-table-column label="活动标题" min-width="220">
            <template #default="{ row }">
              <div class="title-cell">
                <span class="title-text">{{ row.title }}</span>
                <el-tag
                  v-if="row.availableSlots <= 0"
                  type="danger"
                  size="small"
                  class="full-tag"
                >名额已满</el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="类型" width="110">
            <template #default="{ row }">
              <el-tag :color="categoryColor(row.category)" effect="dark" class="category-tag">
                {{ row.category }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="服务地点" prop="location" width="140" show-overflow-tooltip />

          <el-table-column label="时长" width="80" align="center">
            <template #default="{ row }">
              <span class="hours-badge">{{ row.volunteerHours }}h</span>
            </template>
          </el-table-column>

          <el-table-column label="报名进度" width="150">
            <template #default="{ row }">
              <div class="progress-cell">
                <span class="progress-text">{{ row.currentParticipants }}/{{ row.maxParticipants }}</span>
                <el-progress
                  :percentage="Math.min(Math.round((row.currentParticipants / row.maxParticipants) * 100), 100)"
                  :color="progressColor(row.currentParticipants, row.maxParticipants)"
                  :show-text="false"
                  :stroke-width="5"
                  style="margin-top: 4px"
                />
              </div>
            </template>
          </el-table-column>

          <el-table-column label="报名截止" width="130">
            <template #default="{ row }">
              <span class="date-text">{{ formatDate(row.registrationDeadline) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="招募状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getRecruitmentDisplay(row).type" size="small" class="status-tag">
                {{ getRecruitmentDisplay(row).text }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="活动阶段" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="getActivityPhaseDisplay(row).type" size="small" effect="plain">
                {{ getActivityPhaseDisplay(row).text }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="" width="90" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" link @click.stop="goToDetail(row.id)">
                查看详情 →
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-bar">
          <span class="total-tip">共 {{ pagination.total }} 条</span>
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleSearch"
          />
        </div>
      </el-card>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { getActivityList } from '@/api/activity'
import { getRecruitmentDisplay } from '@/utils/recruitment'
import { getActivityPhaseDisplay } from '@/utils/activityPhase'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const activities = ref([])

const statusOptions = [
  { label: '全部', value: '' },
  { label: '招募中', value: 'RECRUITING' },
  { label: '已结项', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]
const phaseOptions = [
  { label: '全部', value: '' },
  { label: '未开始', value: 'NOT_STARTED' },
  { label: '招募中', value: 'RECRUITING' },
  { label: '已结束', value: 'ENDED' }
]
const categoryOptions = [
  { label: '全部', value: '' },
  { label: '学长火炬', value: '学长火炬' },
  { label: '书记驿站', value: '书记驿站' },
  { label: '爱心小屋', value: '爱心小屋' },
  { label: '校友招商', value: '校友招商' },
  { label: '暖冬行动', value: '暖冬行动' }
]

const categoryColorMap = {
  '学长火炬': '#f59e0b',
  '书记驿站': '#3b82f6',
  '爱心小屋': '#ec4899',
  '校友招商': '#8b5cf6',
  '暖冬行动': '#10b981'
}
const categoryColor = (cat) => categoryColorMap[cat] || '#6b7280'

const progressColor = (cur, max) => {
  const pct = (cur / max) * 100
  if (pct >= 90) return '#f56c6c'
  if (pct >= 60) return '#e6a23c'
  return '#67c23a'
}

const filters = reactive({ status: '', category: '', recruitmentPhase: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })

const setFilter = (key, value) => {
  filters[key] = filters[key] === value ? '' : value
  pagination.page = 1
  handleSearch()
}

const handleSizeChange = () => {
  pagination.page = 1
  handleSearch()
}

const formatDate = (date) => dayjs(date).format('MM-DD HH:mm')

const goToDetail = (id) => router.push(`/activity/${id}`)

const handleSearch = async () => {
  loading.value = true
  try {
    const res = await getActivityList({
      page: pagination.page,
      size: pagination.size,
      status: filters.status,
      category: filters.category,
      recruitmentPhase: filters.recruitmentPhase
    })
    activities.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('获取活动列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => { handleSearch() })
</script>

<style scoped>
.activity-list-container {
  max-width: 1280px;
  margin: 0 auto;
}

/* Banner */
.page-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 36px 40px;
  margin-bottom: 20px;
  color: white;
}
.banner-content h1 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
}
.banner-content p {
  font-size: 15px;
  opacity: 0.85;
  margin: 0;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 16px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}
.filter-row {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.filter-label {
  font-size: 13px;
  color: #888;
  white-space: nowrap;
  width: 56px;
  flex-shrink: 0;
}
.filter-pills {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.pill {
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 13px;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.2s;
  color: #606266;
  background: #fff;
  user-select: none;
}
.pill:hover {
  border-color: #667eea;
  color: #667eea;
}
.pill-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
  font-weight: 500;
}

/* 表格卡片 */
.table-card {
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.title-text {
  font-weight: 500;
  color: #303133;
}
.full-tag { flex-shrink: 0; }

.category-tag {
  border-radius: 6px;
  font-weight: 500;
  border: none;
}

.hours-badge {
  display: inline-block;
  background: linear-gradient(135deg, #667eea20, #764ba220);
  color: #667eea;
  font-weight: 600;
  font-size: 13px;
  padding: 2px 8px;
  border-radius: 6px;
}

.progress-cell {
  min-width: 100px;
}
.progress-text {
  font-size: 12px;
  color: #909399;
}

.date-text {
  font-size: 13px;
  color: #606266;
}

.status-tag { font-weight: 500; }

.activity-row:hover td {
  background: #f5f7ff !important;
}

.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  flex-wrap: wrap;
  gap: 12px;
}
.total-tip {
  font-size: 13px;
  color: #909399;
}

@media (max-width: 768px) {
  .page-banner { padding: 24px 20px; }
  .banner-content h1 { font-size: 22px; }
  .filter-group { flex-direction: column; align-items: flex-start; }
  .filter-label { width: auto; }
}
</style>
