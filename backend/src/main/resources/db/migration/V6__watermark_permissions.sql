-- 水印设置权限
INSERT INTO sys_permission (perm_code, perm_name, perm_type, resource, action)
VALUES ('sys:settings:watermark:update', '水印设置更新', 1, 'settings', 'watermark:update')
ON DUPLICATE KEY UPDATE perm_name=VALUES(perm_name);

INSERT INTO sys_permission (perm_code, perm_name, perm_type, resource, action)
VALUES ('sys:settings:watermark:read', '水印设置读取', 1, 'settings', 'watermark:read')
ON DUPLICATE KEY UPDATE perm_name=VALUES(perm_name);

-- 管理员授予权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1 AS role_id, p.id FROM sys_permission p WHERE p.perm_code IN ('sys:settings:watermark:update','sys:settings:watermark:read')
ON DUPLICATE KEY UPDATE role_id=role_id;