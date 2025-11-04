import type { Router, RouteLocationNormalized, NavigationGuardNext } from 'vue-router'
import { ref, nextTick } from 'vue'
import NProgress from 'nprogress'
import { useSettingStore } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'
import { useMenuStore } from '@/store/modules/menu'
import { setWorktab } from '@/utils/navigation'
import { setPageTitle } from '../utils/utils'
import { fetchGetMenuList, fetchImportMenus } from '@/api/system-manage'
import { registerDynamicRoutes } from '../utils/registerRoutes'
import { AppRouteRecord } from '@/types/router'
import { RoutesAlias } from '../routesAlias'
import { menuDataToRouter } from '../utils/menuToRouter'
import { asyncRoutes } from '../routes/asyncRoutes'
import { staticRoutes } from '../routes/staticRoutes'
import { routeModules } from '@/router/modules'
import { loadingService } from '@/utils/ui'
import { useCommon } from '@/composables/useCommon'
import { useWorktabStore } from '@/store/modules/worktab'
import { removeAllDynamicRoutes } from '../utils/registerRoutes'
import { getFirstMenuPath } from '@/utils'
import { HOME_PAGE_PATH } from '@/router'
import { fetchGetUserInfo } from '@/api/auth'
import { ApiStatus } from '@/utils/http/status'
import { HttpError, isHttpError } from '@/utils/http/error'

// 是否已注册动态路由
const isRouteRegistered = ref(false)

// 跟踪是否需要关闭 loading
const pendingLoading = ref(false)

/**
 * 设置路由全局前置守卫
 */
export function setupBeforeEachGuard(router: Router): void {
  router.beforeEach(
    async (
      to: RouteLocationNormalized,
      from: RouteLocationNormalized,
      next: NavigationGuardNext
    ) => {
      try {
        await handleRouteGuard(to, from, next, router)
      } catch (error) {
        console.error('路由守卫处理失败:', error)
        next({ name: 'Exception500' })
      }
    }
  )

  // 设置后置守卫以关闭 loading 和进度条
  setupAfterEachGuard(router)
}

/**
 * 设置路由全局后置守卫
 */
function setupAfterEachGuard(router: Router): void {
  router.afterEach(() => {
    // 关闭进度条
    const settingStore = useSettingStore()
    if (settingStore.showNprogress) {
      NProgress.done()
    }

    // 关闭 loading 效果
    if (pendingLoading.value) {
      nextTick(() => {
        loadingService.hideLoading()
        pendingLoading.value = false
      })
    }
  })
}

/**
 * 处理路由守卫逻辑
 */
async function handleRouteGuard(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext,
  router: Router
): Promise<void> {
  const settingStore = useSettingStore()
  const userStore = useUserStore()

  // 处理进度条
  if (settingStore.showNprogress) {
    NProgress.start()
  }

  // 处理登录状态
  if (!(await handleLoginStatus(to, userStore, next))) {
    return
  }

  // 处理动态路由注册
  if (!isRouteRegistered.value && userStore.isLogin) {
    await handleDynamicRoutes(to, from, next, router)
    return
  }

  // 处理根路径跳转到首页
  if (userStore.isLogin && isRouteRegistered.value && handleRootPathRedirect(to, next)) {
    return
  }

  // 处理已知的匹配路由
  if (to.matched.length > 0) {
    setWorktab(to)
    setPageTitle(to)
    next()
    return
  }

  // 未匹配到路由，跳转到 404
  next({ name: 'Exception404' })
}

/**
 * 处理登录状态
 */
async function handleLoginStatus(
  to: RouteLocationNormalized,
  userStore: ReturnType<typeof useUserStore>,
  next: NavigationGuardNext
): Promise<boolean> {
  // 检查是否为静态路由（通过路由 name 判断）
  const isStaticRoute = isRouteInStaticRoutes(to.path)

  if (!userStore.isLogin && to.path !== RoutesAlias.Login && !isStaticRoute) {
    userStore.logOut()
    next({ name: 'Login' })
    return false
  }
  return true
}

/**
 * 检查路由是否为静态路由
 */
function isRouteInStaticRoutes(path: string): boolean {
  const checkRoute = (routes: any[], targetPath: string): boolean => {
    return routes.some((route) => {
      // 处理动态路由参数匹配
      const routePath = route.path
      const pattern = routePath.replace(/:[^/]+/g, '[^/]+').replace(/\*/g, '.*')
      const regex = new RegExp(`^${pattern}$`)

      if (regex.test(targetPath)) {
        return true
      }
      if (route.children && route.children.length > 0) {
        return checkRoute(route.children, targetPath)
      }
      return false
    })
  }

  return checkRoute(staticRoutes, path)
}

