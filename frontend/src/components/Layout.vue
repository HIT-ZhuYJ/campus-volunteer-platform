<template>
  <div class="layout-shell">
    <el-container>
      <el-header class="header glass-nav">
        <div class="header-inner page-container">
          <div class="brand" @click="router.push('/home')">
            <span class="brand-dot">
              <el-icon><Trophy /></el-icon>
            </span>
            <span class="brand-text">Campus Volunteer</span>
          </div>

          <el-menu
            v-if="!isMobile"
            :default-active="activeMenu"
            mode="horizontal"
            :ellipsis="false"
            @select="handleMenuSelect"
            class="desktop-menu"
          >
            <el-menu-item index="/home">首页</el-menu-item>
            <el-menu-item index="/activities">活动</el-menu-item>
            <el-menu-item index="/my-feedback">反馈</el-menu-item>
            <el-menu-item index="/my">我的志愿足迹</el-menu-item>
            <el-menu-item v-if="userStore.isAdmin" index="/admin/activities">管理后台</el-menu-item>
          </el-menu>

          <div class="header-right">
            <el-dropdown v-if="!isMobile" @command="handleCommand">
              <span class="user-pill">
                <el-avatar :size="30" class="avatar">{{ displayName.charAt(0) || 'U' }}</el-avatar>
                <span class="user-name">{{ displayName }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                  <el-dropdown-item command="myRegistrations">我的报名</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <el-button v-else text class="menu-btn" @click="drawer = true">
              <el-icon :size="22"><Menu /></el-icon>
            </el-button>
          </div>
        </div>
      </el-header>

      <el-main class="main">
        <slot />
      </el-main>

      <el-drawer v-model="drawer" direction="rtl" :size="'min(84vw, 320px)'">
        <template #header>
          <div class="drawer-header">
            <el-avatar :size="56" class="avatar">{{ displayName.charAt(0) || 'U' }}</el-avatar>
            <div class="drawer-name">{{ displayName }}</div>
            <div class="drawer-role">{{ userStore.isAdmin ? '管理员' : '志愿者' }}</div>
          </div>
        </template>

        <el-menu :default-active="activeMenu" @select="handleMobileMenuSelect" class="drawer-menu">
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/activities">活动</el-menu-item>
          <el-menu-item index="/my-feedback">反馈</el-menu-item>
          <el-menu-item index="/my">我的志愿足迹</el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin/activities">管理后台</el-menu-item>
          <el-divider />
          <el-menu-item @click="handleCommand('profile')">个人资料</el-menu-item>
          <el-menu-item @click="handleCommand('myRegistrations')">我的报名</el-menu-item>
          <el-menu-item @click="handleCommand('logout')">退出登录</el-menu-item>
        </el-menu>
      </el-drawer>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const drawer = ref(false)
const isMobile = ref(false)

const activeMenu = computed(() => route.path)
const displayName = computed(() => userStore.userInfo.realName || '用户')

const checkMobile = () => {
  isMobile.value = window.innerWidth < 920
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleMobileMenuSelect = (index) => {
  drawer.value = false
  router.push(index)
}

const handleCommand = (command) => {
  drawer.value = false
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'myRegistrations') {
    router.push('/my')
  }
}
</script>

<style scoped>
.layout-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at 0% 0%, rgba(47, 126, 244, 0.1), transparent 38%),
    radial-gradient(circle at 100% 100%, rgba(15, 139, 95, 0.08), transparent 32%),
    var(--cv-bg);
}

.header {
  position: sticky;
  top: 0;
  z-index: 50;
  border-bottom: 1px solid rgba(201, 214, 243, 0.62);
  background: rgba(244, 248, 255, 0.78);
  padding: 0;
}

.header-inner {
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 clamp(12px, 2vw, 22px);
  gap: 14px;
}

.glass-nav {
  backdrop-filter: blur(14px);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
}

.brand-dot {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(135deg, var(--cv-primary), var(--cv-primary-weak));
  box-shadow: 0 8px 18px rgba(13, 71, 217, 0.32);
}

.brand-text {
  font-family: 'Manrope', 'Noto Sans SC', sans-serif;
  font-weight: 800;
  font-size: clamp(16px, 2vw, 20px);
  background: linear-gradient(90deg, var(--cv-primary), var(--cv-primary-weak));
  -webkit-background-clip: text;
  color: transparent;
}

.desktop-menu {
  flex: 1;
  min-width: 0;
  margin: 0 8px;
  border-bottom: none;
  background: transparent;
}

.desktop-menu :deep(.el-menu-item) {
  border-bottom: none;
  color: #3f4968;
  font-weight: 700;
}

.desktop-menu :deep(.el-menu-item.is-active) {
  color: var(--cv-primary);
  background: linear-gradient(to bottom, transparent 78%, rgba(13, 71, 217, 0.14) 78%);
}

.header-right {
  flex-shrink: 0;
}

.user-pill {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 12px;
  border-radius: 999px;
  background: #e9f0ff;
  cursor: pointer;
}

.avatar {
  background: linear-gradient(135deg, var(--cv-primary), var(--cv-primary-weak));
}

.user-name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #27304e;
  font-weight: 700;
}

.menu-btn {
  color: var(--cv-primary);
}

.main {
  min-height: calc(100vh - 60px);
  padding: clamp(14px, 2.5vw, 24px);
}

.drawer-header {
  padding: 12px 0;
  text-align: center;
}

.drawer-name {
  margin-top: 10px;
  font-size: 17px;
  font-weight: 700;
}

.drawer-role {
  font-size: 13px;
  color: #62667a;
  margin-top: 2px;
}

.drawer-menu {
  border-right: none;
}

@media (max-width: 768px) {
  .header-inner {
    min-height: 56px;
    padding: 0 14px;
  }

  .main {
    padding: 14px;
  }
}

@media (max-width: 460px) {
  .brand-text {
    display: none;
  }
}
</style>
