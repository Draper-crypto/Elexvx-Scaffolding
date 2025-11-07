## 认证与用户

### 模块：登录与认证（页面：登录）

- 操作：登录、获取验证码、登出、获取当前用户
- 接口：
  - `POST /api/auth/login`
    - 请求体：`LoginRequest`
    - 响应体：`{ token: string, user: UserDto }`
  - `GET /api/auth/captcha` → `CaptchaResponse`
  - `POST /api/auth/logout`
  - `GET /api/auth/me` → `UserDto`
- 字段表（LoginRequest）：

| 名称         | 类型   | 长度 | 是否必填 | 说明                                     |
| ------------ | ------ | ---- | -------- | ---------------------------------------- |
| username     | 字符串 | ≤64  | 是       | 用户名                                   |
| password     | 字符串 | ≤128 | 是       | 密码明文（HTTPS 传输，服务端做哈希比对） |
| captchaToken | 字符串 | ≤64  | 否       | 验证码会话令牌（与图片/算式绑定）        |
| captchaCode  | 字符串 | ≤8   | 否       | 验证码答案                               |

- 字段表（CaptchaResponse）：

| 名称            | 类型   | 长度 | 必填 | 说明                     |
| --------------- | ------ | ---- | ---- | ------------------------ |
| captchaToken    | 字符串 | ≤64  | 是   | 验证码会话令牌           |
| imageBase64     | 字符串 | —    | 是   | Base64 图片/或SVG        |
| expireInSeconds | 数字   | —    | 是   | 过期秒数（建议 120–300） |

- 字段表（UserDto，登录/获取当前用户返回）：

| 名称           | 类型            | 长度      | 必填 | 说明                               |
| -------------- | --------------- | --------- | ---- | ---------------------------------- |
| id             | 整数            | —         | 是   | 用户ID                             |
| username       | 字符串          | ≤64       | 是   | 登录名                             |
| name           | 字符串          | ≤100      | 否   | 姓名                               |
| nickname       | 字符串          | ≤100      | 否   | 昵称                               |
| gender         | 枚举            | {0,1,2,9} | 否   | 0未知/1男/2女/9其他                |
| email          | 字符串          | ≤255      | 否   | 邮箱                               |
| phone          | 字符串          | ≤30       | 否   | 手机号                             |
| avatarUrl      | 字符串          | ≤512      | 否   | 头像URL                            |
| address        | 字符串          | ≤255      | 否   | 个人地址                           |
| bio            | 字符串          | —         | 否   | 个人介绍                           |
| tags           | 字符串数组      | —         | 否   | 个人标签                           |
| status         | 枚举            | {0,1}     | 是   | 账号状态：0禁用/1启用              |
| presenceStatus | 枚举            | {0,1,2,3} | 是   | 0离线/1在线/2异常/3注销            |
| roles          | 数组<RoleBrief> | —         | 否   | 用户拥有的角色                     |
| permissions    | 字符串数组      | —         | 否   | 扁平权限标识（如 `sys:user:list`） |
| createdAt      | 日期时间        | —         | 是   | 创建时间                           |

- 数据表映射：`sys_user`、`sys_user_tag`、`sys_user_role`、`sys_role`、`sys_role_permission`、`sys_permission`
- 权限标识（最小权限）：
  - `POST /auth/login`、`GET /auth/captcha`：匿名
  - `POST /auth/logout`、`GET /auth/me`：`sys:auth:session`

------

### 模块：个人资料（页面：个人中心）

- 操作：查看资料、更新资料、修改密码、上传头像、更新个人标签
- 接口：
  - `GET /api/profile` → `UserDto`
  - `PUT /api/profile` ← `UserProfileUpdateRequest` → `UserDto`
  - `PUT /api/profile/password` ← `ChangePasswordRequest`
  - `PUT /api/profile/avatar` ← `UploadAvatarRequest` → `{ avatarUrl }`
  - `PUT /api/profile/tags` ← `UpdateTagsRequest` → `{ tags }`
- 字段表（UserProfileUpdateRequest）：

| 名称     | 类型   | 长度      | 必填 | 说明     |
| -------- | ------ | --------- | ---- | -------- |
| name     | 字符串 | ≤100      | 否   | 姓名     |
| nickname | 字符串 | ≤100      | 否   | 昵称     |
| gender   | 枚举   | {0,1,2,9} | 否   | 性别     |
| email    | 字符串 | ≤255      | 否   | 邮箱     |
| phone    | 字符串 | ≤30       | 否   | 手机号   |
| address  | 字符串 | ≤255      | 否   | 个人地址 |
| bio      | 字符串 | —         | 否   | 个人介绍 |

- 字段表（ChangePasswordRequest）：

