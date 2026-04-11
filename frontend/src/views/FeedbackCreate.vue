<template>
  <Layout>
    <div class="feedback-create page-container">
      <section class="page-head">
        <div>
          <p>Feedback</p>
          <h1>提交反馈</h1>
          <span>问题、建议、Bug 或投诉都会进入同一条工单，方便后续持续沟通。</span>
        </div>
        <el-button @click="router.push('/my-feedback')">我的反馈</el-button>
      </section>

      <el-card shadow="never">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" maxlength="200" show-word-limit placeholder="例如：报名页面无法提交" />
          </el-form-item>
          <el-form-item label="分类" prop="category">
            <el-radio-group v-model="form.category">
              <el-radio-button v-for="item in categoryOptions" :key="item.value" :label="item.value">
                {{ item.label }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="详细说明" prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="7"
              maxlength="5000"
              show-word-limit
              placeholder="请描述出现了什么、你期望看到什么、可复现步骤或补充背景。"
            />
          </el-form-item>
          <el-form-item label="附件">
            <FeedbackAttachmentUploader
              :attachments="form.attachments"
              @update:attachments="value => { form.attachments = value }"
            />
          </el-form-item>
        </el-form>

        <div class="actions">
          <el-button @click="router.back()">返回</el-button>
          <el-button type="primary" :loading="submitting" @click="submitForm">提交反馈</el-button>
        </div>
      </el-card>
    </div>
  </Layout>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout.vue'
import FeedbackAttachmentUploader from '@/components/FeedbackAttachmentUploader.vue'
import { createFeedback } from '@/api/feedback'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)

const categoryOptions = [
  { label: '问题咨询', value: 'QUESTION' },
  { label: '建议', value: 'SUGGESTION' },
  { label: '系统问题', value: 'BUG' },
  { label: '投诉', value: 'COMPLAINT' },
  { label: '其他', value: 'OTHER' }
]

const form = reactive({
  title: '',
  category: 'BUG',
  content: '',
  attachments: []
})

const rules = {
  title: [{ required: true, message: '请输入反馈标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择反馈分类', trigger: 'change' }],
  content: [{
    validator: (rule, value, callback) => {
      if ((value && value.trim()) || form.attachments.length > 0) {
        callback()
      } else {
        callback(new Error('请填写说明或上传附件'))
      }
    },
    trigger: 'blur'
  }]
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const res = await createFeedback({
      title: form.title,
      category: form.category,
      content: form.content,
      attachments: form.attachments
    })
    ElMessage.success('反馈已提交')
    router.push(`/feedback/${res.data.id}`)
  } catch (error) {
    console.error('提交反馈失败:', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.feedback-create {
  max-width: 980px;
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

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
}

@media (max-width: 700px) {
  .page-head {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
