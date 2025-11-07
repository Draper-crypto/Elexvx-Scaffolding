import request from '@/utils/http'
import { AppRouteRecord } from '@/types/router'

// 获取用户列表
export function fetchGetUserList(params: Api.SystemManage.UserSearchParams) {
  const { current, size, ...rest } = params || {}
  return request.get<Api.Common.PaginatedResponse<any>>({
    url: '/api/admin/users',
    params: {
      page: current,
      size,
      ...rest
    }
  })
}

// 新增用户
export function createUser(data: {
  username: string
  password: string
  phone?: string
  gender?: number
  roleIds?: number[]
}) {
  return request.post<any>({
    url: '/api/admin/users',
    data,
    showSuccessMessage: true
  })
}

// 更新用户
export function updateUser(id: number, data: {
  name?: string
  nickname?: string
  gender?: number
  email?: string
  phone?: string
  address?: string
  status?: number
}) {
  return request.put<any>({
    url: `/api/admin/users/${id}`,
    data,
    showSuccessMessage: true
  })
}

// 删除用户
export function deleteUser(id: number) {
  return request.del<any>({
    url: `/api/admin/users/${id}`,
    showSuccessMessage: true
  })
}

// 获取更新日志
export function fetchChangelogList() {
  return request.get<Array<{ version: string; title: string; date: string; detail?: string[]; requireReLogin?: boolean; remark?: string }>>({
    url: '/api/changelog'
  })
}

// 获取角色列表
export function fetchGetRoleList(params: Api.SystemManage.RoleSearchParams) {
  return request.get<Api.SystemManage.RoleList>({
    url: '/api/role/list',
    params
  })
}

// 获取菜单列表
export function fetchGetMenuList() {
  return request.get<AppRouteRecord[]>({
    url: '/api/system/menus'
  })
}
