import request from '@/utils/http'

export function fetchProfileInfo() {
  return request.get<any>({ url: '/api/profile/info' })
}

export function updateProfileInfo(params: any) {
  return request.put<void>({ url: '/api/profile/info', params })
}

export function updateProfilePassword(params: { password: string; newPassword: string; confirmPassword: string }) {
  return request.put<void>({ url: '/api/profile/password', params })
}