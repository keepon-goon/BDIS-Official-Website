import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import UnderConstruction from '../components/UnderConstruction.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
  },
  {
    path: '/wip',
    name: 'wip',
    component: UnderConstruction
  },
  {
    path: '/info',
    component: () => import('@/layouts/InfoLayout.vue'),
    redirect: '/wip',
    children: [
      { path: 'about', component: () => import('@/components/info/About.vue'), meta: { tab: 'about', title: '关于我们' } },
      { path: 'research', component: () => import('@/components/info/Research.vue'), meta: { tab: 'research', title: '项目进展' } },
      { path: 'platform', component: () => import('@/components/info/Platform.vue'), meta: { tab: 'platform', title: '创新竞赛' } },
      { path: 'news', component: () => import('@/components/info/News.vue'), meta: { tab: 'news', title: '动态公告' } },
      { path: 'education', component: () => import('@/components/info/Education.vue'), meta: { tab: 'education', title: '学习资料' } },
      { path: 'join', component: () => import('@/components/info/Join.vue'), meta: { tab: 'join', title: '加入我们' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})



export default router