/**
 * 处理动态路由注册
 */
async function handleDynamicRoutes(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext,
  router: Router
): Promise<void> {
  // 显示 loading 并标记 pending
  pendingLoading.value = true
  loadingService.showLoading()

  try {
    // 获取用户信息
    await fetchUserInfoIfNeeded(from)

    // 获取菜单数据并注册路由
    await getMenuData(router)

    // 处理根路径跳转
    if (handleRootPathRedirect(to, next)) {
      return
    }

    next({
      path: to.path,
      query: to.query,
      hash: to.hash,
      replace: true
    })
  } catch (error) {
    console.error('动态路由注册失败:', error)
    // 401 错误：axios 拦截器已处理退出登录，取消当前导航即可
    if (isUnauthorizedError(error)) {
      next(false)
      return
    }

    // 其他错误：标记路由已注册（避免无限重试）
    isRouteRegistered.value = true
    next({ name: 'Exception500' })
  }
}

/**
 * 获取菜单数据
 */
async function getMenuData(router: Router): Promise<void> {
  if (useCommon().isFrontendMode.value) {
    await processFrontendMenu(router)
  } else {
    await processBackendMenu(router)
  }
}

/**
 * 处理前端控制模式的菜单逻辑
 */
async function processFrontendMenu(router: Router): Promise<void> {
  const menuList = asyncRoutes.map((route) => menuDataToRouter(route))
  const userStore = useUserStore()
  const roles = userStore.info.roles

  if (!roles) {
    throw new Error('获取用户角色失败')
  }

  // 过滤演示菜单后，再根据角色过滤
  const demoFiltered = filterDemoMenus(menuList)
  const filteredMenuList = filterMenuByRoles(demoFiltered, roles)

  await registerAndStoreMenu(router, filteredMenuList)
}

/**
 * 处理后端控制模式的菜单逻辑
 */
async function processBackendMenu(router: Router): Promise<void> {
  let list: any[] = []
  try {
    list = await fetchGetMenuList()
  } catch (error) {
    console.warn('后端菜单获取失败，使用静态路由作为回退', error)
    list = []
  }

  // 后端返回为空或失败时，使用静态路由作为回退，确保开发环境侧边栏可用
  if (!Array.isArray(list) || list.length === 0) {
    const fallbackMenuList = routeModules.map((route) => menuDataToRouter(route))
    const filteredFallback = filterDemoMenus(fallbackMenuList)
    await registerAndStoreMenu(router, filteredFallback)
    return
  }

  const menuList = list.map((route) => menuDataToRouter(route))
  const filteredMenuList = filterDemoMenus(menuList)
  await registerAndStoreMenu(router, filteredMenuList)
}

/**
 * 递归过滤空菜单项
 */
function filterEmptyMenus(menuList: AppRouteRecord[]): AppRouteRecord[] {
  return menuList
    .map((item) => {
      // 如果有子菜单，先递归过滤子菜单
      if (item.children && item.children.length > 0) {
        const filteredChildren = filterEmptyMenus(item.children)
        return {
          ...item,
          children: filteredChildren
        }
      }
      return item
    })
    .filter((item) => {
      // 如果定义了 children 属性（即使是空数组），说明这是一个目录菜单，应该保留
      if ('children' in item) {
        return true
      }

      // 如果有外链或 iframe，保留
      if (item.meta?.isIframe === true || item.meta?.link) {
        return true
      }

      // 如果有有效的 component，保留
      if (item.component && item.component !== '' && item.component !== RoutesAlias.Layout) {
        return true
      }

      // 其他情况过滤掉
      return false
    })
}

/**
 * 注册路由并存储菜单数据
 */
async function registerAndStoreMenu(router: Router, menuList: AppRouteRecord[]): Promise<void> {
  if (!isValidMenuList(menuList)) {
    throw new Error('获取菜单列表失败，请重新登录')
  }
  const menuStore = useMenuStore()
  // 递归过滤掉为空的菜单项
  const list = filterEmptyMenus(menuList)
  const deduped = dedupeMenus(list)
  // 不再存储菜单列表，仅注册路由
  registerDynamicRoutes(router, deduped)
  // 仅更新首页路径，保证根路径跳转
  menuStore.setHomePath(HOME_PAGE_PATH || getFirstMenuPath(deduped))
  isRouteRegistered.value = true
  useWorktabStore().validateWorktabs(router)
}

