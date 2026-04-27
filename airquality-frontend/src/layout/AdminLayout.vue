<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <div class="sidebar-header">
        <div class="logo">
          <svg viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5"/>
            <path d="M8 12h8M12 8v8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <span class="logo-text" v-if="!isCollapse">空气质量分析</span>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: activeMenu === item.path }"
        >
          <component :is="item.icon" class="nav-icon" />
          <span class="nav-text" v-if="!isCollapse">{{ item.label }}</span>
        </router-link>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <div class="main-area">
      <!-- 顶部栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <button class="collapse-btn" @click="isCollapse = !isCollapse">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M4 6h16M4 12h16M4 18h16"/>
            </svg>
          </button>
          <div class="breadcrumb">
            <span class="breadcrumb-main">后台管理</span>
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-current">{{ currentPageTitle }}</span>
          </div>
        </div>

        <div class="topbar-right">
          <button class="home-btn" @click="goHome">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
            </svg>
            返回前台
          </button>
          <div class="user-info">
            <span class="username">{{ userStore.userInfo?.username }}</span>
            <el-dropdown @command="handleCommand" trigger="click">
              <el-avatar :size="36" class="avatar" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, DataAnalysis, TrendCharts, PieChart, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { logout } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapse = ref(false)

const menuItems = [
  { path: '/admin/user', label: '用户管理', icon: User },
  { path: '/admin/aqi', label: 'AQI管理', icon: DataAnalysis },
  { path: '/admin/pollutant', label: '污染物管理', icon: TrendCharts },
  { path: '/admin/source', label: '污染源管理', icon: PieChart },
  { path: '/admin/article', label: '内容管理', icon: Document }
]

const activeMenu = computed(() => route.path)

const currentPageTitle = computed(() => {
  const titles = {
    '/admin/user': '用户管理',
    '/admin/aqi': 'AQI管理',
    '/admin/pollutant': '污染物管理',
    '/admin/source': '污染源管理',
    '/admin/article': '内容管理'
  }
  return titles[route.path] || '后台管理'
})

const goHome = () => router.push('/home')

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await logout()
      userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (e) {
      if (e !== 'cancel') console.error(e)
    }
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #F2F2F7;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', sans-serif;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar.collapsed {
  width: 72px;
}

.sidebar-header {
  height: 64px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.logo {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo svg {
  width: 28px;
  height: 28px;
  color: #007aff;
}

.logo-text {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  white-space: nowrap;
  letter-spacing: -0.3px;
}

.sidebar-nav {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  font-size: 15px;
  color: #86868b;
  border-radius: 10px;
  text-decoration: none;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-item:hover {
  color: #1d1d1f;
  background: rgba(0, 0, 0, 0.04);
}

.nav-item.active {
  color: #007AFF;
  background: rgba(0, 122, 255, 0.1);
  font-weight: 500;
}

.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.nav-text {
  white-space: nowrap;
}

/* 主区域 */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部栏 */
.topbar {
  height: 64px;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.collapse-btn:hover {
  background: rgba(0, 0, 0, 0.04);
}

.collapse-btn svg {
  width: 20px;
  height: 20px;
  color: #86868b;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb-main {
  color: #86868b;
}

.breadcrumb-sep {
  color: #d1d1d6;
}

.breadcrumb-current {
  color: #1d1d1f;
  font-weight: 500;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.home-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 14px;
  color: #007AFF;
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.home-btn:hover {
  background: rgba(0, 122, 255, 0.1);
}

.home-btn svg {
  width: 16px;
  height: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
}

.avatar {
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 122, 255, 0.25);
}

.avatar :deep(.el-avatar-img) {
  object-fit: cover;
}

/* 内容区 */
.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

/* 响应式 */
@media (max-width: 900px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 100;
  }

  .sidebar.collapsed {
    transform: translateX(-100%);
  }
}
</style>
