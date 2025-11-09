import request from '@/utils/http'
import { AppRouteRecord } from '@/types/router'

// 获取用户列表
export function fetchGetUserList(params: Api.SystemManage.UserSearchParams) {
  const q: Record<string, any> = { ...(params || {}) }
  if (q.current != null) {
    q.page = q.current
    delete q.current
  }
  if (q.size != null) {
    q.size = q.size
  }
  return request.get<Api.SystemManage.UserList>({
    url: '/api/admin/users',
    params: q
  })
}

// 创建用户
export function fetchCreateUser(data: any) {
  return request.post<any>({
    url: '/api/admin/users',
    data
  })
}

// 更新用户
export function fetchUpdateUser(id: number, data: any) {
  return request.put<any>({
    url: `/api/admin/users/${id}`,
    data
  })
}

// 删除用户
export function fetchDeleteUser(id: number) {
  return request.del<void>({
    url: `/api/admin/users/${id}`
  })
}

// 设置用户角色
export function fetchSetUserRoles(id: number, roleIds: number[]) {
  return request.put<void>({
    url: `/api/admin/users/${id}/roles`,
    data: { roleIds }
  })
}

// 获取角色列表
export function fetchGetRoleList(params: Api.SystemManage.RoleSearchParams) {
  const q: Record<string, any> = { ...(params || {}) }
  if (q.current != null) {
    q.page = q.current
    delete q.current
  }
  if (q.size != null) {
    q.size = q.size
  }
  return request.get<any>({
    url: '/api/admin/roles',
    params: q
  }).then((res) => {
    const records = Array.isArray(res.records) ? res.records : []
    const mapped = records.map((r: any) => ({
      roleId: Number(r.id),
      roleName: String(r.roleName || ''),
      roleCode: String(r.roleCode || ''),
      description: String(r.description || ''),
      enabled: Number(r.status) === 1,
      createTime: r.createdAt ? String(r.createdAt) : ''
    }))
    return { records: mapped, current: res.current, size: res.size, total: res.total } as Api.SystemManage.RoleList
  })
}

// 获取菜单列表
/**
 * 获取菜单列表（后端返回 MenuTree[]），转换为 AppRouteRecord[]
 */
export function fetchGetMenuList(scope: 'admin' | 'self' = 'admin') {
  const url = scope === 'admin' ? '/api/admin/menus/tree' : '/api/menus/my'
  return request.get<any[]>({
    url
  }).then((trees) => transformMenuTrees(trees))
}

export function fetchCreateMenu(data: Api.SystemManage.MenuSubmitData) {
  return request.post<any>({
    url: '/api/admin/menus',
    data
  })
}

export function fetchUpdateMenu(id: number, data: Api.SystemManage.MenuSubmitData) {
  return request.put<any>({
    url: `/api/admin/menus/${id}`,
    data
  })
}

export function fetchDeleteMenu(id: number) {
  return request.del<void>({
    url: `/api/admin/menus/${id}`
  })
}

export function fetchCreateMenuAuth(menuId: number, data: Api.SystemManage.MenuAuthData) {
  return request.post<Api.SystemManage.MenuAuthData>({
    url: `/api/admin/menus/${menuId}/auths`,
    data
  })
}

export function fetchUpdateMenuAuth(menuId: number, authId: number, data: Api.SystemManage.MenuAuthData) {
  return request.put<Api.SystemManage.MenuAuthData>({
    url: `/api/admin/menus/${menuId}/auths/${authId}`,
    data
  })
}

export function fetchDeleteMenuAuth(menuId: number, authId: number) {
  return request.del<void>({
    url: `/api/admin/menus/${menuId}/auths/${authId}`
  })
}

export function fetchGetRoleMenuIds(roleId: number) {
  return request.get<number[]>({
    url: `/api/admin/roles/${roleId}/menus`
  })
}

export function fetchSetRoleMenus(roleId: number, menuIds: number[]) {
  return request.put<void>({
    url: `/api/admin/roles/${roleId}/menus`,
    data: { menuIds }
  })
}

export function fetchGetRolePermissionIds(roleId: number) {
  return request.get<number[]>({
    url: `/api/admin/roles/${roleId}/permissions`
  })
}

export function fetchSetRolePermissions(roleId: number, permissionIds: number[]) {
  return request.put<void>({
    url: `/api/admin/roles/${roleId}/permissions`,
    data: { permissionIds }
  })
}

function transformMenuTrees(trees: any[]): AppRouteRecord[] {
  return (trees || []).map((node) => transformNode(node))
}

function transformNode(node: any): AppRouteRecord {
  const route: AppRouteRecord = {
    id: Number(node.id),
    parentId: node.parentId != null ? Number(node.parentId) : null,
    path: String(node.routePath || '').trim(),
    name: String(node.menuName || ''),
    component:
      node.menuType === 1
        ? '/index/index'
        : String(node.componentPath || ''),
    meta: {
      title: String(node.menuName || ''),
      icon: node.icon || '',
      keepAlive: !!node.cachePage,
      isHide: !!node.hiddenMenu,
      isFullPage: !!node.fullScreen,
      isIframe: !!node.embedded,
      showBadge: !!node.showBadge,
      showTextBadge: node.badgeText || '',
      permissionHint: node.permissionHint || '',
      activePath: node.activePath || '',
      menuType: Number(node.menuType || 2),
      orderNum: node.orderNum ?? 0,
      externalLink: node.externalLink || '',
      useIconPicker: !!node.useIconPicker,
      authList: Array.isArray(node.authList)
        ? node.authList.map((a: any) => ({
            id: a.id != null ? Number(a.id) : undefined,
            menuId: a.menuId != null ? Number(a.menuId) : undefined,
            authMark: String(a.authMark || ''),
            title: String(a.title || '')
          }))
        : []
    },
    children: Array.isArray(node.children)
      ? node.children.map((child: any) => transformNode(child))
      : []
  }
  return route
}
