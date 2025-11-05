-- =============================================================
--  Project: Elexvx-Scaffolding / art-design-server
--  Purpose: MySQL schema & seed data (DB + Tables + Base rows)
--  Usage:   SOURCE this file in MySQL client to initialize
-- =============================================================

-- Optional: ensure proper charset for this session
SET NAMES utf8mb4;

-- -------------------------------------------------------------
-- Create database
-- -------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS `art`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE `art`;

-- -------------------------------------------------------------
-- Drop existing tables (optional, dangerous in production)
-- Uncomment if you need a clean re-import
-- -------------------------------------------------------------
-- SET FOREIGN_KEY_CHECKS=0;
-- DROP TABLE IF EXISTS sys_role_menu, sys_user_role, sys_role_permission,
--   sys_user_tag, sys_menu, sys_permission, sys_role, sys_user;
-- SET FOREIGN_KEY_CHECKS=1;

-- -------------------------------------------------------------
-- sys_user (users)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL,
  `passwordHash` VARCHAR(128) NOT NULL,
  `name` VARCHAR(100),
  `nickname` VARCHAR(100),
  `gender` INT,
  `email` VARCHAR(255),
  `phone` VARCHAR(30),
  `avatarUrl` VARCHAR(512),
  `address` VARCHAR(255),
  `bio` TEXT,
  `status` INT NOT NULL DEFAULT 1,           -- 0禁用/1启用
  `presenceStatus` INT NOT NULL DEFAULT 0,   -- 0离线/1在线/2异常/3注销
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `idx_sys_user_username` (`username`),
  UNIQUE KEY `uk_sys_user_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- sys_role (roles)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `roleName` VARCHAR(50) NOT NULL,
  `roleCode` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255),
  `status` INT NOT NULL DEFAULT 1,
  UNIQUE KEY `idx_sys_role_code` (`roleCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- sys_permission (permissions)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `permCode` VARCHAR(100) NOT NULL,
  `permName` VARCHAR(100) NOT NULL,
  `permType` INT,                 -- 1接口/2页面/3数据域
  `resource` VARCHAR(100),
  `action` VARCHAR(50),
  `httpMethod` VARCHAR(10),
  `httpPath` VARCHAR(255),
  `effect` INT,                   -- 1允许/2拒绝
  `description` VARCHAR(255),
  UNIQUE KEY `idx_sys_permission_code` (`permCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- sys_menu (menus/routes/buttons)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `parentId` BIGINT NULL,
  `menuType` INT,                    -- 1目录/2菜单/3按钮
  `menuName` VARCHAR(100) NOT NULL,
  `routePath` VARCHAR(255),
  `routeName` VARCHAR(100),
  `componentPath` VARCHAR(255),
  `permissionHint` VARCHAR(100),
  `icon` VARCHAR(100),
  `useIconPicker` TINYINT(1),
  `orderNum` INT,
  `externalLink` VARCHAR(255),
  `badgeText` VARCHAR(20),
  `activePath` VARCHAR(255),
  `enabled` TINYINT(1) DEFAULT 1,
  `cachePage` TINYINT(1),
  `hiddenMenu` TINYINT(1),
  `embedded` TINYINT(1),
  `showBadge` TINYINT(1),
  `affix` TINYINT(1),
  `hideTab` TINYINT(1),
  `fullScreen` TINYINT(1),
  KEY `idx_menu_parent` (`parentId`),
  CONSTRAINT `fk_menu_parent` FOREIGN KEY (`parentId`) REFERENCES `sys_menu`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Join: sys_user_role (users <-> roles)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Join: sys_role_permission (roles <-> permissions)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rp_perm` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Join: sys_role_menu (roles <-> menus)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
  `role_id` BIGINT NOT NULL,
  `menu_id` BIGINT NOT NULL,
  PRIMARY KEY (`role_id`,`menu_id`),
  CONSTRAINT `fk_rm_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rm_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- sys_user_tag (user tags)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user_tag` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT,
  `tag` VARCHAR(255) NOT NULL,
  KEY `idx_user_tag_user` (`user_id`),
  CONSTRAINT `fk_user_tag_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- Seed data
-- =============================================================
START TRANSACTION;

-- Permissions
INSERT INTO `sys_permission` (`permCode`,`permName`,`permType`,`effect`)
VALUES 
  ('sys:menu:add','新增',2,1),
  ('sys:menu:edit','编辑',2,1),
  ('sys:menu:delete','删除',2,1)
ON DUPLICATE KEY UPDATE
  `permName`=VALUES(`permName`),
  `permType`=VALUES(`permType`),
  `effect`=VALUES(`effect`);

-- Roles
INSERT INTO `sys_role` (`roleName`,`roleCode`,`description`,`status`) VALUES
  ('超级管理员','R_SUPER','拥有所有权限',1),
  ('管理员','R_ADMIN','管理后台',1),
  ('普通用户','R_USER','普通用户角色',1)
ON DUPLICATE KEY UPDATE
  `roleName`=VALUES(`roleName`),
  `description`=VALUES(`description`),
  `status`=VALUES(`status`);

-- Grant example permissions to R_SUPER
INSERT INTO `sys_role_permission` (`role_id`,`permission_id`)
SELECT r.id, p.id
FROM `sys_role` r JOIN `sys_permission` p
WHERE r.roleCode='R_SUPER' AND p.permCode IN ('sys:menu:add','sys:menu:edit','sys:menu:delete')
ON DUPLICATE KEY UPDATE role_id=role_id;

-- Admin user: admin / password
-- This bcrypt hash corresponds to the literal string "password" (cost 12)
INSERT INTO `sys_user` (`username`,`passwordHash`,`name`,`status`,`presenceStatus`)
VALUES
  ('admin','$2a$12$NT0I31Sa7ihGEWpka9ASYrEFkhuTNeBQ2xfZskIiiJeyFXhRgS.Sy','管理员',1,1)
ON DUPLICATE KEY UPDATE
  `name`=VALUES(`name`),
  `status`=VALUES(`status`),
  `presenceStatus`=VALUES(`presenceStatus`);

-- Bind roles to admin (R_SUPER, R_ADMIN)
INSERT INTO `sys_user_role` (`user_id`,`role_id`)
SELECT u.id, r.id
FROM `sys_user` u JOIN `sys_role` r
WHERE u.username='admin' AND r.roleCode IN ('R_SUPER','R_ADMIN')
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Optional: minimal example menus (commented out)
-- INSERT INTO `sys_menu` (`parentId`,`menuType`,`menuName`,`routePath`,`routeName`,`componentPath`,`icon`,`orderNum`,`enabled`)
-- VALUES
--   (NULL,1,'系统管理','/system','System','Layout','ion:settings-outline',1,1);

COMMIT;

-- =============================================================
-- End of file
-- =============================================================

