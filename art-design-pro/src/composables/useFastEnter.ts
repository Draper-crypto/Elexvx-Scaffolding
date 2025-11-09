/**
 * 快速入口 composable
 * 用于获取和管理快速入口配置
 */
import { computed } from 'vue'
import appConfig from '@/config'
import type {
  FastEnterApplication,
  FastEnterBaseItem,
  FastEnterQuickLink
} from '@/types/config'
import { useMenuStore } from '@/store/modules/menu'
import type { AppRouteRecord } from '@/types/router'
import { storeToRefs } from 'pinia'
import { formatMenuTitle } from '@/router/utils/utils'

interface MenuMaps {
  byPath: Map<string, AppRouteRecord>
  byName: Map<string, AppRouteRecord>
}

const normalizePath = (path?: string): string => {
  if (!path) return ''
  let normalized = path.trim()
  if (!normalized) return ''
  normalized = normalized.startsWith('/') ? normalized : `/${normalized}`
  normalized = normalized.replace(/\/+/g, '/')
  if (normalized.length > 1 && normalized.endsWith('/')) {
    normalized = normalized.slice(0, -1)
  }
  return normalized
}

const createMenuMaps = (menus: AppRouteRecord[] = []): MenuMaps => {
  const byPath = new Map<string, AppRouteRecord>()
  const byName = new Map<string, AppRouteRecord>()
  const traverse = (list: AppRouteRecord[]) => {
    list.forEach((item) => {
      if (item.path) {
        byPath.set(normalizePath(item.path), item)
      }
      if (item.name) {
        byName.set(String(item.name), item)
      }
      if (item.children?.length) {
        traverse(item.children)
      }
    })
  }
  traverse(menus)
  return { byPath, byName }
}

const findMenuNode = (item: FastEnterBaseItem, maps: MenuMaps): AppRouteRecord | undefined => {
  if (item.routePath) {
    const normalized = normalizePath(item.routePath)
    if (normalized && maps.byPath.has(normalized)) {
      return maps.byPath.get(normalized)
    }
  }
  if (item.routeName && maps.byName.has(item.routeName)) {
    return maps.byName.get(item.routeName)
  }
  return undefined
}

const syncItemWithMenu = <T extends FastEnterBaseItem>(item: T, maps: MenuMaps): T => {
  const matched = findMenuNode(item, maps)
  if (!matched) return item
  const resolvedTitle = formatMenuTitle(
    String(matched.meta?.title || matched.name || item.name || '')
  )
  if (!resolvedTitle) return item
  return {
    ...item,
    name: resolvedTitle
  }
}

export function useFastEnter() {
  const menuStore = useMenuStore()
  const { menuList } = storeToRefs(menuStore)
  // 获取快速入口配置
  const fastEnterConfig = computed(() => appConfig.fastEnter)
  const menuMaps = computed(() => createMenuMaps(menuList.value))

  const enhanceItems = <T extends FastEnterBaseItem>(items: T[]): T[] => {
    const maps = menuMaps.value
    return items.map((item) => syncItemWithMenu(item, maps))
  }

  // 获取启用的应用列表（按排序权重排序）
  const enabledApplications = computed<FastEnterApplication[]>(() => {
    if (!fastEnterConfig.value?.applications) return []

    const apps = fastEnterConfig.value.applications
      .filter((app) => app.enabled !== false)
      .sort((a, b) => (a.order || 0) - (b.order || 0))

    return enhanceItems(apps)
  })

  // 获取启用的快速链接（按排序权重排序）
  const enabledQuickLinks = computed<FastEnterQuickLink[]>(() => {
    if (!fastEnterConfig.value?.quickLinks) return []

    const links = fastEnterConfig.value.quickLinks
      .filter((link) => link.enabled !== false)
      .sort((a, b) => (a.order || 0) - (b.order || 0))

    return enhanceItems(links)
  })

  // 获取最小显示宽度
  const minWidth = computed(() => {
    return fastEnterConfig.value?.minWidth || 1200
  })

  return {
    fastEnterConfig,
    enabledApplications,
    enabledQuickLinks,
    minWidth
  }
}
