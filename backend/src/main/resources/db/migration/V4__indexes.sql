-- 索引优化：提升菜单与权限聚合查询性能

-- 权限码查询
CREATE INDEX IF NOT EXISTS idx_sys_permission_perm_code ON sys_permission (perm_code);

-- 菜单权限提示
CREATE INDEX IF NOT EXISTS idx_sys_menu_perm_hint ON sys_menu (permission_hint);

-- 用户角色关联
CREATE INDEX IF NOT EXISTS idx_user_role_user ON sys_user_role (user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_role ON sys_user_role (role_id);

-- 角色权限关联
CREATE INDEX IF NOT EXISTS idx_role_perm_role ON sys_role_permission (role_id);
CREATE INDEX IF NOT EXISTS idx_role_perm_perm ON sys_role_permission (permission_id);