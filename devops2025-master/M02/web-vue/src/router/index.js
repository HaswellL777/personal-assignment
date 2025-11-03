import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
const Apps = () => import('@/views/AppsView.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/apps',
      name: 'apps',
      component: Apps,
    },
  ],
})

export default router
