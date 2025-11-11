# Elexvx-Scaffolding
Elexvx Inc


# 启动项目
以下命令在 Windows PowerShell 执行，用于开发启动后端与前端。

目录结构：

```
Elexvx-Scaffolding/
├── frontend/   # Art Design Pro 前端（Vue3 + Vite）
└── backend/    # Spring Boot 后端（RESTful + sa-token）
```

前端（首次拉取后清理示例并启动开发）：

```
cd frontend
pnpm install
pnpm clean:dev   # 按提示输入 yes 确认清理
pnpm dev         # 默认 http://localhost:3006/
```

后端（开发模式运行）：

```
cd backend
mvn spring-boot:run
```

说明：
- 后端默认端口 `8080`，前端开发端口 `3006`。
- 数据库连接在 `backend/src/main/resources/application.properties` 已配置为 MySQL：`jdbc:mysql://localhost:3306/acc`，用户名：`root`，密码：`151505`。
- 项目遵循前后端分离；账户相关功能将使用 `sa-token` 鉴权（已集成依赖，后续接口开发时启用）。
- 所有后端依赖通过 Maven 管理，定义在 `backend/pom.xml`。

## 常见问题

（占位）后续根据具体业务补充常见问题与解决方案。
