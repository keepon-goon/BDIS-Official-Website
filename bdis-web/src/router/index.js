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
    path: '/search',
    name: 'search',
    component: () => import('@/views/SearchView.vue'),
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
      { path: 'news/:id', component: () => import('@/views/NewsDetail.vue'), meta: { tab: 'news', title: '新闻详情' } },
      { path: 'education', component: () => import('@/components/info/Education.vue'), meta: { tab: 'education', title: '学习资料' } },
      { path: 'join', component: () => import('@/components/info/Join.vue'), meta: { tab: 'join', title: '加入我们' } },
      { path: 'project/:id', component: () => import('@/views/ProjectDetail.vue'), meta: { tab: 'research', title: '项目详情' } },
      { path: 'forum/:id', component: () => import('@/views/ForumDetail.vue'), meta: { tab: 'platform', title: '竞赛详情' } },
    ]
  },
  {
    path: '/admin/login',
    name: 'admin-login',
    component: () => import('@/views/admin/LoginView.vue'),
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', component: () => import('@/views/admin/DashboardView.vue') },
      { path: 'banners', component: () => import('@/views/admin/BannerManage.vue') },
      { path: 'news', component: () => import('@/views/admin/NewsManage.vue') },
      { path: 'announcements', component: () => import('@/views/admin/AnnouncementManage.vue') },
      { path: 'projects', component: () => import('@/views/admin/ProjectManage.vue') },
      { path: 'team-members', component: () => import('@/views/admin/TeamMemberManage.vue') },
      { path: 'resources', component: () => import('@/views/admin/ResourceManage.vue') },
      { path: 'forum-posts', component: () => import('@/views/admin/ForumPostManage.vue') },
      { path: 'lab-info', component: () => import('@/views/admin/LabInfoManage.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const token = localStorage.getItem('adminToken')
    if (!token) {
      next({ name: 'admin-login' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
