import { AppRouteRecord } from '@/types/router'

export const examplesRoutes: AppRouteRecord = {
  path: '/examples',
  name: 'Examples',
  component: '/index/index',
  meta: {
    title: 'menus.examples.title',
    icon: '&#xe8d4;'
  },
  children: [
    {
      path: 'permission',
      name: 'Permission',
      component: '',
      meta: {
        title: 'menus.examples.permission.title'
      },
      children: [
        {
          path: '/examples/permission/switch-role',
          name: 'PermissionSwitchRole',
          component: '/examples/permission/switch-role',
          meta: {
            title: 'menus.examples.permission.switchRole',
            keepAlive: true
          }
        },
        {
          path: '/examples/permission/button-auth',
          name: 'PermissionButtonAuth',
          component: '/examples/permission/button-auth',
          meta: {
            title: 'menus.examples.permission.buttonAuth',
            keepAlive: true,
            authList: [
              { title: '新增', authMark: 'add' },
              { title: '编辑', authMark: 'edit' },
              { title: '删除', authMark: 'delete' },
              { title: '导出', authMark: 'export' },
              { title: '查看', authMark: 'view' },
              { title: '发布', authMark: 'publish' },
              { title: '配置', authMark: 'config' },
              { title: '管理', authMark: 'manage' }
            ]
          }
        },
        {
          path: '/examples/permission/page-visibility',
          name: 'PermissionPageVisibility',
          component: '/examples/permission/page-visibility',
          meta: {
            title: 'menus.examples.permission.pageVisibility',
            keepAlive: true,
            roles: ['R_SUPER']
          }
        }
      ]
    },
    // 其他示例页面已移除，仅保留权限演示
  ]
}
