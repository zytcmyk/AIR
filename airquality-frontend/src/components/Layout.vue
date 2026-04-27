<template>
  <div class="layout-container">
    <!-- 大屏首页使用全屏布局 -->
    <template v-if="isHomePage">
      <router-view />
    </template>

    <!-- 其他页面使用苹果风格侧边栏布局 -->
    <template v-else>
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <div class="sidebar-logo">
          <svg class="logo-icon" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5"/>
            <path d="M8 12h8M12 8v8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
          <span class="logo-text">空气质量分析</span>
        </div>

        <nav class="sidebar-nav">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ active: activeMenu === item.path }"
          >
            <component :is="item.icon" class="nav-icon" />
            <span class="nav-label">{{ item.label }}</span>
          </router-link>
        </nav>

        <div class="sidebar-footer">
          <el-dropdown @command="handleCommand" trigger="click" placement="right">
            <div class="user-trigger">
              <el-avatar :size="32" class="user-avatar" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="user-info">
                <span class="user-name">{{ userStore.userInfo?.username }}</span>
                <span class="user-role">{{ userStore.isAdmin ? '管理员' : '用户' }}</span>
              </div>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </aside>

      <!-- 主内容区 -->
      <div class="main-wrapper">
        <!-- 顶栏 -->
        <header class="app-header">
          <div class="header-left">
            <span class="page-title">{{ currentPageTitle }}</span>
          </div>
          <div class="header-right">
            <div class="time-display">{{ currentTime }}</div>
          </div>
        </header>

        <!-- 内容区 -->
        <main class="app-main">
          <router-view />
        </main>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, DataAnalysis, TrendCharts, PieChart, Document, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { logout } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const isHomePage = computed(() => route.path === '/home')

const navItems = [
  { path: '/home', label: '首页', icon: HomeFilled },
  { path: '/aqi', label: 'AQI分析', icon: DataAnalysis },
  { path: '/pollutant', label: '污染物', icon: TrendCharts },
  { path: '/source', label: '污染源', icon: PieChart },
  { path: '/article', label: '资讯', icon: Document }
]

if (userStore.isAdmin) {
  navItems.push({ path: '/admin', label: '管理', icon: Setting })
}

const pageTitles = {
  '/home': '首页',
  '/aqi': 'AQI分析',
  '/pollutant': '污染物分析',
  '/source': '污染源分析',
  '/article': '资讯管理',
  '/admin': '系统管理',
  '/profile': '个人中心'
}

const currentPageTitle = computed(() => pageTitles[route.path] || '空气质量分析平台')

const currentTime = ref('')
let timer = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
}

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
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
/* 苹果风格基础变量 */
.layout-container {
  min-height: 100vh;
  display: flex;
  background: #f5f5f7;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 侧边栏样式 */
.sidebar {
  width: 220px;
  min-width: 220px;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  z-index: 100;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.05);
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 20px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.logo-icon {
  width: 28px;
  height: 28px;
  color: #007aff;
}

.logo-text {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.3px;
}

/* 导航菜单 */
.sidebar-nav {
  flex: 1;
  padding: 12px 12px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  margin-bottom: 2px;
  font-size: 14px;
  color: #1d1d1f;
  border-radius: 8px;
  transition: all 0.15s ease;
  text-decoration: none;
}

.nav-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.nav-item.active {
  background: #007aff;
  color: #ffffff;
}

.nav-item.active .nav-icon {
  color: #ffffff;
}

.nav-icon {
  width: 18px;
  height: 18px;
  color: #86868b;
  transition: color 0.15s ease;
}

.nav-item:hover .nav-icon {
  color: #1d1d1f;
}

.nav-label {
  font-weight: 400;
  letter-spacing: -0.1px;
}

/* 侧边栏底部用户信息 */
.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s ease;
}

.user-trigger:hover {
  background: rgba(0, 0, 0, 0.04);
}

.user-avatar {
  background: #007aff;
  color: #ffffff;
  font-size: 13px;
  font-weight: 500;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 13px;
  font-weight: 500;
  color: #1d1d1f;
}

.user-role {
  font-size: 11px;
  color: #86868b;
}

/* 主内容区域 */
.main-wrapper {
  flex: 1;
  margin-left: 220px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 顶栏样式 */
.app-header {
  height: 52px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.time-display {
  font-size: 13px;
  font-weight: 500;
  color: #86868b;
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.3px;
}

/* 内容区 */
.app-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

/* 响应式适配 */
@media (max-width: 900px) {
  .sidebar {
    width: 72px;
    min-width: 72px;
  }

  .sidebar-logo {
    justify-content: center;
    padding: 20px 0;
  }

  .logo-text {
    display: none;
  }

  .nav-item {
    justify-content: center;
    padding: 10px;
  }

  .nav-label {
    display: none;
  }

  .main-wrapper {
    margin-left: 72px;
  }

  .user-info {
    display: none;
  }

  .user-trigger {
    justify-content: center;
    padding: 8px;
  }

  .sidebar-footer {
    padding: 12px 8px;
  }
}
</style>