| 名称        | 类型   | 长度 | 必填 | 说明                       |
| ----------- | ------ | ---- | ---- | -------------------------- |
| oldPassword | 字符串 | ≤128 | 是   | 旧密码                     |
| newPassword | 字符串 | ≤128 | 是   | 新密码（服务端做强度校验） |

- 数据表映射：`sys_user`、`sys_user_tag`
- 权限标识：`sys:profile:read`（GET）、`sys:profile:update`、`sys:profile:password`、`sys:profile:avatar`、`sys:profile:tags`

------

## 用户管理（后台）

### 模块：用户管理（页面：系统设置/用户）

- 操作：分页查询、详情、创建、更新、删除、重置密码、修改状态、分配角色、导入/导出
- 接口：
  - `GET /api/admin/users` → `Page<UserListItem>`
  - `GET /api/admin/users/{id}` → `UserDetail`
  - `POST /api/admin/users` ← `UserCreateRequest` → `UserDetail`
  - `PUT /api/admin/users/{id}` ← `UserUpdateRequest` → `UserDetail`
  - `DELETE /api/admin/users/{id}`
  - `PUT /api/admin/users/{id}/status` ← `{ status }`
  - `PUT /api/admin/users/{id}/reset-password` ← `{ newPassword }`
  - `PUT /api/admin/users/{id}/roles` ← `SetUserRolesRequest`
  - `POST /api/admin/users/import` （文件上传：xlsx/csv）
  - `GET /api/admin/users/export` （导出xlsx）
- 字段表（查询参数，部分可选）：

| 名称           | 类型     | 长度      | 必填 | 说明                         |
| -------------- | -------- | --------- | ---- | ---------------------------- |
| username       | 字符串   | ≤64       | 否   | 模糊匹配                     |
| gender         | 枚举     | {0,1,2,9} | 否   | 性别                         |
| phone          | 字符串   | ≤30       | 否   | 手机号                       |
| presenceStatus | 枚举     | {0,1,2,3} | 否   | 在线状态                     |
| status         | 枚举     | {0,1}     | 否   | 账号状态                     |
| createdAtStart | 日期时间 | —         | 否   | 创建时间开始                 |
| createdAtEnd   | 日期时间 | —         | 否   | 创建时间结束                 |
| page           | 数字     | —         | 否   | 页码（默认1）                |
| size           | 数字     | —         | 否   | 页大小（默认20）             |
| sort           | 字符串   | —         | 否   | 排序字段，如`createdAt,desc` |

- 字段表（UserCreateRequest）：

| 名称     | 类型     | 长度      | 必填 | 说明      |
| -------- | -------- | --------- | ---- | --------- |
| username | 字符串   | ≤64       | 是   | 唯一      |
| password | 字符串   | ≤128      | 是   | 初始密码  |
| name     | 字符串   | ≤100      | 否   | 姓名      |
| nickname | 字符串   | ≤100      | 否   | 昵称      |
| gender   | 枚举     | {0,1,2,9} | 否   | 性别      |
| email    | 字符串   | ≤255      | 否   | 邮箱      |
| phone    | 字符串   | ≤30       | 否   | 手机号    |
| address  | 字符串   | ≤255      | 否   | 个人地址  |
| status   | 枚举     | {0,1}     | 否   | 默认1启用 |
| roleIds  | 整数数组 | —         | 否   | 初始角色  |

- 字段表（UserUpdateRequest）与 `UserCreateRequest` 相同但不含 `username/password`（或仅允许改邮箱/手机号等）。
- 字段表（UserListItem）：

| 名称           | 类型     | 说明                |
| -------------- | -------- | ------------------- |
| id             | 整数     | 用户ID              |
| username       | 字符串   | 用户名              |
| gender         | 枚举     | 性别                |
| phone          | 字符串   | 手机号              |
| presenceStatus | 枚举     | 注销/离线/异常/在线 |
| createdAt      | 日期时间 | 创建时间            |
| status         | 枚举     | 启用/禁用           |

- 数据表映射：`sys_user`、`sys_user_role`
- 权限标识：`sys:user:list`、`sys:user:read`、`sys:user:create`、`sys:user:update`、`sys:user:delete`、`sys:user:status`、`sys:user:resetpwd`、`sys:user:setroles`、`sys:user:import`、`sys:user:export`

------

## 角色管理（后台）

### 模块：角色管理（页面：系统设置/角色）

- 操作：分页查询、详情、创建、更新、删除、分配菜单、分配权限
- 接口：
  - `GET /api/admin/roles` → `Page<RoleItem>`
  - `GET /api/admin/roles/{id}` → `RoleDetail`
  - `POST /api/admin/roles` ← `RoleCreateRequest` → `RoleDetail`
  - `PUT /api/admin/roles/{id}` ← `RoleUpdateRequest` → `RoleDetail`
  - `DELETE /api/admin/roles/{id}`
  - `PUT /api/admin/roles/{id}/menus` ← `AssignRoleMenusRequest`
  - `PUT /api/admin/roles/{id}/permissions` ← `AssignRolePermissionsRequest`
