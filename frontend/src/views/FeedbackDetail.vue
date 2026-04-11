<template>
  <Layout>
    <div class="feedback-detail page-container">
      <section class="detail-head">
        <div>
          <el-button text @click="router.push('/my-feedback')">返回我的反馈</el-button>
          <h1>{{ detail.title || '反馈详情' }}</h1>
          <div class="meta-row">
            <el-tag>{{ categoryText(detail.category) }}</el-tag>
            <el-tag :type="statusDisplay(detail.status).type">{{ statusDisplay(detail.status).text }}</el-tag>
            <el-tag type="info">{{ priorityText(detail.priority) }}</el-tag>
            <span>{{ formatDate(detail.createTime) }}</span>
          </div>
        </div>
        <el-button
          v-if="canOperate"
          type="success"
          :loading="closing"
          @click="handleClose"
        >
          确认关闭
        </el-button>
      </section>

      <div class="detail-grid">
        <main class="timeline">
          <div v-loading="loading" class="messages">
            <article
              v-for="message in detail.messages || []"
              :key="message.id"
              :class="['message', message.senderRole === 'ADMIN' && 'admin', message.senderRole === 'SYSTEM' && 'system']"
            >
              <div class="bubble">
                <div class="message-top">
                  <strong>{{ roleText(message.senderRole) }}</strong>
                  <span>{{ formatDate(message.createTime) }}</span>
                </div>
                <p v-if="message.content">{{ message.content }}</p>
                <FeedbackAttachmentList :attachments="message.attachments || []" />
              </div>
            </article>
          </div>

          <el-card v-if="canOperate" class="reply-card" shadow="never">
            <h3>继续补充</h3>
            <el-input
              v-model="replyForm.content"
              type="textarea"
              :rows="4"
              maxlength="5000"
              show-word-limit
              placeholder="补充复现步骤、截图说明或继续追问。"
            />
            <FeedbackAttachmentUploader
              :attachments="replyForm.attachments"
              @update:attachments="value => { replyForm.attachments = value }"
            />
            <div class="reply-actions">
              <el-button type="primary" :loading="replying" @click="submitReply">发送消息</el-button>
            </div>
          </el-card>

          <el-alert
            v-else
            title="这条反馈已结束，不能继续回复。"
            :closable="false"
            type="info"
            show-icon
          />
        </main>

        <aside class="side">
          <el-card shadow="never">
            <h3>处理信息</h3>
            <dl>
              <dt>状态</dt>
              <dd>{{ statusDisplay(detail.status).text }}</dd>
              <dt>最近回复</dt>
              <dd>{{ roleText(detail.lastReplierRole) }}</dd>
              <dt>最后更新</dt>
              <dd>{{ formatDate(detail.lastMessageTime || detail.updateTime) }}</dd>
              <dt v-if="detail.rejectReason">驳回原因</dt>
              <dd v-if="detail.rejectReason">{{ detail.rejectReason }}</dd>
            </dl>
          </el-card>
        </aside>
      </div>
    </div>
  </Layout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import Layout from '@/components/Layout.vue'
import FeedbackAttachmentList from '@/components/FeedbackAttachmentList.vue'
import FeedbackAttachmentUploader from '@/components/FeedbackAttachmentUploader.vue'
import { closeMyFeedback, getFeedbackDetail, replyMyFeedback } from '@/api/feedback'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const replying = ref(false)
const closing = ref(false)
const detail = ref({})
const replyForm = reactive({ content: '', attachments: [] })

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
const roleMap = {
  VOLUNTEER: '我',
  ADMIN: '管理员',
  SYSTEM: '系统'
}

const canOperate = computed(() => ['OPEN', 'REPLIED'].includes(detail.value.status))
const statusDisplay = (status) => statusMap[status] || { text: status || '--', type: 'info' }
const categoryText = (value) => categoryMap[value] || value || '--'
const priorityText = (value) => priorityMap[value] || value || '--'
const roleText = (value) => roleMap[value] || value || '--'
const formatDate = (date) => date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '--'

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getFeedbackDetail(route.params.id)
    detail.value = res.data || {}
  } catch (error) {
    console.error('获取反馈详情失败:', error)
  } finally {
    loading.value = false
  }
}

const submitReply = async () => {
  if (!replyForm.content.trim() && replyForm.attachments.length === 0) {
    ElMessage.warning('请填写内容或上传附件')
    return
  }
  replying.value = true
  try {
    await replyMyFeedback(route.params.id, {
      content: replyForm.content,
      attachments: replyForm.attachments
    })
    replyForm.content = ''
    replyForm.attachments = []
    ElMessage.success('消息已发送')
    fetchDetail()
  } catch (error) {
    console.error('回复反馈失败:', error)
  } finally {
    replying.value = false
  }
}

const handleClose = () => {
  ElMessageBox.confirm('确认这条反馈已经解决并关闭吗？', '关闭反馈', {
    type: 'warning',
    confirmButtonText: '确认关闭',
    cancelButtonText: '取消'
  }).then(async () => {
    closing.value = true
    try {
      await closeMyFeedback(route.params.id)
      ElMessage.success('反馈已关闭')
      fetchDetail()
    } catch (error) {
      console.error('关闭反馈失败:', error)
    } finally {
      closing.value = false
    }
  }).catch(() => {})
}

onMounted(fetchDetail)
</script>

<style scoped>
.feedback-detail {
  max-width: 1180px;
  margin: 0 auto;
}

.detail-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 16px;
}

.detail-head h1 {
  margin: 8px 0 10px;
  font-size: clamp(24px, 3.6vw, 34px);
  word-break: break-word;
}

.meta-row {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  color: #6d748d;
}

.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 16px;
}

.messages {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 140px;
  margin-bottom: 16px;
}

.message {
  display: flex;
}

.message.admin {
  justify-content: flex-end;
}

.message.system {
  justify-content: center;
}

.bubble {
  width: min(760px, 92%);
  padding: 14px;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 10px 26px rgba(13, 71, 217, 0.08);
}

.admin .bubble {
  background: #eef6ff;
}

.system .bubble {
  width: auto;
  max-width: 760px;
  background: #f2f4f8;
  color: #666f86;
}

.message-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: #69718c;
  font-size: 13px;
}

.bubble p {
  margin: 10px 0 0;
  white-space: pre-wrap;
  word-break: break-word;
  color: #263250;
  line-height: 1.7;
}

.reply-card h3,
.side h3 {
  margin: 0 0 12px;
}

.reply-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
}

.side dl {
  display: grid;
  grid-template-columns: 74px minmax(0, 1fr);
  gap: 10px;
  margin: 0;
}

.side dt {
  color: #7a8298;
}

.side dd {
  margin: 0;
  color: #27304e;
  font-weight: 700;
  word-break: break-word;
}

@media (max-width: 900px) {
  .detail-head {
    align-items: stretch;
    flex-direction: column;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
