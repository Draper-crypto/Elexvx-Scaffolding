import request from '@/utils/http'

/**
 * 登录
 * @param params 登录参数
 * @returns 登录响应
 */
export function fetchLogin(params: Api.Auth.LoginParams) {
  const mapped = {
    username: (params as any).userName ?? (params as any).username,
    password: (params as any).password,
    roleCode: (params as any).roleCode
  }
  return request.post<{ token: string; user: any }>({
    url: '/api/auth/login',
    params: mapped
  })
}

/**
 * 获取用户信息
 * @returns 用户信息
 */
export function fetchGetUserInfo() {
  return request.get<any>({
    url: '/api/auth/me'
  })
}

export function fetchBootstrap() {
  return request.get<{ user: any; menus: any[] }>({
    url: '/api/auth/bootstrap'
  })
}

export function fetchUserRoles(username: string) {
  return request.get<{ roles: { id: number; roleName: string; roleCode: string; status: number }[] }>({
    url: '/api/auth/roles',
    params: { username }
  })
}

export function fetchRegister(params: { username: string; password: string; gender?: number; phone?: string }) {
  return request.post<{ id: number }>({
    url: '/api/auth/register',
    params
  })
}

export function fetchForgotPassword(params: { username: string; newPassword: string; captchaToken?: string; captchaCode?: string }) {
  return request.post<void>({
    url: '/api/auth/forgot-password',
    params
  })
}
