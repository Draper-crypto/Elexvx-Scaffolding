import request from '@/utils/http'

export function fetchPluginList() {
  return request.get<Api.Plugin.Item[]>({
    url: '/api/system/plugins'
  })
}

export function fetchLoadPlugin(pluginKey: string) {
  return request.post<Api.Plugin.Item>({
    url: `/api/system/plugins/${pluginKey}/load`
  })
}

export function fetchUnloadPlugin(pluginKey: string) {
  return request.post<Api.Plugin.Item>({
    url: `/api/system/plugins/${pluginKey}/unload`
  })
}
