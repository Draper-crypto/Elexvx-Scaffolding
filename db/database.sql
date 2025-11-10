/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : acc

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 10/11/2025 05:06:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_change_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_change_log`;
CREATE TABLE `sys_change_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `created_by` bigint NULL DEFAULT NULL,
  `release_date` date NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `require_relogin` int NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_change_log
-- ----------------------------
INSERT INTO `sys_change_log` VALUES (1, '<p style=\"text-align: right;\"><strong>1111111</strong></p><ul><li>555555</li></ul>', '2025-11-09 13:10:18.294733', 1, '2025-11-09', '', 0, '新版本', '2025-11-09 16:42:29.308991', 1, '1.0', 'aaa');

-- ----------------------------
-- Table structure for sys_global_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_global_setting`;
CREATE TABLE `sys_global_setting`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `created_by` bigint NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `setting_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `setting_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_hwspl727ai9nit9q1bskaf8qi`(`setting_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_global_setting
-- ----------------------------
INSERT INTO `sys_global_setting` VALUES (1, '2025-11-08 05:29:14.498837', 1, '品牌配置', 'brand.name', '{\"name\":\"Elexvx Acc\",\"logoUrl\":\"/src/assets/img/common/logo.webp\"}', '2025-11-09 19:18:05.311583', 1);
INSERT INTO `sys_global_setting` VALUES (2, '2025-11-08 05:29:18.150843', 1, '水印配置', 'ui.watermark', '{\"enabled\":true,\"mode\":\"username\",\"customText\":\"\",\"fontSize\":20}', '2025-11-09 19:16:36.068053', 1);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父菜单ID，根为NULL',
  `menu_type` int NULL DEFAULT NULL,
  `menu_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `route_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路由地址',
  `component_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `permission_hint` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识提示（前端显隐）',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标名',
  `use_icon_picker` int NULL DEFAULT NULL,
  `order_num` int NULL DEFAULT 0 COMMENT '排序（升序）',
  `external_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外部链接',
  `badge_text` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文本徽章',
  `active_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '激活路径',
  `enabled` int NULL DEFAULT NULL,
  `cache_page` int NULL DEFAULT NULL,
  `hidden_menu` int NULL DEFAULT NULL,
  `embedded` int NULL DEFAULT NULL,
  `show_badge` int NULL DEFAULT NULL,
  `affix` int NULL DEFAULT NULL,
  `hide_tab` int NULL DEFAULT NULL,
  `full_screen` int NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint UNSIGNED NULL DEFAULT NULL,
  `updated_by` bigint UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_menu_route_path`(`route_path` ASC) USING BTREE,
  INDEX `idx_menu_parent`(`parent_id` ASC) USING BTREE,
  INDEX `idx_menu_type`(`menu_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统菜单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, NULL, 2, '控制台', '/dashboard', '/index/index', '控制台', '', NULL, 1, '', '', '', 1, 0, 0, 0, 0, 0, 0, 0, '2025-11-07 20:55:06', '2025-11-09 20:36:28', NULL, NULL);
