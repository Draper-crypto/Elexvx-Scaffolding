-- Database initialization for User Management
-- Target DB: acc (per project README and custom instructions)

CREATE DATABASE IF NOT EXISTS `acc`
  /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
USE `acc`;

-- -----------------------------------------------------
-- Table: sys_user
-- 用户基础信息，参考 A.md 字段规范（用户名≤64、邮箱≤255、手机号≤30 等）
-- 审计：created_at/updated_at、created_by/updated_by
-- presence_status: 0离线/1在线/2异常/3注销；status: 0禁用/1启用
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL COMMENT '唯一登录名',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `name` VARCHAR(100) DEFAULT NULL COMMENT '姓名',
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
  `gender` TINYINT DEFAULT 0 COMMENT '0未知/1男/2女/9其他',
  `email` VARCHAR(255) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(30) DEFAULT NULL COMMENT '手机号',
  `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '个人地址',
  `bio` TEXT DEFAULT NULL COMMENT '个人介绍',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：0禁用/1启用',
  `presence_status` TINYINT NOT NULL DEFAULT 0 COMMENT '在线状态：0离线/1在线/2异常/3注销',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT UNSIGNED DEFAULT NULL,
  `updated_by` BIGINT UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  KEY `idx_sys_user_phone` (`phone`),
  KEY `idx_sys_user_status` (`status`),
  KEY `idx_sys_user_presence` (`presence_status`),
  KEY `idx_sys_user_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户';

-- -----------------------------------------------------
-- Table: sys_user_tag
-- 用户标签，一用户多标签；用于个人中心与展示
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user_tag` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `tag` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`, `tag`),
  CONSTRAINT `fk_user_tag_user`
    FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户标签';

-- -----------------------------------------------------
-- Table: sys_role
-- 角色，参考 A.md：roleName≤50、roleCode≤50 唯一、status {0,1}
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码（唯一）',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  `status` TINYINT DEFAULT 1 COMMENT '启用状态：0禁用/1启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT UNSIGNED DEFAULT NULL,
  `updated_by` BIGINT UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`),
  KEY `idx_sys_role_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色';

-- -----------------------------------------------------
-- Table: sys_user_role
-- 用户与角色关联表
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `role_id` BIGINT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_role_user` (`user_id`),
  KEY `idx_user_role_role` (`role_id`),
  CONSTRAINT `fk_user_role_user`
    FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_role`
    FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-角色关联';

-- -----------------------------------------------------
-- Table: sys_permission
-- 权限（动作），参考 A.md 字段：permCode/permName/permType/resource/action/httpMethod/httpPath/effect/description
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `perm_code` VARCHAR(100) NOT NULL COMMENT '权限标识，如 sys:user:list',
  `perm_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
  `perm_type` TINYINT DEFAULT NULL COMMENT '类型：1接口/2页面/3数据域',
  `resource` VARCHAR(100) DEFAULT NULL COMMENT '资源名',
  `action` VARCHAR(50) DEFAULT NULL COMMENT '动作',
  `http_method` VARCHAR(10) DEFAULT NULL COMMENT 'GET/POST/PUT/DELETE',
  `http_path` VARCHAR(255) DEFAULT NULL COMMENT '接口路径',
  `effect` TINYINT DEFAULT NULL COMMENT '效果：1允许/2拒绝',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT UNSIGNED DEFAULT NULL,
  `updated_by` BIGINT UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限';

-- -----------------------------------------------------
-- Table: sys_role_permission
-- 角色-权限关联
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT UNSIGNED NOT NULL,
  `permission_id` BIGINT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`),
  KEY `idx_role_perm_role` (`role_id`),
  KEY `idx_role_perm_perm` (`permission_id`),
  CONSTRAINT `fk_role_perm_role`
    FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_perm_permission`
    FOREIGN KEY (`permission_id`) REFERENCES `sys_permission`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-权限关联';

-- -----------------------------------------------------
-- Table: sys_menu
-- 菜单，参考 A.md MenuCreate/Update 字段
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '父菜单ID，根为NULL',
  `menu_type` TINYINT NOT NULL COMMENT '1目录/2菜单/3按钮',
  `menu_name` VARCHAR(100) NOT NULL COMMENT '菜单名称',
  `route_path` VARCHAR(255) NOT NULL COMMENT '路由地址',
  `component_path` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `permission_hint` VARCHAR(100) DEFAULT NULL COMMENT '权限标识提示（前端显隐）',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标名',
  `use_icon_picker` TINYINT DEFAULT 0 COMMENT '是否使用图标选择器',
  `order_num` INT DEFAULT 0 COMMENT '排序（升序）',
  `external_link` VARCHAR(255) DEFAULT NULL COMMENT '外部链接',
  `badge_text` VARCHAR(20) DEFAULT NULL COMMENT '文本徽章',
  `active_path` VARCHAR(255) DEFAULT NULL COMMENT '激活路径',
  `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  `cache_page` TINYINT DEFAULT 0 COMMENT '页面缓存',
  `hidden_menu` TINYINT DEFAULT 0 COMMENT '隐藏菜单',
  `embedded` TINYINT DEFAULT 0 COMMENT '是否内嵌（iframe）',
  `show_badge` TINYINT DEFAULT 0 COMMENT '显示徽章',
  `affix` TINYINT DEFAULT 0 COMMENT '固定标签',
  `hide_tab` TINYINT DEFAULT 0 COMMENT '标签隐藏',
  `full_screen` TINYINT DEFAULT 0 COMMENT '全屏页面',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT UNSIGNED DEFAULT NULL,
  `updated_by` BIGINT UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_menu_parent` (`parent_id`),
  KEY `idx_menu_type` (`menu_type`),
  UNIQUE KEY `uk_menu_route_path` (`route_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统菜单';

