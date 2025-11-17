-- =====================================
-- 初始化表结构
-- =====================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  name VARCHAR(100),
  nickname VARCHAR(100),
  gender TINYINT,
  email VARCHAR(255),
  phone VARCHAR(30),
  avatar_url VARCHAR(512),
  address VARCHAR(255),
  bio TEXT,
  tags JSON,
  status TINYINT DEFAULT 1,
  presence_status TINYINT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_name VARCHAR(50) NOT NULL,
  role_code VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255),
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  perm_code VARCHAR(100) NOT NULL UNIQUE,
  perm_name VARCHAR(100) NOT NULL,
  perm_type TINYINT,
  resource VARCHAR(100),
  action VARCHAR(50),
  http_method VARCHAR(10),
  http_path VARCHAR(255),
  effect TINYINT,
  description VARCHAR(255)
);

-- 用户角色关联
CREATE TABLE IF NOT EXISTS sys_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  UNIQUE KEY uk_user_role (user_id, role_id)
);

-- 角色权限关联
CREATE TABLE IF NOT EXISTS sys_role_permission (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  UNIQUE KEY uk_role_perm (role_id, permission_id)
);

-- 菜单表（基础结构）
CREATE TABLE IF NOT EXISTS sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NULL,
  menu_type TINYINT,
  menu_name VARCHAR(100) NOT NULL,
  route_path VARCHAR(255) NOT NULL,
  component_path VARCHAR(255),
  permission_hint VARCHAR(100),
  icon VARCHAR(100),
  order_num INT,
  external_link VARCHAR(255),
  badge_text VARCHAR(20),
  active_path VARCHAR(255),
  enabled TINYINT DEFAULT 1,
  cache_page TINYINT DEFAULT 0,
  hidden_menu TINYINT DEFAULT 0,
  embedded TINYINT DEFAULT 0,
  show_badge TINYINT DEFAULT 0,
  affix TINYINT DEFAULT 0,
  hide_tab TINYINT DEFAULT 0,
  full_screen TINYINT DEFAULT 0
);

-- 角色菜单关联
CREATE TABLE IF NOT EXISTS sys_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  UNIQUE KEY uk_role_menu (role_id, menu_id)
);

-- 操作日志
CREATE TABLE IF NOT EXISTS sys_oper_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  action VARCHAR(255),
  operator VARCHAR(64),
  request_uri VARCHAR(255),
  method VARCHAR(10),
  ip VARCHAR(64),
  details TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 系统参数表
CREATE TABLE IF NOT EXISTS sys_param (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  param_key VARCHAR(100) NOT NULL UNIQUE,
  param_value VARCHAR(1000) NULL
);

-- =====================================
-- 基础数据初始化
-- =====================================

INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('管理员', 'ADMIN', '系统管理员', 1),
('普通用户', 'USER', '普通用户角色', 1);

INSERT INTO sys_permission (perm_code, perm_name, perm_type, resource, action) VALUES
('sys:auth:session','会话访问',1,'auth','session'),
('sys:user:list','用户列表',1,'user','list'),
('sys:user:read','用户详情',1,'user','read'),
('sys:user:create','用户创建',1,'user','create'),
('sys:user:update','用户更新',1,'user','update'),
('sys:user:delete','用户删除',1,'user','delete'),
('sys:user:status','用户状态',1,'user','status'),
('sys:user:resetpwd','用户重置密码',1,'user','resetpwd'),
('sys:user:setroles','用户设置角色',1,'user','setroles'),
('sys:role:list','角色列表',1,'role','list'),
('sys:role:read','角色详情',1,'role','read'),
('sys:role:create','角色创建',1,'role','create'),
('sys:role:update','角色更新',1,'role','update'),
('sys:role:delete','角色删除',1,'role','delete'),
('sys:role:setmenus','角色设置菜单',1,'role','setmenus'),
('sys:role:setperms','角色设置权限',1,'role','setperms');

-- 角色权限关联：管理员拥有全部权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1 AS role_id, p.id FROM sys_permission p;

-- 普通用户拥有部分只读权限（示例）
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2 AS role_id, p.id FROM sys_permission p WHERE p.perm_code IN ('sys:user:list','sys:role:list');

-- 菜单示例
INSERT INTO sys_menu (parent_id, menu_type, menu_name, route_path, component_path, permission_hint, icon, order_num, enabled)
VALUES
(NULL, 1, '系统设置', '/', NULL, NULL, 'SettingOutlined', 1, 1),
(1, 2, '用户管理', '/users', NULL, 'sys:user:list', 'UserOutlined', 2, 1),
(1, 2, '角色管理', '/roles', NULL, 'sys:role:list', 'TeamOutlined', 3, 1),
(1, 2, '权限管理', '/permissions', NULL, 'sys:perm:list', 'SafetyOutlined', 4, 1);

-- 默认用户数据（密码为 "123456" 的 BCrypt 哈希）
INSERT INTO sys_user (username, password_hash, name, status)
VALUES
('Super', '$2a$10$7EqJtq98hPqEX7fNLEG5OuE7h.uGcLWXh0qzQfM8hPvLT3T1/4A0e', '超级管理员', 1),
('Admin', '$2a$10$7EqJtq98hPqEX7fNLEG5OuE7h.uGcLWXh0qzQfM8hPvLT3T1/4A0e', '管理员', 1),
('User', '$2a$10$7EqJtq98hPqEX7fNLEG5OuE7h.uGcLWXh0qzQfM8hPvLT3T1/4A0e', '普通用户', 1);

-- 用户角色绑定
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, 1 FROM sys_user u WHERE u.username IN ('Super','Admin');

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, 2 FROM sys_user u WHERE u.username = 'User';

-- =====================================
-- 系统参数与扩展权限
-- =====================================

INSERT INTO sys_param(param_key,param_value) VALUES('watermark.mode','username')
ON DUPLICATE KEY UPDATE param_value=VALUES(param_value);

INSERT INTO sys_param(param_key,param_value) VALUES('watermark.custom','')
ON DUPLICATE KEY UPDATE param_value=VALUES(param_value);

-- 水印设置权限
INSERT INTO sys_permission (perm_code, perm_name, perm_type, resource, action)
VALUES ('sys:settings:watermark:update', '水印设置更新', 1, 'settings', 'watermark:update')
ON DUPLICATE KEY UPDATE perm_name=VALUES(perm_name);

INSERT INTO sys_permission (perm_code, perm_name, perm_type, resource, action)
VALUES ('sys:settings:watermark:read', '水印设置读取', 1, 'settings', 'watermark:read')
ON DUPLICATE KEY UPDATE perm_name=VALUES(perm_name);

-- 管理员授予水印权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1 AS role_id, p.id FROM sys_permission p WHERE p.perm_code IN ('sys:settings:watermark:update','sys:settings:watermark:read')
ON DUPLICATE KEY UPDATE role_id=role_id;

-- =====================================
-- 索引优化
-- =====================================

CREATE INDEX IF NOT EXISTS idx_sys_permission_perm_code ON sys_permission (perm_code);
CREATE INDEX IF NOT EXISTS idx_sys_menu_perm_hint ON sys_menu (permission_hint);
CREATE INDEX IF NOT EXISTS idx_user_role_user ON sys_user_role (user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_role ON sys_user_role (role_id);
CREATE INDEX IF NOT EXISTS idx_role_perm_role ON sys_role_permission (role_id);
CREATE INDEX IF NOT EXISTS idx_role_perm_perm ON sys_role_permission (permission_id);
