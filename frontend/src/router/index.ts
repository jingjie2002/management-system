import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import { hasAnyRole, readRoleKeys, type RoleKey } from '../utils/access'

type RouteMeta = {
  title?: string
  roles?: RoleKey[]
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/login', component: Login, meta: { title: '登录' } satisfies RouteMeta },
    {
      path: '/dashboard',
      component: Dashboard,
      meta: { title: '首页' } satisfies RouteMeta,
      children: [
        {
          path: '',
          component: () => import('../views/dashboard/index.vue'),
          meta: { title: '仪表盘' } satisfies RouteMeta
        },
        {
          path: '/department',
          component: () => import('../views/department/index.vue'),
          meta: { title: '组织架构', roles: ['admin'] } satisfies RouteMeta
        },
        {
          path: '/employee',
          component: () => import('../views/employee/index.vue'),
          meta: { title: '员工管理', roles: ['admin', 'hr'] } satisfies RouteMeta
        },
        {
          path: '/job-level',
          component: () => import('../views/job-level/index.vue'),
          meta: { title: '职级管理', roles: ['admin', 'hr'] } satisfies RouteMeta
        },
        {
          path: '/employee-change',
          component: () => import('../views/employee-change/index.vue'),
          meta: { title: '异动管理', roles: ['admin', 'hr'] } satisfies RouteMeta
        },
        {
          path: '/my-change',
          component: () => import('../views/employee-change/my.vue'),
          meta: { title: '我的异动', roles: ['employee'] } satisfies RouteMeta
        },
        {
          path: '/attendance',
          component: () => import('../views/attendance/index.vue'),
          meta: { title: '考勤管理' } satisfies RouteMeta
        },
        {
          path: '/leave',
          component: () => import('../views/leave/index.vue'),
          meta: { title: '请假管理' } satisfies RouteMeta
        },
        {
          path: '/notice',
          component: () => import('../views/notice/index.vue'),
          meta: { title: '通知公告' } satisfies RouteMeta
        },
        {
          path: '/performance',
          component: () => import('../views/performance/index.vue'),
          meta: { title: '绩效管理' } satisfies RouteMeta
        },
        {
          path: '/training',
          component: () => import('../views/training/index.vue'),
          meta: { title: '培训管理' } satisfies RouteMeta
        },
        {
          path: '/project',
          component: () => import('../views/project/index.vue'),
          meta: { title: '项目管理', roles: ['admin', 'employee'] } satisfies RouteMeta
        },
        {
          path: '/user',
          component: () => import('../views/user/index.vue'),
          meta: { title: '个人中心' } satisfies RouteMeta
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/login') {
    next('/login')
    return
  }

  const routeRoles = (to.meta.roles as RoleKey[] | undefined) || []
  if (routeRoles.length === 0) {
    next()
    return
  }

  const roleKeys = readRoleKeys()
  if (!hasAnyRole(roleKeys, routeRoles)) {
    next('/dashboard')
    return
  }

  next()
})

export default router
