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
