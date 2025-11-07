import { AppRouteRecord } from '@/types/router'
import { dashboardRoutes } from './dashboard'
import { systemRoutes } from './system'
import { safeguardRoutes } from './safeguard'
import { helpRoutes } from './help'

/**
 * 导出所有模块化路由
 */
export const routeModules: AppRouteRecord[] = [
  dashboardRoutes,
  systemRoutes,
  safeguardRoutes,
  ...helpRoutes
]