INSERT INTO `sys_menu` VALUES (2, 1, 2, '控制台', '/dashboard/console', '/dashboard/console', NULL, NULL, NULL, 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_menu` VALUES (3, NULL, 1, '系统设置', '/system', NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_menu` VALUES (4, 3, 2, '用户管理', '/system/user', '/system/user', NULL, NULL, NULL, 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_menu` VALUES (5, 3, 2, '角色管理', '/system/role', '/system/role', NULL, NULL, NULL, 2, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_menu` VALUES (6, 3, 2, '菜单管理', '/system/menu', '/system/menu', NULL, NULL, NULL, 3, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_menu` VALUES (7, NULL, 2, '更新日志', '/change/log', '/change/log', 'sys:changelog:list', '&#xe712;', 0, 99, NULL, NULL, '', 1, 0, 0, 0, 0, 0, 0, 0, '2025-11-09 21:03:16', '2025-11-09 21:03:16', NULL, NULL);
INSERT INTO `sys_menu` VALUES (8, 3, 2, '操作日志', '/system/operation-log', '/system/operation-log', 'sys:operation-log:list', '&#xe7c1;', 0, 80, NULL, NULL, NULL, 1, 1, 0, 0, 0, 0, 0, 0, '2025-11-09 21:03:16', '2025-11-09 21:03:16', NULL, NULL);
INSERT INTO `sys_menu` VALUES (9, 3, 2, '插件管理', '/system/plugins', '/system/plugin', 'sys:plugin:list', '&#xe857;', 0, 81, NULL, NULL, NULL, 1, 1, 0, 0, 0, 0, 0, 0, '2025-11-09 21:03:16', '2025-11-09 21:03:16', NULL, NULL);

-- ----------------------------
-- Table structure for sys_menu_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_permission`;
CREATE TABLE `sys_menu_permission`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `menu_id` bigint UNSIGNED NOT NULL,
  `permission_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_menu_perm`(`menu_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `idx_menu_perm_menu`(`menu_id` ASC) USING BTREE,
  INDEX `idx_menu_perm_perm`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `fk_menu_perm_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_menu_perm_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单-权限绑定' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu_permission
-- ----------------------------

-- Records of sys_page
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `perm_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限标识，如 sys:user:list',
  `perm_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `perm_type` int NULL DEFAULT NULL,
  `resource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资源名',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '动作',
  `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'GET/POST/PUT/DELETE',
  `http_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接口路径',
  `effect` int NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint UNSIGNED NULL DEFAULT NULL,
  `updated_by` bigint UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_perm_code`(`perm_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统权限' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, 'sys:user:list', 'sys:user:list', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (2, 'sys:user:read', 'sys:user:read', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (3, 'sys:user:create', 'sys:user:create', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (4, 'sys:user:update', 'sys:user:update', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (5, 'sys:user:delete', 'sys:user:delete', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (6, 'sys:user:status', 'sys:user:status', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (7, 'sys:user:resetpwd', 'sys:user:resetpwd', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (8, 'sys:user:setroles', 'sys:user:setroles', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (9, 'sys:user:import', 'sys:user:import', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (10, 'sys:user:export', 'sys:user:export', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (11, 'sys:role:list', 'sys:role:list', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (12, 'sys:role:read', 'sys:role:read', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (13, 'sys:role:create', 'sys:role:create', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (14, 'sys:role:update', 'sys:role:update', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (15, 'sys:role:delete', 'sys:role:delete', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (16, 'sys:role:setmenus', 'sys:role:setmenus', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (17, 'sys:role:setperms', 'sys:role:setperms', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (18, 'sys:menu:tree', 'sys:menu:tree', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (19, 'sys:menu:read', 'sys:menu:read', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (20, 'sys:menu:create', 'sys:menu:create', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (21, 'sys:menu:update', 'sys:menu:update', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (22, 'sys:menu:delete', 'sys:menu:delete', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (23, 'sys:menu:bindperms', 'sys:menu:bindperms', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-07 20:55:06', '2025-11-07 20:55:06', NULL, NULL);
INSERT INTO `sys_permission` VALUES (24, 'sys:operation-log:list', 'sys:operation-log:list', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-09 21:05:00', '2025-11-09 21:05:00', NULL, NULL);
INSERT INTO `sys_permission` VALUES (25, 'sys:plugin:list', 'sys:plugin:list', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-09 21:05:00', '2025-11-09 21:05:00', NULL, NULL);
INSERT INTO `sys_permission` VALUES (26, 'sys:plugin:load', 'sys:plugin:load', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-09 21:05:00', '2025-11-09 21:05:00', NULL, NULL);
INSERT INTO `sys_permission` VALUES (27, 'sys:plugin:unload', 'sys:plugin:unload', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-09 21:05:00', '2025-11-09 21:05:00', NULL, NULL);
INSERT INTO `sys_permission` VALUES (28, 'sys:setting:read', 'sys:setting:read', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-09 21:05:00', '2025-11-09 21:05:00', NULL, NULL);
INSERT INTO `sys_permission` VALUES (29, 'sys:setting:brand', 'sys:setting:brand', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-09 21:05:00', '2025-11-09 21:05:00', NULL, NULL);
INSERT INTO `sys_permission` VALUES (30, 'sys:setting:watermark', 'sys:setting:watermark', 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-09 21:05:00', '2025-11-09 21:05:00', NULL, NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码（唯一）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` int NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint UNSIGNED NULL DEFAULT NULL,
  `updated_by` bigint UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_role_code`(`role_code` ASC) USING BTREE,
  INDEX `idx_sys_role_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'sys_admin', NULL, 1, '2025-11-07 20:09:35', '2025-11-07 20:09:35', NULL, NULL);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id` bigint UNSIGNED NOT NULL,
  `menu_id` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE,
  INDEX `idx_role_menu_role`(`role_id` ASC) USING BTREE,
  INDEX `idx_role_menu_menu`(`menu_id` ASC) USING BTREE,
  CONSTRAINT `fk_role_menu_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_menu_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色-菜单绑定' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id` bigint UNSIGNED NOT NULL,
  `permission_id` bigint UNSIGNED NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_perm`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `idx_role_perm_role`(`role_id` ASC) USING BTREE,
  INDEX `idx_role_perm_perm`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `fk_role_perm_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_perm_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色-权限关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (2, 1, 2, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (3, 1, 3, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (4, 1, 4, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (5, 1, 5, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (6, 1, 6, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (7, 1, 7, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (8, 1, 8, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (9, 1, 9, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (10, 1, 10, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (11, 1, 11, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (12, 1, 12, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (13, 1, 13, '2025-11-08 04:55:05');
INSERT INTO `sys_role_permission` VALUES (14, 1, 14, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (15, 1, 15, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (16, 1, 16, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (17, 1, 17, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (18, 1, 18, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (19, 1, 19, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (20, 1, 20, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (21, 1, 21, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (22, 1, 22, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (23, 1, 23, '2025-11-08 04:55:06');
INSERT INTO `sys_role_permission` VALUES (24, 1, 24, '2025-11-09 21:05:00');
INSERT INTO `sys_role_permission` VALUES (25, 1, 25, '2025-11-09 21:05:00');
INSERT INTO `sys_role_permission` VALUES (26, 1, 26, '2025-11-09 21:05:00');
INSERT INTO `sys_role_permission` VALUES (27, 1, 27, '2025-11-09 21:05:00');
INSERT INTO `sys_role_permission` VALUES (28, 1, 28, '2025-11-09 21:05:00');
INSERT INTO `sys_role_permission` VALUES (29, 1, 29, '2025-11-09 21:05:00');
INSERT INTO `sys_role_permission` VALUES (30, 1, 30, '2025-11-09 21:05:00');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一登录名',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` int NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人地址',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人介绍',
  `status` int NULL DEFAULT NULL,
  `presence_status` int NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint UNSIGNED NULL DEFAULT NULL,
  `updated_by` bigint UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_username`(`username` ASC) USING BTREE,
  INDEX `idx_sys_user_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_sys_user_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_user_presence`(`presence_status` ASC) USING BTREE,
  INDEX `idx_sys_user_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$bXSkpMC/VjKEQG7Ku0K74OzUAgMddjRHTDJbGm4NL/N47S4W/cJxK', '啊啊啊', '啊啊啊', 1, 'aa', '15150578099', '/uploads/avatar/f315f1e1-3735-4b5a-a5b2-0ec6e6cd3433.jpg', '啊啊啊啊', '测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试', 1, 0, '2025-11-07 19:41:08', '2025-11-09 19:30:10', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint UNSIGNED NOT NULL,
  `role_id` bigint UNSIGNED NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_role_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_role_role`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-角色关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (10, 1, 1, '2025-11-09 23:16:01');

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `action_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `action_summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `action_detail` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `request_params` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ip_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `color_hex` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tag_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `success_flag` tinyint NULL DEFAULT NULL,
  `error_message` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_log_type`(`action_type` ASC) USING BTREE,
  INDEX `idx_operation_log_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_plugin
-- ----------------------------
DROP TABLE IF EXISTS `sys_plugin`;
CREATE TABLE `sys_plugin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plugin_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `plugin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `plugin_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `manifest_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `backend_entry` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `frontend_entry` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `database_scripts` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `data_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  `installed_at` datetime NULL DEFAULT NULL,
  `last_loaded_at` datetime NULL DEFAULT NULL,
  `last_unloaded_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_plugin_key`(`plugin_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
