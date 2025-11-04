/**
 * 动态路由处理
 * 根据接口返回的菜单列表注册动态路由
 */
import type { Router, RouteRecordRaw } from 'vue-router'
import type { AppRouteRecord } from '@/types/router'
import { saveIframeRoutes } from './menuToRouter'
import { h } from 'vue'
// 注意：不再将移除函数存入 Pinia，改为模块级内存存储
import { RoutesAlias } from '../routesAlias'

/**
 * 动态导入 views 目录下所有 .vue 组件
 */
const modules: Record<string, () => Promise<any>> = import.meta.glob('../../views/**/*.vue')

/**
 * 注册异步路由
 * 将接口返回的菜单列表转换为 Vue Router 路由配置，并添加到传入的 router 实例中
 * @param router Vue Router 实例
 * @param menuList 接口返回的菜单列表
 */
export function registerDynamicRoutes(router: Router, menuList: AppRouteRecord[]): void {
  // 记录运行时菜单列表（仅内存，不入 store）
  runtimeMenuList = menuList
  // 用于局部收集 iframe 类型路由
  const iframeRoutes: AppRouteRecord[] = []
  // 收集路由移除函数
  const removeRouteFns: (() => void)[] = []

  // 检测菜单列表中是否有重复路由
  checkDuplicateRoutes(menuList)

  // 遍历菜单列表，注册路由
  menuList.forEach((route) => {
    // 只有还没注册过的路由才进行注册
    if (route.name && !router.hasRoute(route.name)) {
      const routeConfig = convertRouteComponent(route, iframeRoutes)
      // 外链（非 iframe）或无组件且无子路由的项不需要注册到 Router，避免非法 path 导致错误
      const hasComponent = !!(routeConfig as RouteRecordRaw).component
      const hasChildren = Array.isArray((routeConfig as RouteRecordRaw).children) &&
        ((routeConfig as RouteRecordRaw).children as RouteRecordRaw[]).length > 0
      const isExternalLink = !!route.meta?.link && !route.meta?.isIframe
      if (!hasComponent && !hasChildren) {
        console.warn(`[路由跳过] ${String(route.name)} 无组件且无子路由，不注册到 Router。`)
        return
      }
      if (isExternalLink) {
        console.info(`[外链菜单] ${String(route.name)} 为非 iframe 外链，跳过 Router 注册。`)
        return
      }
      // addRoute 返回移除函数，收集起来
      const removeRouteFn = router.addRoute(routeConfig as RouteRecordRaw)
      removeRouteFns.push(removeRouteFn)
    }
  })

  // 将移除函数存入模块级内存，避免在前端存储路由数据到 store
  registeredRouteRemoveFns.push(...removeRouteFns)

  // 保存 iframe 路由
  saveIframeRoutes(iframeRoutes)
}

/**
 * 模块级：已注册动态路由的移除函数集合
 * 仅驻留于内存，不写入任何 store
 */
const registeredRouteRemoveFns: (() => void)[] = []
let runtimeMenuList: AppRouteRecord[] = []

/** 获取运行时菜单列表（仅内存） */
export function getRuntimeMenuList(): AppRouteRecord[] {
  return runtimeMenuList
}

/**
 * 移除所有已注册的动态路由（内存级）
 */
export function removeAllDynamicRoutes(): void {
  registeredRouteRemoveFns.forEach((fn) => {
    try {
      fn()
    } catch (e) {
      console.warn('移除动态路由失败', e)
    }
  })
  registeredRouteRemoveFns.length = 0
}

/**
 * 路径解析函数：处理父路径和子路径的拼接
 */
