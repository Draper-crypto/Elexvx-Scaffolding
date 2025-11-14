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
