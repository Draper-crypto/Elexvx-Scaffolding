/**
 * 快速入口配置
 * 包含：应用列表、快速链接等配置
 */
import { WEB_LINKS } from '@/utils/constants'
import type { FastEnterConfig } from '@/types/config'

const fastEnterConfig: FastEnterConfig = {
  // 显示条件（屏幕宽度）
  minWidth: 1200,
  // 应用列表
  applications: [
    {
      name: '工作台',
      description: '系统概览与数据统计',
      icon: '&#xe721;',
      iconColor: '#377dff',
      enabled: true,
      order: 1,
      routeName: 'Console',
      routePath: '/dashboard/console'
    },
    {
      name: '分析页',
      description: '数据分析与可视化',
      icon: '&#xe812;',
      iconColor: '#ff3b30',
      enabled: true,
      order: 2,
      routeName: 'Analysis'
    },
    {
      name: '礼花效果',
      description: '动画特效展示',
      icon: '&#xe7ed;',
      iconColor: '#7A7FFF',
      enabled: true,
      order: 3,
      routeName: 'Fireworks',
      routePath: '/widgets/fireworks'
    },
    {
      name: '聊天',
      description: '即时通讯功能',
      icon: '&#xe70a;',
      iconColor: '#13DEB9',
      enabled: true,
      order: 4,
      routeName: 'Chat',
      routePath: '/template/chat'
    },
    {
      name: '官方文档',
      description: '使用指南与开发文档',
      icon: '&#xe788;',
      iconColor: '#ffb100',
      enabled: true,
      order: 5,
      link: WEB_LINKS.DOCS
    },
    {
      name: '技术支持',
      description: '技术支持与问题反馈',
      icon: '&#xe86e;',
      iconColor: '#ff6b6b',
      enabled: true,
      order: 6,
      link: WEB_LINKS.COMMUNITY
    },
    {
      name: '更新日志',
      description: '版本更新与变更记录',
      icon: '&#xe81c;',
      iconColor: '#38C0FC',
      enabled: true,
      order: 7,
      routeName: 'ChangeLog',
      routePath: '/change/log'
    },
    {
      name: '哔哩哔哩',
      description: '技术分享与交流',
      icon: '&#xe6b4;',
      iconColor: '#FB7299',
      enabled: true,
      order: 8,
      link: WEB_LINKS.BILIBILI
    }
  ],
  // 快速链接
  quickLinks: [
    {
      name: '登录',
      enabled: true,
      order: 1,
      routeName: 'Login',
      routePath: '/auth/login'
    },
    {
      name: '注册',
      enabled: true,
      order: 2,
      routeName: 'Register',
      routePath: '/auth/register'
    },
    {
      name: '忘记密码',
      enabled: true,
      order: 3,
      routeName: 'ForgetPassword',
      routePath: '/auth/forget-password'
    },
    {
      name: '定价',
      enabled: true,
      order: 4,
      routeName: 'Pricing',
      routePath: '/template/pricing'
    },
    {
      name: '个人中心',
      enabled: true,
      order: 5,
      routeName: 'UserCenter',
      routePath: '/system/user-center'
    },
    {
      name: '留言管理',
      enabled: true,
      order: 6,
      routeName: 'ArticleComment',
      routePath: '/article/comment'
    }
  ]
}

export default Object.freeze(fastEnterConfig)