-- -----------------------------------------------------
-- Table: sys_menu_permission
-- 菜单-权限绑定（可选）
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_menu_permission` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `menu_id` BIGINT UNSIGNED NOT NULL,
  `permission_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_perm` (`menu_id`, `permission_id`),
  KEY `idx_menu_perm_menu` (`menu_id`),
  KEY `idx_menu_perm_perm` (`permission_id`),
  CONSTRAINT `fk_menu_perm_menu`
    FOREIGN KEY (`menu_id`) REFERENCES `sys_menu`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_menu_perm_permission`
    FOREIGN KEY (`permission_id`) REFERENCES `sys_permission`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单-权限绑定';

-- -----------------------------------------------------
-- Table: sys_role_menu
-- 角色-菜单可见性绑定
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT UNSIGNED NOT NULL,
  `menu_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  KEY `idx_role_menu_role` (`role_id`),
  KEY `idx_role_menu_menu` (`menu_id`),
  CONSTRAINT `fk_role_menu_role`
    FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_menu_menu`
    FOREIGN KEY (`menu_id`) REFERENCES `sys_menu`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-菜单绑定';

-- -----------------------------------------------------
-- Table: sys_page
-- 页面管理（独立管理页面元数据，可与菜单分离；菜单可引用 route/component）
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_page` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `page_name` VARCHAR(100) NOT NULL COMMENT '页面名称',
  `route_path` VARCHAR(255) NOT NULL COMMENT '路由地址',
  `component_path` VARCHAR(255) NOT NULL COMMENT '组件路径',
  `permission_hint` VARCHAR(100) DEFAULT NULL COMMENT '权限标识提示',
  `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  `cache_page` TINYINT DEFAULT 0 COMMENT '页面缓存',
  `embedded` TINYINT DEFAULT 0 COMMENT '是否内嵌（iframe）',
  `full_screen` TINYINT DEFAULT 0 COMMENT '全屏页面',
  `hide_tab` TINYINT DEFAULT 0 COMMENT '隐藏标签',
  `affix` TINYINT DEFAULT 0 COMMENT '固定标签',
  `active_path` VARCHAR(255) DEFAULT NULL COMMENT '激活路径',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT UNSIGNED DEFAULT NULL,
  `updated_by` BIGINT UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_page_route_path` (`route_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='页面管理';

-- Suggested seed data (optional): create base roles/permissions
-- INSERT statements can be added here when requirements are finalized.

-- -----------------------------------------------------
-- Table: sys_change_log
-- 存储系统更新日志
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_change_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `version` VARCHAR(50) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `content` LONGTEXT,
  `release_date` DATE DEFAULT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `require_relogin` TINYINT DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT UNSIGNED DEFAULT NULL,
  `updated_by` BIGINT UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_change_release` (`release_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统更新日志';

-- -----------------------------------------------------
-- Table: sys_global_setting
-- 全局设置（JSON）
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_global_setting` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `setting_key` VARCHAR(100) NOT NULL,
  `setting_value` TEXT,
  `description` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT UNSIGNED DEFAULT NULL,
  `updated_by` BIGINT UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_setting_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统全局设置';

-- 默认品牌与水印配置
INSERT INTO `sys_global_setting` (`setting_key`, `setting_value`, `description`)
VALUES
  ('brand.name', JSON_OBJECT('name', 'Art Design Pro'), '品牌配置'),
  ('ui.watermark', JSON_OBJECT('enabled', TRUE, 'mode', 'username', 'customText', '', 'fontSize', 16), '水印配置')
ON DUPLICATE KEY UPDATE
  `setting_value` = VALUES(`setting_value`),
  `description` = VALUES(`description`);

