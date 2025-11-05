import { createRouter, createWebHistory } from 'vue-router'
import Login from '../pages/Login.vue'
import Layout from '../components/Layout.vue'
import Home from '../pages/Home.vue'
import Dashboard from '../pages/Dashboard.vue'
import Apps from '../pages/Apps.vue'
import Profile from '../pages/Profile.vue'

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
      { path: 'profile', name: 'profile', component: Profile },
    ]
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局路由守卫
// 要求：
// 1. 未登录用户仅可访问登录页
// 2. 已登录用户访问登录页时自动跳转首页
// 3. Token 失效时自动跳转登录页
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')

  if (to.path === '/') {
    // 登录页面
    if (token) {
      next('/home')
    } else {
      next()
    }
  } else {
    // 其他页面需要登录
    if (token) {
      next()
    } else {
      next('/')
    }
  }
})

export default router