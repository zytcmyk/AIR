import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'aqi',
        name: 'AqiAnalysis',
        component: () => import('@/views/aqi/AqiAnalysis.vue'),
        meta: { title: 'AQI分析', requiresAuth: true }
      },
      {
        path: 'pollutant',
        name: 'PollutantAnalysis',
        component: () => import('@/views/pollutant/PollutantAnalysis.vue'),
        meta: { title: '污染物分析', requiresAuth: true }
      },
      {
        path: 'source',
        name: 'SourceAnalysis',
        component: () => import('@/views/source/SourceAnalysis.vue'),
        meta: { title: '污染源分析', requiresAuth: true }
      },
      {
        path: 'article',
        name: 'ArticleList',
        component: () => import('@/views/article/ArticleList.vue'),
        meta: { title: '文章列表', requiresAuth: true }
      },
      {
        path: 'article/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/article/ArticleDetail.vue'),
        meta: { title: '文章详情', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  },
  // 后台管理路由
  {
    path: '/admin',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/admin/user',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'aqi',
        name: 'AqiManage',
        component: () => import('@/views/admin/AqiManage.vue'),
        meta: { title: 'AQI管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'pollutant',
        name: 'PollutantManage',
        component: () => import('@/views/admin/PollutantManage.vue'),
        meta: { title: '污染物管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'source',
        name: 'SourceManage',
        component: () => import('@/views/admin/SourceManage.vue'),
        meta: { title: '污染源管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'article',
        name: 'ArticleManage',
        component: () => import('@/views/admin/ArticleManage.vue'),
        meta: { title: '内容管理', requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 空气质量分析平台` : '空气质量分析平台'

  const userStore = useUserStore()
  const token = userStore.token || localStorage.getItem('token')

  // 需要认证的页面
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  // 已登录时访问登录注册页，跳转到首页
  if ((to.path === '/login' || to.path === '/register') && token) {
    next('/home')
    return
  }

  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    ElMessage.error('权限不足，需要管理员权限')
    next('/home')
    return
  }

  next()
})

export default router
