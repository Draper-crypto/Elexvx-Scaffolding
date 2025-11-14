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

