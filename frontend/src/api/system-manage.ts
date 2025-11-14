import request from '@/utils/http'
import { AppRouteRecord } from '@/types/router'

export function fetchGetUserList(params: Api.SystemManage.UserSearchParams) {
  return request.get<Api.SystemManage.UserList>({
    url: '/api/user/list',
    params
  })
}

export function fetchGetRoleList(params: Api.SystemManage.RoleSearchParams) {
  return request.get<Api.SystemManage.RoleList>({
    url: '/api/role/list',
    params
  })
}

export async function fetchGetMenuList(): Promise<AppRouteRecord[]> {
  const data = await request.get<{ user: Api.Auth.UserInfo; menus: AppRouteRecord[] }>({
    url: '/api/auth/bootstrap'
  })
  return Array.isArray(data.menus) ? data.menus : []
}

export function fetchUpdateUser(params: { id: number; gender: number; phone: string }) {
  return request.post<void>({
    url: '/api/user/update',
    params
  })
}
