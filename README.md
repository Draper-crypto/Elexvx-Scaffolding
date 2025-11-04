# Elexvx-Scaffolding
Elexvx Inc


# 启动项目
以下命令在 Windows PowerShell 执行，分别启动后端与前端。

后端（Spring Boot）：

1) 构建并启动 Jar（推荐）

```
cd art-design-server
mvn -s maven-settings.xml clean package -DskipTests
java -jar target/art-design-server-0.0.1-SNAPSHOT.jar
```

或直接运行（无需打包）：

```
cd art-design-server
mvn -s maven-settings.xml spring-boot:run
```

前端（Vite + Vue）：

```
cd art-design-pro
npm install
npm run dev
```

说明：
- 后端默认端口 `8080`，前端开发端口 `3006`。
- 开发模式 `.env.development` 使用 `VITE_ACCESS_MODE=backend`，前端会通过 `/api` 代理后端接口。
- 默认管理员账号：`admin` / `admin123`（首次登录后可在系统管理中修改）。
