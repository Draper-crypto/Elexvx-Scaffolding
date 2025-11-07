import { AppRouteRecord } from '@/types/router'
import { WEB_LINKS } from '@/utils/constants'

export const helpRoutes: AppRouteRecord[] = [
  {
    name: 'ChangeLog',
    path: '/change/log',
    component: '/change/log',
    meta: {
      title: 'menus.plan.log',
      showTextBadge: `v${__APP_VERSION__}`,
      icon: '&#xe712;',
      keepAlive: false
    }
  }
]
