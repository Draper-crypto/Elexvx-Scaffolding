import request from '@/utils/http'

export function fetchPublicSystemConfig() {
  return request.get<Api.SystemSetting.Response>({
    url: '/api/system/settings/public'
  })
}

export function fetchSystemSettings() {
  return request.get<Api.SystemSetting.Response>({
    url: '/api/system/settings'
  })
}

export function fetchUpdateBrandSetting(data: Api.SystemSetting.BrandSetting) {
  return request.put<Api.SystemSetting.BrandSetting>({
    url: '/api/system/settings/brand',
    data
  })
}

export function fetchUpdateWatermarkSetting(data: Api.SystemSetting.WatermarkSetting) {
  return request.put<Api.SystemSetting.WatermarkSetting>({
    url: '/api/system/settings/watermark',
    data
  })
}

export function uploadBrandLogo(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<{ url: string }>({
    url: '/api/system/settings/brand/logo',
    data: formData,
    showSuccessMessage: false
  })
}
