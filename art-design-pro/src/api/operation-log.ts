import request from '@/utils/http'

export function fetchOperationLogList(params: Api.OperationLog.QueryParams) {
  const query: Record<string, any> = { ...(params || {}) }
  if (query.current != null) {
    query.page = query.current
    delete query.current
  }
  if (query.size != null) {
    query.size = query.size
  }
  if (query.username !== undefined && query.username === '') {
    delete query.username
  }
  if (query.actionType !== undefined && query.actionType === '') {
    delete query.actionType
  }
  if (query.success === '') {
    delete query.success
  }
  return request.get<Api.OperationLog.ListResponse>({
    url: '/api/system/operation-logs',
    params: query
  })
}

export function fetchOperationLogTypes() {
  return request.get<Api.OperationLog.TypeMeta[]>({
    url: '/api/system/operation-logs/types'
  })
}