- 字段表（RoleCreateRequest / RoleUpdateRequest）：

| 名称        | 类型   | 长度  | 必填 | 说明                           |
| ----------- | ------ | ----- | ---- | ------------------------------ |
| roleName    | 字符串 | ≤50   | 是   | 角色名称                       |
| roleCode    | 字符串 | ≤50   | 是   | 唯一英文编码（如 `sys_admin`） |
| description | 字符串 | ≤255  | 否   | 描述                           |
| status      | 枚举   | {0,1} | 否   | 启用/禁用                      |

- 字段表（AssignRoleMenusRequest）：

| 名称    | 类型     | 必填 | 说明                   |
| ------- | -------- | ---- | ---------------------- |
| menuIds | 整数数组 | 是   | 该角色可见的菜单ID集合 |

- 字段表（AssignRolePermissionsRequest）：

| 名称          | 类型     | 必填 | 说明                   |
| ------------- | -------- | ---- | ---------------------- |
| permissionIds | 整数数组 | 是   | 该角色可用的权限ID集合 |

- 数据表映射：`sys_role`、`sys_role_menu`、`sys_role_permission`
- 最小权限标识：`sys:role:list`、`sys:role:read`、`sys:role:create`、`sys:role:update`、`sys:role:delete`、`sys:role:setmenus`、`sys:role:setperms`

------

## 菜单管理（后台）

### 模块：菜单管理（页面：系统设置/菜单）

- 操作：树查询、详情、创建、更新、删除、排序、（可选）绑定动作权限
- 接口：
  - `GET /api/admin/menus/tree` → `MenuTree[]`
  - `GET /api/admin/menus/{id}` → `MenuDetail`
  - `POST /api/admin/menus` ← `MenuCreateRequest` → `MenuDetail`
  - `PUT /api/admin/menus/{id}` ← `MenuUpdateRequest` → `MenuDetail`
  - `DELETE /api/admin/menus/{id}`
  - `PUT /api/admin/menus/{id}/permissions` ← `BindMenuPermissionsRequest`  （可选）
- 字段表（MenuCreate/Update Request）：

| 名称           | 类型     | 长度    | 必填 | 说明                         |
| -------------- | -------- | ------- | ---- | ---------------------------- |
| parentId       | 整数或空 | —       | 否   | 父菜单ID，根为NULL           |
| menuType       | 枚举     | {1,2,3} | 是   | 1目录/2菜单/3按钮            |
| menuName       | 字符串   | ≤100    | 是   | 菜单名称                     |
| routePath      | 字符串   | ≤255    | 是   | 路由地址                     |
| componentPath  | 字符串   | ≤255    | 否   | 组件路径                     |
| permissionHint | 字符串   | ≤100    | 否   | 主要权限标识提示（前端显隐） |
| icon           | 字符串   | ≤100    | 否   | 图标名                       |
| useIconPicker  | 布尔     | —       | 否   | 图标选择器                   |
| orderNum       | 整数     | —       | 否   | 排序（升序）                 |
| externalLink   | 字符串   | ≤255    | 否   | 外部链接                     |
| badgeText      | 字符串   | ≤20     | 否   | 文本徽章                     |
| activePath     | 字符串   | ≤255    | 否   | 激活路径                     |
| enabled        | 布尔     | —       | 否   | 是否启用                     |
| cachePage      | 布尔     | —       | 否   | 页面缓存                     |
| hiddenMenu     | 布尔     | —       | 否   | 隐藏菜单                     |
| embedded       | 布尔     | —       | 否   | 是否内嵌（iframe）           |
| showBadge      | 布尔     | —       | 否   | 显示徽章                     |
| affix          | 布尔     | —       | 否   | 固定标签                     |
| hideTab        | 布尔     | —       | 否   | 标签隐藏                     |
| fullScreen     | 布尔     | —       | 否   | 全屏页面                     |

- 字段表（BindMenuPermissionsRequest）：

| 名称          | 类型     | 必填 | 说明                         |
| ------------- | -------- | ---- | ---------------------------- |
| permissionIds | 整数数组 | 是   | 该菜单可关联的动作权限ID集合 |

- 数据表映射：`sys_menu`、`sys_menu_permission`
- 最小权限标识：`sys:menu:tree`、`sys:menu:read`、`sys:menu:create`、`sys:menu:update`、`sys:menu:delete`、`sys:menu:bindperms`

------

