import request from '@/utils/http'

export function fetchChangeLogs(params?: { page?: number; size?: number; keyword?: string }) {
  return request.get<Api.ChangeLog.ListResponse>({
    url: '/api/change-logs',
    params
  })
}

export function fetchPublicChangeLogs() {
  return request.get<Api.ChangeLog.Item[]>({
    url: '/api/change-logs/public'
  })
}

export function fetchLatestChangeLog() {
  return request.get<Api.ChangeLog.Item | null>({
    url: '/api/change-logs/latest'
  })
}

export function fetchCreateChangeLog(data: Api.ChangeLog.FormData) {
  return request.post<Api.ChangeLog.Item>({
    url: '/api/change-logs',
    data
  })
}

export function fetchUpdateChangeLog(id: number, data: Api.ChangeLog.FormData) {
  return request.put<Api.ChangeLog.Item>({
    url: `/api/change-logs/${id}`,
    data
  })
}

export function fetchDeleteChangeLog(id: number) {
  return request.del<void>({
    url: `/api/change-logs/${id}`
  })
}
