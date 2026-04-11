<template>
  <Layout>
    <div class="announcement-detail page-container" v-loading="loading">
      <template v-if="announcement.id">
        <section class="detail-hero">
          <div class="hero-copy">
            <el-tag type="success" effect="light">公告</el-tag>
            <h1>{{ announcement.title }}</h1>
            <p>{{ formatDate(announcement.publishTime || announcement.updateTime) }}</p>
          </div>
          <div class="hero-cover" :class="{ empty: galleryImages.length === 0 }">
            <el-carousel v-if="galleryImages.length > 0" height="240px" indicator-position="outside">
              <el-carousel-item v-for="(imageUrl, index) in galleryImages" :key="`${announcement.id}-${index}`">
                <img :src="imageUrl" :alt="`${announcement.title}-${index + 1}`">
              </el-carousel-item>
            </el-carousel>
            <div v-else class="hero-fallback">
              <span>Campus Bulletin</span>
            </div>
          </div>
        </section>

        <el-row :gutter="16">
          <el-col :xs="24" :md="16">
            <el-card class="panel" shadow="never">
              <template #header><h3>公告内容</h3></template>
              <div class="content">{{ announcement.content }}</div>
            </el-card>
          </el-col>
          <el-col :xs="24" :md="8">
            <el-card class="panel" shadow="never">
              <template #header><h3>关联活动</h3></template>
              <div v-if="linkedActivities.length > 0" class="linked-list">
                <button
                  v-for="activity in linkedActivities"
                  :key="activity.id"
                  type="button"
                  class="linked-item"
                  @click="goToActivity(activity.id)"
                >
                  <span>{{ activity.title || `活动 #${activity.id}` }}</span>
                  <small>{{ formatActivityMeta(activity) }}</small>
                </button>
              </div>
              <p v-else class="muted">暂无关联活动</p>
            </el-card>

            <el-card class="panel" shadow="never">
              <template #header><h3>公告附件</h3></template>
              <div v-if="attachments.length > 0" class="attachment-list">
                <a
                  v-for="attachment in attachments"
                  :key="attachment.attachmentKey"
                  class="attachment-item"
                  :href="attachment.url"
                  target="_blank"
                  rel="noopener"
                >
                  <span>{{ attachment.fileName || '附件' }}</span>
                  <small>{{ formatFileSize(attachment.fileSize) }}</small>
                </a>
              </div>
              <p v-else class="muted">暂无附件</p>
            </el-card>

            <el-card class="panel" shadow="never">
              <template #header><h3>相关操作</h3></template>
              <el-button class="action-btn" @click="router.push('/home')">返回公告首页</el-button>
            </el-card>
          </el-col>
        </el-row>
      </template>

      <el-empty v-else-if="!loading" description="公告不存在或已下线" />
    </div>
  </Layout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import Layout from '@/components/Layout.vue'
import { getAnnouncementDetail } from '@/api/announcement'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const announcement = ref({})

const galleryImages = computed(() => {
  if (Array.isArray(announcement.value.imageUrls) && announcement.value.imageUrls.length > 0) {
    return announcement.value.imageUrls
  }
  return announcement.value.imageUrl ? [announcement.value.imageUrl] : []
})

const linkedActivities = computed(() => {
  if (Array.isArray(announcement.value.activities) && announcement.value.activities.length > 0) {
    return announcement.value.activities
  }
  if (announcement.value.activityId) {
    return [{ id: announcement.value.activityId, title: `活动 #${announcement.value.activityId}` }]
  }
  return []
})

const attachments = computed(() => (
  Array.isArray(announcement.value.attachments) ? announcement.value.attachments : []
))

const formatDate = (date) => {
  if (!date) return '--'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const formatActivityMeta = (activity) => {
  const parts = []
  if (activity.location) parts.push(activity.location)
  if (activity.startTime) parts.push(formatDate(activity.startTime))
  return parts.join(' · ') || '点击查看活动详情'
}

const formatFileSize = (size) => {
  const bytes = Number(size || 0)
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}

const goToActivity = (id) => {
  router.push(`/activity/${id}`)
}

const fetchAnnouncement = async () => {
  loading.value = true
  try {
    const res = await getAnnouncementDetail(route.params.id)
    announcement.value = res.data || {}
  } catch (error) {
    console.error('获取公告详情失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchAnnouncement()
})
</script>

<style scoped>
.announcement-detail {
  width: 100%;
  margin: 0 auto;
}

.detail-hero {
  margin-bottom: 16px;
  border-radius: 24px;
  padding: 30px 26px;
  color: white;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(260px, 380px);
  gap: 22px;
  align-items: center;
  background: linear-gradient(130deg, var(--cv-primary), var(--cv-primary-weak));
}

.hero-copy h1 {
  font-size: clamp(28px, 4.6vw, 36px);
  line-height: 1.25;
  margin: 14px 0 10px;
}

.hero-copy p {
  opacity: 0.9;
}

.hero-cover {
  min-height: 240px;
  border-radius: 20px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.hero-cover img {
  width: 100%;
  height: 100%;
  min-height: 240px;
  object-fit: cover;
  display: block;
}

.hero-cover :deep(.el-carousel),
.hero-cover :deep(.el-carousel__container) {
  height: 240px;
}

.hero-fallback {
  min-height: 240px;
  display: flex;
  align-items: flex-end;
  padding: 22px;
  font-weight: 800;
  font-size: 28px;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.35), transparent 42%),
    linear-gradient(140deg, rgba(255, 255, 255, 0.18), rgba(255, 255, 255, 0.05));
}

.panel {
  margin-bottom: 16px;
}

.panel h3 {
  font-size: 18px;
  font-weight: 700;
}

.content {
  white-space: pre-wrap;
  line-height: 1.9;
  color: #4f556d;
}

.linked-list,
.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.linked-item,
.attachment-item {
  width: 100%;
  border: 1px solid rgba(74, 90, 139, 0.14);
  border-radius: 8px;
  padding: 10px 12px;
  background: #f8fbff;
  color: #2d3654;
  text-align: left;
  text-decoration: none;
  cursor: pointer;
}

.linked-item span,
.attachment-item span {
  display: block;
  font-weight: 700;
  line-height: 1.4;
}

.linked-item small,
.attachment-item small,
.muted {
  display: block;
  margin-top: 4px;
  color: #7b8298;
  font-size: 12px;
}

.action-btn {
  width: 100%;
  margin: 0 0 12px;
  height: 42px;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .detail-hero {
    padding: 22px 16px;
    grid-template-columns: 1fr;
  }
}
</style>