/**
 * 过滤演示/模板/示例等菜单项（递归）
 */
function filterDemoMenus(menuList: AppRouteRecord[]): AppRouteRecord[] {
  const demoPrefixes = [
    '/template',
    '/widgets',
    '/examples',
    '/article',
    '/result',
    '/exception',
    '/safeguard',
    '/change',
    '/system/nested'
  ]

  const isDemoPath = (path?: string): boolean => {
    if (!path) return false
    return demoPrefixes.some((prefix) => path.startsWith(prefix))
  }

  const shouldRemove = (item: AppRouteRecord): boolean => {
    // 带外链的帮助类菜单，视为演示项直接移除
    if (item.meta?.link && item.meta?.isIframe === false) return true
    // 路径命中演示前缀
    if (isDemoPath(item.path)) return true
    return false
  }

  const walk = (items: AppRouteRecord[]): AppRouteRecord[] => {
    return items
      .filter((item) => !shouldRemove(item))
      .map((item) => {
        const next: AppRouteRecord = { ...item }
        if (Array.isArray(item.children) && item.children.length > 0) {
          next.children = walk(item.children)
        }
        return next
      })
  }

  return walk(menuList)
}

/**
 * 根据角色过滤菜单
 */
const filterMenuByRoles = (menu: AppRouteRecord[], roles: string[]): AppRouteRecord[] => {
  return menu.reduce((acc: AppRouteRecord[], item) => {
    const itemRoles = item.meta?.roles
    const hasPermission = !itemRoles || itemRoles.some((role) => roles?.includes(role))

    if (hasPermission) {
      const filteredItem = { ...item }
      if (filteredItem.children?.length) {
        filteredItem.children = filterMenuByRoles(filteredItem.children, roles)
      }
      acc.push(filteredItem)
    }

    return acc
  }, [])
}

/**
 * 验证菜单列表是否有效
 */
function isValidMenuList(menuList: AppRouteRecord[]): boolean {
  return Array.isArray(menuList) && menuList.length > 0
}

/**
 * 重置路由相关状态
 */
export function resetRouterState(): void {
  isRouteRegistered.value = false
  // 仅通过内存移除已注册动态路由，不操作菜单 store
  removeAllDynamicRoutes()
}

/**
 * 处理根路径跳转到首页
 */
function handleRootPathRedirect(to: RouteLocationNormalized, next: NavigationGuardNext): boolean {
  if (to.path === '/') {
    const { homePath } = useCommon()
    if (homePath.value && homePath.value !== '/') {
      next({ path: homePath.value, replace: true })
      return true
    }
  }
  return false
}

/**
 * 获取用户信息（如果需要）
 */
async function fetchUserInfoIfNeeded(from: RouteLocationNormalized): Promise<void> {
  const userStore = useUserStore()
  const isRefresh = from.path === '/'
  const needFetch = isRefresh || !userStore.info || Object.keys(userStore.info).length === 0

  if (needFetch) {
    const data = await fetchGetUserInfo()
    userStore.setUserInfo(data)
  }
}

/**
 * 判断是否为未授权错误（401）
 */
function isUnauthorizedError(error: unknown): error is HttpError {
  return (
    isHttpError(error) &&
    (error.code === ApiStatus.unauthorized || error.code === ApiStatus.forbidden)
  )
}

/**
 * 递归去重菜单（按同级的 path/name 键去重）
 */
function dedupeMenus(menuList: AppRouteRecord[]): AppRouteRecord[] {
  const dedupeLevel = (items: AppRouteRecord[]): AppRouteRecord[] => {
    const seen = new Set<string>()
    const unique: AppRouteRecord[] = []

    items.forEach((item) => {
      const key = [String(item.path || ''), String(item.name || ''), String(item.meta?.title || '')]
        .filter(Boolean)
        .join('::')
        .toLowerCase()

      if (!seen.has(key)) {
        seen.add(key)
        const children = item.children ? dedupeLevel(item.children) : undefined
        unique.push({ ...item, children })
      } else {
        console.warn(`[菜单去重] 忽略重复菜单: ${String(item.name || item.meta?.title || '')} (${String(item.path || '')})`)
      }
    })

    return unique
  }

  return dedupeLevel(menuList)
}