function resolvePath(parent: string, child: string): string {
  return [parent.replace(/\/$/, ''), child.replace(/^\//, '')].filter(Boolean).join('/')
}

/**
 * 检测菜单中的重复路由（包括子路由）
 */
function checkDuplicateRoutes(routes: AppRouteRecord[], parentPath = ''): void {
  // 用于检测动态路由中的重复项
  const routeNameMap = new Map<string, string>() // 路由名称 -> 路径
  const componentPathMap = new Map<string, string>() // 组件路径 -> 路由信息

  const checkRoutes = (routes: AppRouteRecord[], parentPath = '') => {
    routes.forEach((route) => {
      // 处理路径拼接
      const currentPath = route.path || ''
      const fullPath = resolvePath(parentPath, currentPath)

      // 名称重复检测
      if (route.name) {
        if (routeNameMap.has(String(route.name))) {
          console.warn(`[路由警告] 名称重复: "${String(route.name)}"`)
        } else {
          routeNameMap.set(String(route.name), fullPath)
        }
      }

      // 组件路径重复检测
      if (route.component) {
        const componentPath = getComponentPathString(route.component)

        if (componentPath && componentPath !== RoutesAlias.Layout) {
          const componentKey = `${parentPath}:${componentPath}`

          if (componentPathMap.has(componentKey)) {
            console.warn(`[路由警告] 路径重复: "${componentPath}"`)
          } else {
            componentPathMap.set(componentKey, fullPath)
          }
        }
      }

      // 递归处理子路由
      if (route.children?.length) {
        checkRoutes(route.children, fullPath)
      }
    })
  }

  checkRoutes(routes, parentPath)
}

/**
 * 获取组件路径的字符串表示
 */
function getComponentPathString(component: any): string {
  if (typeof component === 'string') {
    return component
  }

  return ''
}

/**
 * 根据组件路径动态加载组件
 * @param componentPath 组件路径（不包含 ../../views 前缀和 .vue 后缀）
 * @param routeName 当前路由名称（用于错误提示）
 * @returns 组件加载函数
 */
function loadComponent(componentPath: string, routeName: string): () => Promise<any> {
  // 如果路径为空，直接返回一个空的组件
  if (componentPath === '') {
    return () =>
      Promise.resolve({
        render() {
          return h('div', {})
        }
      })
  }

  // 构建可能的路径
  const fullPath = `../../views${componentPath}.vue`
  const fullPathWithIndex = `../../views${componentPath}/index.vue`

  // 先尝试直接路径，再尝试添加/index的路径
  const module = modules[fullPath] || modules[fullPathWithIndex]

  if (!module) {
    console.error(
      `[路由错误] 未找到组件：${routeName}，尝试过的路径: ${fullPath} 和 ${fullPathWithIndex}`
    )
    return () =>
      Promise.resolve({
        render() {
          return h('div', `组件未找到: ${routeName}`)
        }
      })
  }

  return module
}

/**
 * 转换后的路由配置类型
 */
interface ConvertedRoute extends Omit<RouteRecordRaw, 'children'> {
  id?: number
  children?: ConvertedRoute[]
  component?: RouteRecordRaw['component'] | (() => Promise<any>)
}

/**
 * 转换路由组件配置
 */
function convertRouteComponent(
  route: AppRouteRecord,
  iframeRoutes: AppRouteRecord[],
  depth = 0
): ConvertedRoute {
  const { component, children, ...routeConfig } = route

  // 基础路由配置
  const converted: ConvertedRoute = {
    ...routeConfig,
    component: undefined
  }

  // 是否为一级菜单
  const isFirstLevel =
    depth === 0 && route.children?.length === 0 && component !== RoutesAlias.Layout

  if (route.meta.isIframe) {
    handleIframeRoute(converted, route, iframeRoutes, depth)
  } else if (isFirstLevel) {
    handleLayoutRoute(converted, route, component as string)
  } else {
    handleNormalRoute(converted, component as string, String(route.name))
  }

  // 递归时增加深度
  if (children?.length) {
    converted.children = children.map((child) =>
      convertRouteComponent(child, iframeRoutes, depth + 1)
    )
  }

  return converted
}
/**
 * 处理 iframe 类型路由
 */
function handleIframeRoute(
  targetRoute: ConvertedRoute,
  sourceRoute: AppRouteRecord,
  iframeRoutes: AppRouteRecord[],
  depth: number
): void {
  const LAYOUT_VIEW = () => import('@/views/index/index.vue')
  const IFRAME_VIEW = () => import('@/views/outside/Iframe.vue')

  if (depth === 0) {
    // 顶级 iframe：用 Layout 包裹
    targetRoute.component = LAYOUT_VIEW
    targetRoute.path = `/${(sourceRoute.path?.split('/')[1] || '').trim()}`
    targetRoute.name = ''
    targetRoute.meta = sourceRoute.meta // 保留顶级菜单的 meta

    targetRoute.children = [
      {
        ...sourceRoute,
        component: IFRAME_VIEW
      } as ConvertedRoute
    ]
  } else {
    // 非顶级（嵌套）iframe：直接使用 Iframe.vue
    targetRoute.component = IFRAME_VIEW
  }

  // 记录 iframe 路由，供 Iframe.vue 查找对应的外链
  iframeRoutes.push(sourceRoute)
}

/**
 * 处理一级菜单路由
 */
function handleLayoutRoute(
  converted: ConvertedRoute,
  route: AppRouteRecord,
  component: string | undefined
): void {
  converted.component = () => import('@/views/index/index.vue')
  converted.path = `/${(route.path?.split('/')[1] || '').trim()}`
  converted.name = ''
  converted.meta = route.meta // 保留顶级菜单的 meta
  route.meta.isFirstLevel = true

  converted.children = [
    {
      ...route,
      component: loadComponent(component as string, String(route.name))
    } as ConvertedRoute
  ]
}

/**
 * 处理普通路由
 */
function handleNormalRoute(
  converted: ConvertedRoute,
  component: string | undefined,
  routeName: string
): void {
  if (component) {
    // 直接使用组件路径加载组件
    converted.component = loadComponent(component as string, routeName)
  }
}
