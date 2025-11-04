-- MySQL 数据库建表 SQL（与 api.md 对应）

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password_hash VARCHAR(128) NOT NULL,
  name VARCHAR(100),
  nickname VARCHAR(100),
  gender TINYINT,
  email VARCHAR(255),
  phone VARCHAR(30),
  avatar_url VARCHAR(512),
  address VARCHAR(255),
  bio TEXT,
  status TINYINT NOT NULL DEFAULT 1,
  presence_status TINYINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_name VARCHAR(50) NOT NULL,
  role_code VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255),
  status TINYINT NOT NULL DEFAULT 1
);

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

CREATE TABLE IF NOT EXISTS sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT,
  menu_type TINYINT,
  menu_name VARCHAR(100) NOT NULL,
  route_path VARCHAR(255),
  route_name VARCHAR(100),
  component_path VARCHAR(255),
  permission_hint VARCHAR(100),
  icon VARCHAR(100),
  use_icon_picker TINYINT,
  order_num INT,
  external_link VARCHAR(255),
  badge_text VARCHAR(20),
  active_path VARCHAR(255),
  enabled TINYINT DEFAULT 1,
  cache_page TINYINT,
  hidden_menu TINYINT,
  embedded TINYINT,
  show_badge TINYINT,
  affix TINYINT,
  hide_tab TINYINT,
  full_screen TINYINT
);

CREATE TABLE IF NOT EXISTS sys_user_tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  tag VARCHAR(255) NOT NULL,
  CONSTRAINT fk_user_tag_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sys_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_role_perm_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
  CONSTRAINT fk_role_perm_perm FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sys_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, menu_id),
  CONSTRAINT fk_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
  CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id) ON DELETE CASCADE
);
