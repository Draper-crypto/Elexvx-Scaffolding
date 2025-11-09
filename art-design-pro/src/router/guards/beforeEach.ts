import type { Router, RouteLocationNormalized, NavigationGuardNext } from 'vue-router'
import { ref, nextTick } from 'vue'
import NProgress from 'nprogress'
import { useSettingStore } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'
import { useMenuStore } from '@/store/modules/menu'
import { setWorktab } from '@/utils/navigation'
import { setPageTitle } from '../utils/utils'
import { fetchGetMenuList } from '@/api/system-manage'
import { fetchLatestChangeLog } from '@/api/change-log'
import { registerDynamicRoutes } from '../utils/registerRoutes'
import { AppRouteRecord } from '@/types/router'
import { RoutesAlias } from '../routesAlias'
import { menuDataToRouter } from '../utils/menuToRouter'
import { asyncRoutes } from '../routes/asyncRoutes'
import { staticRoutes } from '../routes/staticRoutes'
import { loadingService } from '@/utils/ui'
import { useCommon } from '@/composables/useCommon'
import { useWorktabStore } from '@/store/modules/worktab'
import { fetchGetUserInfo } from '@/api/auth'
import { ApiStatus } from '@/utils/http/status'
import { HttpError, isHttpError } from '@/utils/http/error'

// 是否已注册动态路由
const isRouteRegistered = ref(false)

// 璺熻釜鏄惁闇€瑕佸叧闂?loading
const pendingLoading = ref(false)
let versionBadgeLoaded = false
let cachedVersionBadge: string | null = null

/**
 * 璁剧疆璺敱鍏ㄥ眬鍓嶇疆瀹堝崼
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
        console.error('璺敱瀹堝崼澶勭悊澶辫触:', error)
        next({ name: 'Exception500' })
      }
    }
  )

  // 璁剧疆鍚庣疆瀹堝崼浠ュ叧闂?loading 鍜岃繘搴︽潯
  setupAfterEachGuard(router)
}

/**
 * 璁剧疆璺敱鍏ㄥ眬鍚庣疆瀹堝崼
 */
function setupAfterEachGuard(router: Router): void {
  router.afterEach(() => {
    // 鍏抽棴杩涘害鏉?
    const settingStore = useSettingStore()
    if (settingStore.showNprogress) {
      NProgress.done()
    }

    // 鍏抽棴 loading 鏁堟灉
    if (pendingLoading.value) {
      nextTick(() => {
        loadingService.hideLoading()
        pendingLoading.value = false
      })
    }
  })
}

/**
 * 澶勭悊璺敱瀹堝崼閫昏緫
 */
async function handleRouteGuard(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext,
  router: Router
): Promise<void> {
  const settingStore = useSettingStore()
  const userStore = useUserStore()

  // 澶勭悊杩涘害鏉?
  if (settingStore.showNprogress) {
    NProgress.start()
  }

  // 澶勭悊鐧诲綍鐘舵€?
  if (!(await handleLoginStatus(to, userStore, next))) {
    return
  }

  // 澶勭悊鍔ㄦ€佽矾鐢辨敞鍐?
  if (!isRouteRegistered.value && userStore.isLogin) {
    await handleDynamicRoutes(to, from, next, router)
    return
  }

  // 澶勭悊鏍硅矾寰勮烦杞埌棣栭〉
  if (userStore.isLogin && isRouteRegistered.value && handleRootPathRedirect(to, next)) {
    return
  }

  // 澶勭悊宸茬煡鐨勫尮閰嶈矾鐢?
  if (to.matched.length > 0) {
    setWorktab(to)
    setPageTitle(to)
    next()
    return
  }

  // 鏈尮閰嶅埌璺敱锛岃烦杞埌 404
  next({ name: 'Exception404' })
}

/**
 * 澶勭悊鐧诲綍鐘舵€? */
async function handleLoginStatus(
  to: RouteLocationNormalized,
  userStore: ReturnType<typeof useUserStore>,
  next: NavigationGuardNext
): Promise<boolean> {
  // 妫€鏌ユ槸鍚︿负闈欐€佽矾鐢憋紙閫氳繃璺敱 name 鍒ゆ柇锛?
  const isStaticRoute = isRouteInStaticRoutes(to.path)

  if (!userStore.isLogin && to.path !== RoutesAlias.Login && !isStaticRoute) {
    userStore.logOut()
    next({ name: 'Login' })
    return false
  }
  return true
}

