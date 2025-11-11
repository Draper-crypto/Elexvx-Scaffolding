# Elexvx-Scaffolding
Elexvx Inc


# 启动项目
以下命令在 Windows PowerShell 执行，用于开发启动后端与前端。

后端（开发模式，Spring Boot 直接运行）：

```
cd acc-server
mvn spring-boot:run
```

前端（开发模式，Vite + Vue）：

```
cd art-design-pro
npm install
npm run dev
```

说明：
- 后端默认端口 `8080`，前端开发端口 `3006`。
- 数据库连接在 `acc-server/src/main/resources/application.properties` 已配置为 MySQL：`jdbc:mysql://localhost:3306/acc?createDatabaseIfNotExist=true`，用户名：`root`，密码：`151505`。
- 开发模式 `.env.development` 使用 `VITE_ACCESS_MODE=backend`，前端会通过 `/api` 代理后端接口。
- 默认管理员账号：`admin` / `admin123`（首次登录后可在系统管理中修改）。

## 常见问题

### 设置角色菜单时报 `Duplicate entry '<roleId>-<menuId>' for key 'sys_role_menu.uk_role_menu'`

原因：在同一事务内先删除旧绑定再插入新绑定时，如果删除未立即刷新到数据库，插入可能与旧记录的唯一索引 `(role_id, menu_id)` 冲突。

修复：后端在 `RoleService#setMenus` 中对 `deleteByRoleId` 后显式 `flush()`，并对前端提交的 `menuIds` 去重后批量保存，避免出现重复插入导致的唯一键冲突。
