import { AppRouteRecord } from '@/types/router'

export const dashboardRoutes: AppRouteRecord = {
  path: '/dashboard',
  name: 'Dashboard',
  component: '/index/index',
  meta: {
    title: 'menus.dashboard.title',
    icon: '&#xe7fe;'
  },
  children: [
    {
      path: 'console',
      name: 'Console',
      component: '/dashboard/console',
      meta: {
        title: 'menus.dashboard.console',
        keepAlive: true
      }
    },
    {
      path: 'analysis',
      name: 'Analysis',
      component: '/dashboard/analysis',
      meta: {
        title: 'menus.dashboard.analysis',
        keepAlive: true
      }
    },
    {
      path: 'ecommerce',
      name: 'Ecommerce',
      component: '/dashboard/ecommerce',
      meta: {
        title: 'menus.dashboard.ecommerce',
        keepAlive: true
      }
    }
  ]
}

