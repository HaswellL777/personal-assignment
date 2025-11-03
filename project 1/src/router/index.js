import { createRouter, createWebHistory } from 'vue-router'
import Login from '../pages/Login.vue'
import Layout from '../components/Layout.vue'
import Home from '../pages/Home.vue'
import Dashboard from '../pages/Dashboard.vue'
import Apps from '../pages/Apps.vue'
import UserProfile from '../pages/UserProfile.vue'

const routes = [
  { path: '/', name: 'login', component: Login, meta: { requiresAuth: false } },
  {
    path: '/home',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'home', component: Home },
      { path: 'dashboard', name: 'dashboard', component: Dashboard },
      { path: 'apps', name: 'apps', component: Apps },
      { path: 'profile', name: 'profile', component: UserProfile },
    ]
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局路由守卫：只有登录页可无 token 打开，其余页面必须登录
router.beforeEach((to, from, next) => {
  let token = ''
  try { token = localStorage.getItem('auth_token') || '' } catch {}

  // 已登录情况下，访问登录页则直接跳转到首页
  if (to.name === 'login' && token) {
    return next({ name: 'home' })
  }

  // 需要鉴权的路由且无 token，强制跳回登录页
  if (to.matched.some(record => record.meta?.requiresAuth) && !token) {
    return next({ name: 'login' })
  }

  next()
})

export default router