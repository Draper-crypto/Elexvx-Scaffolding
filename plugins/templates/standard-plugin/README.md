# 标准插件模板

该模板用于快速创建新的业务插件，包含前后端及数据库脚手架。复制整个目录并重命名为目标插件的 key 即可开始开发。

## 目录结构

```
standard-plugin/
  ├── plugin.json               # 插件清单，描述元数据与入口
  ├── backend/                  # 后端扩展（Spring Boot/其他服务）
  │   └── README.md             # 约定的开发说明
  ├── frontend/                 # 前端扩展（Vue、React 等）
  │   └── README.md
  ├── database/
  │   └── migrations/
  │       └── 001_init.sql      # 数据库初始化脚本
  └── data/                     # 运行期数据目录（部署时系统自动创建）
```

> **注意：** `plugin.json` 中的 `key` 字段应与最终插件目录名称保持一致。
