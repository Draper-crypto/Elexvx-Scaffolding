/**
 * namespace: Api
 *
 * 所有接口相关类型定义
 * 在.vue文件使用会报错，需要在 eslint.config.mjs 中配置 globals: { Api: 'readonly' }
 */

declare namespace Api {
  /** 通用类型 */
  namespace Common {
    /** 分页参数 */
    interface PaginationParams {
      /** 当前页码 */
      current: number
      /** 每页条数 */
      size: number
      /** 总条数 */
      total: number
    }

    /** 通用搜索参数 */
    type CommonSearchParams = Pick<PaginationParams, 'current' | 'size'>

    /** 分页响应基础结构 */
    interface PaginatedResponse<T = any> {
      records: T[]
      current: number
      size: number
      total: number
    }

    /** 启用状态 */
    type EnableStatus = '1' | '2'
  }

  /** 认证类型 */
  namespace Auth {
    /** 登录参数 */
    interface LoginParams {
      userName: string
      password: string
    }

    /** 登录响应 */
    interface LoginResponse {
      token: string
      refreshToken: string
    }

    /** 用户信息 */
    interface UserInfo {
      buttons: string[]
      roles: string[]
      userId: number
      userName: string
      email: string
      avatar?: string
      fullName?: string
      nickname?: string
      displayName?: string
    }
  }

  /** 系统管理类型 */
  namespace SystemManage {
    /** 用户列表 */
    type UserList = Api.Common.PaginatedResponse<UserListItem>

    /** 用户列表项 */
    interface UserListItem {
      id: number
      avatar: string
      status: string
      userName: string
      userGender: string
      nickName: string
      userPhone: string
      userEmail: string
      userRoles: string[]
      createBy: string
      createTime: string
      updateBy: string
      updateTime: string
    }

    /** 用户搜索参数 */
    type UserSearchParams = Partial<
      Pick<UserListItem, 'id' | 'userName' | 'userGender' | 'userPhone' | 'userEmail' | 'status'> &
        Api.Common.CommonSearchParams
    >

    /** 角色列表 */
    type RoleList = Api.Common.PaginatedResponse<RoleListItem>

    /** 角色列表项 */
    interface RoleListItem {
      roleId: number
      roleName: string
      roleCode: string
      description: string
      enabled: boolean
      createTime: string
    }

    /** 角色搜索参数 */
    type RoleSearchParams = Partial<
      Pick<RoleListItem, 'roleId' | 'roleName' | 'roleCode' | 'description' | 'enabled'> &
        Api.Common.CommonSearchParams
    >

    /** 菜单提交数据 */
    interface MenuSubmitData {
      id?: number
      parentId?: number | null
      menuType: number
      menuName: string
      routePath: string
      componentPath?: string
      permissionHint?: string
      icon?: string
      orderNum?: number
      externalLink?: string
      badgeText?: string
      activePath?: string
      enabled?: boolean
      cachePage?: boolean
      hiddenMenu?: boolean
      embedded?: boolean
      showBadge?: boolean
      affix?: boolean
      hideTab?: boolean
      fullScreen?: boolean
    }

    interface MenuAuthData {
      id?: number
      menuId?: number
      title: string
      authMark: string
    }
  }

  /** 更新日志 */
  namespace ChangeLog {
    interface Item {
      id: number
      version: string
      title: string
      content: string
      releaseDate?: string
      remark?: string
      requireReLogin?: boolean
      createdAt?: string
      updatedAt?: string
    }

    interface ListResponse extends Api.Common.PaginatedResponse<Item> {}

    interface FormData {
      version: string
      title: string
      content: string
      releaseDate?: string
      remark?: string
      requireReLogin?: boolean
    }
  }

  /** 用户个人中心 */
  namespace Profile {
    interface Detail {
      id: number
      username: string
      name?: string
      nickname?: string
      gender?: number
      email?: string
      phone?: string
      avatarUrl?: string
      address?: string
      bio?: string
    }

    interface UpdateRequest extends Partial<Omit<Detail, 'id' | 'username' | 'avatarUrl'>> {}

    interface ChangePasswordRequest {
      currentPassword: string
      newPassword: string
      confirmPassword?: string
    }
  }

  /** 系统设置 */
  namespace SystemSetting {
    interface BrandSetting {
      name: string
    }

    interface WatermarkSetting {
      enabled: boolean
      mode: 'username' | 'custom'
      customText: string
      fontSize: number
    }

    interface Response {
      brand: BrandSetting
      watermark: WatermarkSetting
    }
  }
}
