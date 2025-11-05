import { routeModules } from '../src/router/modules'

// 简单导出为 JSON，去掉可能为 undefined 的字段
const replacer = (_key: string, value: unknown) => {
  if (value === undefined) return undefined
  return value
}

const json = JSON.stringify(routeModules, replacer)
process.stdout.write(json)