## 权限（动作）管理（后台，可选暴露）

### 模块：权限管理（页面：系统设置/权限）

- 操作：列表、详情、创建、更新、删除
- 接口：
  - `GET /api/admin/permissions` → `Page<PermissionItem>`
  - `GET /api/admin/permissions/{id}` → `PermissionDetail`
  - `POST /api/admin/permissions` ← `PermissionCreateRequest`
  - `PUT /api/admin/permissions/{id}` ← `PermissionUpdateRequest`
  - `DELETE /api/admin/permissions/{id}`
- 字段表（PermissionCreate/Update Request）：

| 名称        | 类型   | 长度    | 必填 | 说明                                      |
| ----------- | ------ | ------- | ---- | ----------------------------------------- |
| permCode    | 字符串 | ≤100    | 是   | 权限标识（如 `sys:user:list`）            |
| permName    | 字符串 | ≤100    | 是   | 权限名称                                  |
| permType    | 枚举   | {1,2,3} | 否   | 1接口/2页面/3数据域                       |
| resource    | 字符串 | ≤100    | 否   | 资源名                                    |
| action      | 字符串 | ≤50     | 否   | 动作（list/create/update/delete/export…） |
| httpMethod  | 字符串 | ≤10     | 否   | GET/POST/PUT/DELETE                       |
| httpPath    | 字符串 | ≤255    | 否   | 接口路径（用于网关鉴权）                  |
| effect      | 枚举   | {1,2}   | 否   | 1允许/2拒绝                               |
| description | 字符串 | ≤255    | 否   | 描述                                      |

- 数据表映射：`sys_permission`
- 最小权限标识：`sys:perm:list`、`sys:perm:read`、`sys:perm:create`、`sys:perm:update`、`sys:perm:delete`

------

## 通用对象

### 分页（响应包装）

- `Page<T>`：

| 名称    | 类型    | 说明     |
| ------- | ------- | -------- |
| records | 数组<T> | 数据列表 |
| total   | 整数    | 总条数   |
| page    | 整数    | 当前页   |
| size    | 整数    | 每页大小 |

### 角色摘要（RoleBrief）

| 名称     | 类型   | 说明      |
| -------- | ------ | --------- |
| id       | 整数   | 角色ID    |
| roleName | 字符串 | 角色名称  |
| roleCode | 字符串 | 唯一编码  |
| status   | 枚举   | 启用/禁用 |

### 菜单树（MenuTree / MenuDetail）

| 名称           | 类型           | 说明              |
| -------------- | -------------- | ----------------- |
| id             | 整数           | 菜单ID            |
| parentId       | 整数/空        | 父ID              |
| menuType       | 枚举           | 1目录/2菜单/3按钮 |
| menuName       | 字符串         | 名称              |
| routePath      | 字符串         | 路由              |
| componentPath  | 字符串         | 组件              |
| permissionHint | 字符串         | 权限提示          |
| icon           | 字符串         | 图标              |
| useIconPicker  | 布尔           | 图标选择器        |
| orderNum       | 整数           | 排序              |
| externalLink   | 字符串         | 外链              |
| badgeText      | 字符串         | 徽章文字          |
| activePath     | 字符串         | 激活路径          |
| enabled        | 布尔           | 启用              |
| cachePage      | 布尔           | 缓存              |
| hiddenMenu     | 布尔           | 隐藏              |
| embedded       | 布尔           | 内嵌              |
| showBadge      | 布尔           | 展示徽章          |
| affix          | 布尔           | 固定标签          |
| hideTab        | 布尔           | 隐藏标签          |
| fullScreen     | 布尔           | 全屏              |
| children       | 数组<MenuTree> | 子节点            |

------

## 字典（枚举）对照（建议后端提供接口 `GET /api/dicts/{type}`）

- `gender`: 0未知/1男/2女/9其他
- `user_status`: 0禁用/1启用
- `presence_status`: 0离线/1在线/2异常/3注销
- `enable_disable`: 0禁用/1启用
- `perm_type`: 1接口/2页面/3数据域
- `allow_deny`: 1允许/2拒绝
- `menu_type`: 1目录/2菜单/3按钮

------

## 安全与最小权限建议

- 登录与验证码无需鉴权；其余接口统一 `Authorization: Bearer <token>`（推荐 JWT/Session+CSRF 双策略）。
- 角色默认**无**菜单、无权限；通过 `sys_role_menu` 与 `sys_role_permission` 精确赋权。
- 前端按钮显隐使用：
   1）`UserDto.permissions` 直接判断；或
   2）`Menu.permissionHint` + `MenuPermissions`（按钮绑定）两种方式协同。
- 审计：所有写操作记录 `createdBy/updatedBy`（可放在表字段或操作日志表）。
