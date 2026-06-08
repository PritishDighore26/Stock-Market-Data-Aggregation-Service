import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '@/views/DashboardView.vue'
import ChartView from '@/views/ChartView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: DashboardView
    },
    {
      path: '/chart',
      name: 'chart',
      component: ChartView
    }
  ],
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router
