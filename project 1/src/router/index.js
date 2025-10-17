import { createRouter, createWebHistory } from 'vue-router'
const Apps = () => import('@/views/AppsView.vue')

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/apps' },
    { path: '/apps', name: 'apps', component: Apps },
  ],
})
