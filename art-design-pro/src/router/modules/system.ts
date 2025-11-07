import { AppRouteRecord } from '@/types/router'

export const systemRoutes: AppRouteRecord = {
  path: '/system',
  name: 'System',
  component: '/index/index',
  meta: {
    title: 'menus.system.title',
    icon: '&#xe7b9;',
    roles: ['R_SUPER', 'R_ADMIN']
  },
  children: [
    {
      path: 'user',
      name: 'User',
      component: '/system/user',
      meta: {
        title: 'menus.system.user',
        keepAlive: true,
        roles: ['R_SUPER', 'R_ADMIN']
      }
    },
    {
      path: 'role',
      name: 'Role',
      component: '/system/role',
      meta: {
        title: 'menus.system.role',
        keepAlive: true,
        roles: ['R_SUPER']
      }
    },
    {
      path: 'user-center',
      name: 'UserCenter',
      component: '/system/user-center',
      meta: {
        title: 'menus.system.userCenter',
        isHide: true,
        keepAlive: true,
        isHideTab: true
      }
    },
    {
      path: 'menu',
      name: 'Menus',
      component: '/system/menu',
      meta: {
        title: 'menus.system.menu',
        keepAlive: true,
        roles: ['R_SUPER'],
        authList: [
          { title: '新增', authMark: 'add' },
          { title: '编辑', authMark: 'edit' },
          { title: '删除', authMark: 'delete' }
        ]
      }
    },
    {
      path: 'nested',
      name: 'Nested',
      component: '',
      meta: {
        title: 'menus.system.nested',
        keepAlive: true
      },
      children: [
        
        {
          path: 'menu2',
          name: 'NestedMenu2',
          component: '',
          meta: {
            title: 'menus.system.menu2',
            icon: '&#xe676;',
            keepAlive: true
          },
          children: [
            
          ]
        },
        {
          path: 'menu3',
          name: 'NestedMenu3',
          component: '',
          meta: {
            title: 'menus.system.menu3',
            icon: '&#xe676;',
            keepAlive: true
          },
          children: [
            {
              path: 'menu3-2',
              name: 'NestedMenu3-2',
              component: '',
              meta: {
                title: 'menus.system.menu32',
                icon: '&#xe676;',
                keepAlive: true
              },
              children: [
                
              ]
            }
          ]
        }
      ]
    }
  ]
}
