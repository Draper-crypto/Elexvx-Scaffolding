import { AppRouteRecord } from '@/types/router'
import { dashboardRoutes } from './dashboard'
import { systemRoutes } from './system'

/**
 * 导出所有模块化路由
 */
// 仅保留核心模块路由（仪表盘与系统管理），移除所有演示/模板/组件示例等模块
export const routeModules: AppRouteRecord[] = [dashboardRoutes, systemRoutes]
