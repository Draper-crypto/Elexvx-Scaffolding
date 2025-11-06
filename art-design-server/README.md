# JSJ 后端（art-design-server）

基于 Spring Boot + JPA + Security + JWT 的后端服务，实现与 `api.md` 对齐的接口与数据库结构。

## 快速开始

1. 安装 JDK 17 与 Maven（可选）。
2. 仅支持 MySQL 数据库，需确保本地或远程 MySQL 可用并具备初始化数据（可执行 `datebase/db_art_schema_seed.sql`）。
3. 运行：

```bash
mvn spring-boot:run
```

4. 可通过环境变量修改连接信息或激活 `dev` profile：

```bash
SPRING_DATASOURCE_URL="jdbc:mysql://127.0.0.1:3306/art?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8&createDatabaseIfNotExist=true" \
SPRING_DATASOURCE_USERNAME=root \
SPRING_DATASOURCE_PASSWORD=你的密码 \
mvn spring-boot:run
```

或：

```bash
SPRING_PROFILES_ACTIVE=dev \
DEV_DB_URL="jdbc:mysql://127.0.0.1:3306/art?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8&createDatabaseIfNotExist=true" \
DEV_DB_USERNAME=root \
DEV_DB_PASSWORD=你的密码 \
mvn spring-boot:run
```

或打包：

```bash
mvn -DskipTests package
java -jar target/art-design-server-0.0.1-SNAPSHOT.jar
```

## 数据库配置

默认使用 MySQL（`application.yml` 与 `application-dev.yml`）。

- 连接信息示例：`jdbc:mysql://127.0.0.1:3306/art?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8&createDatabaseIfNotExist=true`
- 可通过环境变量覆盖：`SPRING_DATASOURCE_URL`、`SPRING_DATASOURCE_USERNAME`、`SPRING_DATASOURCE_PASSWORD` 等（`dev` profile 使用 `DEV_` 前缀）。
- JPA `ddl-auto` 默认 `update`，也可通过环境变量调整或改为 `none` 并执行 `datebase/db_art_schema_seed.sql` 初始化结构。

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