/**
 * 妫€鏌ヨ矾鐢辨槸鍚︿负闈欐€佽矾鐢? */
function isRouteInStaticRoutes(path: string): boolean {
  const checkRoute = (routes: any[], targetPath: string): boolean => {
    return routes.some((route) => {
      // 澶勭悊鍔ㄦ€佽矾鐢卞弬鏁板尮閰?
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
 * 澶勭悊鍔ㄦ€佽矾鐢辨敞鍐? */
async function handleDynamicRoutes(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext,
  router: Router
): Promise<void> {
  // 鏄剧ず loading 骞舵爣璁?pending
  pendingLoading.value = true
  loadingService.showLoading()

  try {
    // 鑾峰彇鐢ㄦ埛淇℃伅
    await fetchUserInfoIfNeeded(from)

    // 鑾峰彇鑿滃崟鏁版嵁骞舵敞鍐岃矾鐢?
    await getMenuData(router)

    // 澶勭悊鏍硅矾寰勮烦杞?
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
    console.error('鍔ㄦ€佽矾鐢辨敞鍐屽け璐?', error)
    // 401 閿欒锛歛xios 鎷︽埅鍣ㄥ凡澶勭悊閫€鍑虹櫥褰曪紝鍙栨秷褰撳墠瀵艰埅鍗冲彲
    if (isUnauthorizedError(error)) {
      next(false)
      return
    }

    // 鍏朵粬閿欒锛氭爣璁拌矾鐢卞凡娉ㄥ唽锛堥伩鍏嶆棤闄愰噸璇曪級
    isRouteRegistered.value = true
    next({ name: 'Exception500' })
  }
}

/**
 * 鑾峰彇鑿滃崟鏁版嵁
 */
async function getMenuData(router: Router): Promise<void> {
  if (useCommon().isFrontendMode.value) {
    await processFrontendMenu(router)
  } else {
    await processBackendMenu(router)
  }
}

/**
 * 澶勭悊鍓嶇鎺у埗妯″紡鐨勮彍鍗曢€昏緫
 */
async function processFrontendMenu(router: Router): Promise<void> {
  const menuList = asyncRoutes.map((route) => menuDataToRouter(route))
  const userStore = useUserStore()
  const roles = userStore.info.roles ?? []

  // 濡傛灉娌℃湁瑙掕壊淇℃伅锛堝墠绔ā寮忎笅鍙兘鏈幏鍙栧埌鐢ㄦ埛淇℃伅锛夛紝鍒欎笉鍋氳繃婊わ紝鐩存帴浣跨敤瀹屾暣鑿滃崟
  const filteredMenuList = roles.length > 0 ? filterMenuByRoles(menuList, roles) : menuList

  await applyLatestVersionBadge(filteredMenuList)
  await registerAndStoreMenu(router, filteredMenuList)
}

/**
 * 澶勭悊鍚庣鎺у埗妯″紡鐨勮彍鍗曢€昏緫
 */
async function processBackendMenu(router: Router): Promise<void> {
  try {
    const list = await fetchGetMenuList('self')
    let menuList = list.map((route) => menuDataToRouter(route))
    // 后端为空时，回退到前端静态菜单
    if (!isValidMenuList(menuList)) {
      menuList = asyncRoutes.map((route) => menuDataToRouter(route))
    }
    await applyLatestVersionBadge(menuList)
    await registerAndStoreMenu(router, menuList)
  } catch (e) {
    // 接口失败时回退到前端静态菜单
    const menuList = asyncRoutes.map((route) => menuDataToRouter(route))
    await applyLatestVersionBadge(menuList)
    await registerAndStoreMenu(router, menuList)
  }
}

/**
 * 閫掑綊杩囨护绌鸿彍鍗曢」
 */
async function ensureLatestVersionBadge(): Promise<string | null> {
  if (versionBadgeLoaded) {
    return cachedVersionBadge
  }
  try {
    const latest = await fetchLatestChangeLog()
    cachedVersionBadge = latest?.version ? latest.version : null
  } catch (error) {
    console.warn('获取最新版本号失败', error)
    cachedVersionBadge = null
  } finally {
    versionBadgeLoaded = true
  }
  return cachedVersionBadge
}

async function applyLatestVersionBadge(menuList: AppRouteRecord[]): Promise<void> {
  if (!Array.isArray(menuList) || menuList.length === 0) return
  const version = await ensureLatestVersionBadge()
  if (!version) return
  const normalized = version.startsWith('v') ? version : `v${version}`
  const target = findMenuNode(menuList, (node) => {
    return node.path === '/change/log' || node.component === '/change/log' || node.name === 'ChangeLog'
  })
  if (target) {
    target.meta = target.meta || {}
    target.meta.showTextBadge = normalized
  }
}

function findMenuNode(
  nodes: AppRouteRecord[],
  predicate: (node: AppRouteRecord) => boolean
): AppRouteRecord | null {
  for (const node of nodes) {
    if (predicate(node)) {
      return node
    }
    if (node.children?.length) {
      const child = findMenuNode(node.children, predicate)
      if (child) {
        return child
      }
    }
  }
  return null
}
function filterEmptyMenus(menuList: AppRouteRecord[]): AppRouteRecord[] {
  return menuList
    .map((item) => {
      // 濡傛灉鏈夊瓙鑿滃崟锛屽厛閫掑綊杩囨护瀛愯彍鍗?
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
      // 濡傛灉瀹氫箟浜?children 灞炴€э紙鍗充娇鏄┖鏁扮粍锛夛紝璇存槑杩欐槸涓€涓洰褰曡彍鍗曪紝搴旇淇濈暀
      if ('children' in item) {
        return true
      }

      // 濡傛灉鏈夊閾炬垨 iframe锛屼繚鐣?
      if (item.meta?.isIframe === true || item.meta?.link) {
        return true
      }

      // 濡傛灉鏈夋湁鏁堢殑 component锛屼繚鐣?
      if (item.component && item.component !== '' && item.component !== RoutesAlias.Layout) {
        return true
      }

      // 鍏朵粬鎯呭喌杩囨护鎺?
      return false
    })
}

/**
 * 娉ㄥ唽璺敱骞跺瓨鍌ㄨ彍鍗曟暟鎹? */
async function registerAndStoreMenu(router: Router, menuList: AppRouteRecord[]): Promise<void> {
  if (!isValidMenuList(menuList)) {
    throw new Error('鑾峰彇鑿滃崟鍒楄〃澶辫触锛岃閲嶆柊鐧诲綍')
  }
  const menuStore = useMenuStore()
  // 閫掑綊杩囨护鎺変负绌虹殑鑿滃崟椤?
  const list = filterEmptyMenus(menuList)
  menuStore.setMenuList(list)
  registerDynamicRoutes(router, list)
  isRouteRegistered.value = true
  useWorktabStore().validateWorktabs(router)
}

/**
 * 鏍规嵁瑙掕壊杩囨护鑿滃崟
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
 * 楠岃瘉鑿滃崟鍒楄〃鏄惁鏈夋晥
 */
function isValidMenuList(menuList: AppRouteRecord[]): boolean {
  return Array.isArray(menuList) && menuList.length > 0
}

/**
 * 閲嶇疆璺敱鐩稿叧鐘舵€? */
export function resetRouterState(): void {
  isRouteRegistered.value = false
  const menuStore = useMenuStore()
  menuStore.removeAllDynamicRoutes()
  menuStore.setMenuList([])
}

/**
 * 澶勭悊鏍硅矾寰勮烦杞埌棣栭〉
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
 * 鑾峰彇鐢ㄦ埛淇℃伅锛堝鏋滈渶瑕侊級
 */
async function fetchUserInfoIfNeeded(from: RouteLocationNormalized): Promise<void> {
  const userStore = useUserStore()
  const isRefresh = from.path === '/'
  const needFetch = isRefresh || !userStore.info || Object.keys(userStore.info).length === 0

  if (needFetch) {
    try {
      const data = await fetchGetUserInfo()
      userStore.setUserInfo(data)
    } catch (error) {
      // 鍓嶇妯″紡涓嬶紝鐢ㄦ埛淇℃伅鑾峰彇澶辫触涓嶅奖鍝嶈彍鍗曟敞鍐岋紝璺宠繃閿欒
      if (useCommon().isFrontendMode.value) {
        console.warn('前端模式下获取用户信息失败，跳过：', error)
      } else {
        throw error
      }
    }
  }
}

/**
 * 鍒ゆ柇鏄惁涓烘湭鎺堟潈閿欒锛?01锛? */
function isUnauthorizedError(error: unknown): error is HttpError {
  return isHttpError(error) && error.code === ApiStatus.unauthorized
}
