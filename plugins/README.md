# 插件包目录规范

该目录用于存放可插拔的业务插件包。默认约定：

- 每个插件以 `plugins/<插件标识>` 独立目录存在，目录名称建议与 manifest 中的 `key` 保持一致。
- 插件目录内至少包含 `plugin.json` 清单文件，以及按需提供的 `backend/`、`frontend/`、`database/` 子目录。
- 插件产生的运行数据应放入插件目录下的 `data/` 子目录，系统会在载入时自动创建。

```
plugins/
  ├── <plugin-key>/
  │   ├── plugin.json         # 插件清单（必填）
  │   ├── backend/            # 后端扩展代码（可选）
  │   ├── frontend/           # 前端扩展代码（可选）
  │   ├── database/           # 数据脚本，例如 migrations
  │   └── data/               # 运行期产生的数据（系统自动创建）
  └── templates/
      └── standard-plugin/    # 官方提供的规范模板
```

> **提示：** 可以复制 `templates/standard-plugin` 作为新插件的起始目录，并根据业务需求补充实现。
