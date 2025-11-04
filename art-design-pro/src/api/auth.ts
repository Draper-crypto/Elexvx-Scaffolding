import request from '@/utils/http'

// 后端返回的用户信息结构（UserDto）
interface BackendUserDto {
  id: number
  username: string
  email?: string
  avatarUrl?: string
  roles?: { id: number; roleName: string; roleCode: string; status: number }[]
  permissions?: string[]
}

/**
 * 登录
 * @param params 登录参数
 * @returns 登录响应
 */
export function fetchLogin(params: Api.Auth.LoginParams) {
  return request.post<Api.Auth.LoginResponse>({
    url: '/api/auth/login',
    params
    // showSuccessMessage: true // 显示成功消息
    // showErrorMessage: false // 不显示错误消息
  })
}

/**
 * 获取用户信息
 * @returns 用户信息
 */
export async function fetchGetUserInfo() {
  const dto = await request.get<BackendUserDto>({
    url: '/api/auth/me'
  })

  // 映射为前端使用的 UserInfo 结构
  const userInfo: Api.Auth.UserInfo = {
    userId: dto.id,
    userName: dto.username,
    email: dto.email || '',
    avatar: dto.avatarUrl,
    roles: Array.isArray(dto.roles) ? dto.roles.map((r) => r.roleCode) : [],
    buttons: Array.isArray(dto.permissions) ? dto.permissions : []
  }

  return userInfo
}
