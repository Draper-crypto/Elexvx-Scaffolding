import request from '@/utils/http'

export function fetchLogin(params: Api.Auth.LoginParams) {
  return request.post<Api.Auth.LoginResponse>({
    url: '/api/auth/login',
    params
  })
}

export function fetchGetUserInfo() {
  return request.get<Api.Auth.UserInfo>({
    url: '/api/auth/me'
  })
}

export function fetchBootstrap() {
  return request.get<{ user: Api.Auth.UserInfo; menus: any[] }>({
    url: '/api/auth/bootstrap'
  })
}

export function fetchUserRoles(username: string) {
  return request.get<{ roles: { roleName: string; roleCode: string }[] }>({
    url: '/api/auth/roles',
    params: { username },
    ignoreUnauthorized: true,
    showErrorMessage: false
  })
}

export function fetchForgotPassword(params: { username: string; newPassword: string }) {
  return request.post<void>({
    url: '/api/auth/forgot-password',
    params
  })
}

export function fetchRegister(params: {
  username: string
  password: string
  gender: number
  phone: string
}) {
  return request.post<void>({
    url: '/api/auth/register',
    params
  })
}
