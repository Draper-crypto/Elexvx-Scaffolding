# Elexvx-Scaffolding
Elexvx Inc


# 启动项目

## 环境要求
- JDK 17，并将 `JAVA_HOME` 指向正确的 JDK 目录（Maven 运行必须）。
- Maven 3.9+（仓库脚本会自动调用 `mvn -s maven-settings.xml spring-boot:run`）。
- Node.js >= 20.19.0，推荐使用同版本的 npm（或 pnpm 8.8+）。
- 本地或远程 MySQL，默认连接 `jdbc:mysql://127.0.0.1:3306/art`（可自动建库）。

> 可选：如需预置数据，可执行 `datebase/db_art_schema_seed.sql`。

## 1. 启动后端（Spring Boot）

推荐使用仓库脚本，自动切换到 `art-design-server` 目录并注入常用环境变量：

macOS / Linux：

```bash
./scripts/start-backend.sh [profile] [dbUser] [dbPassword]
```

Windows PowerShell：

```powershell
.\scripts\start-backend.ps1 [-Profile dev] [-DbUser root] [-DbPassword ""]
```

- 默认使用 `dev` 配置文件，连接 `jdbc:mysql://127.0.0.1:3306/art`，数据库账号默认为 `root`（无密码）。
- 如需指定数据库密码等，可传入参数，例如：

```bash
./scripts/start-backend.sh dev root 151505
# 或
.\scripts\start-backend.ps1 -DbPassword 151505
```

启动完成后服务监听 `http://localhost:8080`，首次运行会自动建库/建表并写入初始化数据。
如需手动执行，可在 `art-design-server` 中运行 `mvn -s maven-settings.xml spring-boot:run` 并提前导出 `SPRING_PROFILES_ACTIVE`、`DB_USERNAME`、`DB_PASSWORD`。

## 2. 启动前端（Vite + Vue 3）

```bash
cd art-design-pro
npm install           # 首次安装依赖（建议 Node.js 20.19+）
npm run dev           # 启动开发服务器
```

默认监听 `http://localhost:3000`，端口被占用时会自动切换（实测自动改用 3001）。
开发模式下 `.env.development` 使用 `VITE_ACCESS_MODE=backend`，`/api` 请求会由 Vite 代理转发到 `http://localhost:8080`；请先启动后端脚本后再运行前端，确保接口可用。

默认管理员账号：`admin` / `admin123`（登录后可在系统管理中修改）。
