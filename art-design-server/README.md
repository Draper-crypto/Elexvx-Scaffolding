# JSJ 后端（art-design-server）

基于 Spring Boot + JPA + Security + JWT 的后端服务，实现与 `api.md` 对齐的接口与数据库结构。

## 快速开始

1. 安装 JDK 17 与 Maven（可选）。
2. 开发模式默认使用内存数据库 H2，直接运行即可。
3. 运行：

```bash
mvn spring-boot:run
```

或打包：

```bash
mvn -DskipTests package
java -jar target/art-design-server-0.0.1-SNAPSHOT.jar
```

## 数据库配置

默认使用 H2（`application.yml`）。若要使用 MySQL：

- 在 `src/main/resources/application.yml` 中替换为 MySQL 连接信息（已给出示例），并将 `ddl-auto` 设为 `none`。
- 在数据库中执行 `src/main/resources/schema-mysql.sql` 建表。

## 已实现接口

- 认证模块：
  - `GET /api/auth/captcha` → 生成验证码（180 秒有效）。
  - `POST /api/auth/login` ← 验证码可选，登录成功返回 `token` 与 `user`。
  - `POST /api/auth/logout` → 前端删除 token（可扩展黑名单）。
  - `GET /api/auth/me` → 返回当前用户信息。
- 字典模块：
  - `GET /api/dicts/{type}` → 返回枚举项（gender、user_status、presence_status、menu_type、perm_type、allow_deny）。

## 默认账号

- 初始化创建：用户名 `admin`，密码 `admin123`，角色 `sys_admin`，基础权限 `sys:auth:session`。

## 后续计划（按需扩展）

- 个人资料模块（profile）与后台管理模块（用户、角色、菜单、权限）接口实现。
- 导入导出、角色菜单/权限分配、菜单树查询等。

