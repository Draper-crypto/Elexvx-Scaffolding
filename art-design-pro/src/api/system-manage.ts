import request from '@/utils/http'
import { AppRouteRecord } from '@/types/router'

// 获取用户列表
export function fetchGetUserList(params: Api.SystemManage.UserSearchParams) {
  return request.get<Api.SystemManage.UserList>({
    url: '/api/user/list',
    params
  })
}

// 新增用户
export function fetchCreateUser(data: Api.SystemManage.UserCreatePayload) {
  return request.post<Api.SystemManage.UserListItem>({
    url: '/api/user',
    data
  })
}

// 更新用户
export function fetchUpdateUser(id: number, data: Api.SystemManage.UserUpdatePayload) {
  return request.put<Api.SystemManage.UserListItem>({
    url: `/api/user/${id}`,
    data
  })
}

// 删除用户
export function fetchDeleteUser(id: number) {
  return request.del<void>({
    url: `/api/user/${id}`
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

// 已移除“导入静态路由到数据库”入口，前端仅依赖后端(MySQL)菜单数据

// 新增菜单或权限按钮
export function fetchCreateMenu(data: any) {
  return request.post<any>({
    url: '/api/system/menus',
    data
  })
}

// 更新菜单或权限按钮
export function fetchUpdateMenu(id: number, data: any) {
  return request.put<any>({
    url: `/api/system/menus/${id}`,
    data
  })
}

// 删除菜单或权限按钮
export function fetchDeleteMenu(id: number) {
  return request.del<void>({
    url: `/api/system/menus/${id}`
  })
}
