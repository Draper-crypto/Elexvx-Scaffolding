import request from '@/utils/http'

export function fetchUserProfile() {
  return request.get<Api.Profile.Detail>({
    url: '/api/profile'
  })
}

export function fetchUpdateProfile(data: Api.Profile.UpdateRequest) {
  return request.put<Api.Profile.Detail>({
    url: '/api/profile',
    data
  })
}

export function fetchChangePassword(data: Api.Profile.ChangePasswordRequest) {
  return request.put<void>({
    url: '/api/profile/password',
    data
  })
}
