-- 初始化示例菜单数据（开发用）
-- 目录：仪表盘 / 系统设置；页面：控制台、用户管理、角色管理、菜单管理

USE `acc`;

-- 仪表盘（目录）
INSERT INTO `sys_menu` (
  `parent_id`, `menu_type`, `menu_name`, `route_path`, `component_path`,
  `permission_hint`, `icon`, `use_icon_picker`, `order_num`,
  `external_link`, `badge_text`, `active_path`,
  `enabled`, `cache_page`, `hidden_menu`, `embedded`, `show_badge`, `affix`, `hide_tab`, `full_screen`,
  `created_at`, `updated_at`
) VALUES (
  NULL, 1, '仪表盘', '/dashboard', NULL,
  NULL, 'Monitor', 0, 1,
  NULL, NULL, NULL,
  1, 0, 0, 0, 0, 0, 0, 0,
  NOW(), NOW()
);

SET @dashboard_id := (SELECT id FROM `sys_menu` WHERE `route_path` = '/dashboard' LIMIT 1);

-- 控制台（页面）
INSERT INTO `sys_menu` (
  `parent_id`, `menu_type`, `menu_name`, `route_path`, `component_path`,
  `permission_hint`, `icon`, `use_icon_picker`, `order_num`,
  `external_link`, `badge_text`, `active_path`,
  `enabled`, `cache_page`, `hidden_menu`, `embedded`, `show_badge`, `affix`, `hide_tab`, `full_screen`,
  `created_at`, `updated_at`
) VALUES (
  @dashboard_id, 2, '控制台', '/dashboard/console', '/dashboard/console',
  NULL, 'TrendCharts', 0, 1,
  NULL, NULL, NULL,
  1, 0, 0, 0, 0, 0, 0, 0,
  NOW(), NOW()
);

-- 系统设置（目录）
INSERT INTO `sys_menu` (
  `parent_id`, `menu_type`, `menu_name`, `route_path`, `component_path`,
  `permission_hint`, `icon`, `use_icon_picker`, `order_num`,
  `external_link`, `badge_text`, `active_path`,
  `enabled`, `cache_page`, `hidden_menu`, `embedded`, `show_badge`, `affix`, `hide_tab`, `full_screen`,
  `created_at`, `updated_at`
) VALUES (
  NULL, 1, '系统设置', '/system', NULL,
  NULL, 'Setting', 0, 2,
  NULL, NULL, NULL,
  1, 0, 0, 0, 0, 0, 0, 0,
  NOW(), NOW()
);

SET @system_id := (SELECT id FROM `sys_menu` WHERE `route_path` = '/system' LIMIT 1);

-- 用户管理（页面）
INSERT INTO `sys_menu` (
  `parent_id`, `menu_type`, `menu_name`, `route_path`, `component_path`,
  `permission_hint`, `icon`, `use_icon_picker`, `order_num`,
  `external_link`, `badge_text`, `active_path`,
  `enabled`, `cache_page`, `hidden_menu`, `embedded`, `show_badge`, `affix`, `hide_tab`, `full_screen`,
  `created_at`, `updated_at`
) VALUES (
  @system_id, 2, '用户管理', '/system/user', '/system/user',
  NULL, 'User', 0, 1,
  NULL, NULL, NULL,
  1, 0, 0, 0, 0, 0, 0, 0,
  NOW(), NOW()
);

-- 角色管理（页面）
INSERT INTO `sys_menu` (
  `parent_id`, `menu_type`, `menu_name`, `route_path`, `component_path`,
  `permission_hint`, `icon`, `use_icon_picker`, `order_num`,
  `external_link`, `badge_text`, `active_path`,
  `enabled`, `cache_page`, `hidden_menu`, `embedded`, `show_badge`, `affix`, `hide_tab`, `full_screen`,
  `created_at`, `updated_at`
) VALUES (
  @system_id, 2, '角色管理', '/system/role', '/system/role',
  NULL, 'UserFilled', 0, 2,
  NULL, NULL, NULL,
  1, 0, 0, 0, 0, 0, 0, 0,
  NOW(), NOW()
);

-- 菜单管理（页面）
INSERT INTO `sys_menu` (
  `parent_id`, `menu_type`, `menu_name`, `route_path`, `component_path`,
  `permission_hint`, `icon`, `use_icon_picker`, `order_num`,
  `external_link`, `badge_text`, `active_path`,
  `enabled`, `cache_page`, `hidden_menu`, `embedded`, `show_badge`, `affix`, `hide_tab`, `full_screen`,
  `created_at`, `updated_at`
) VALUES (
  @system_id, 2, '菜单管理', '/system/menu', '/system/menu',
  NULL, 'Menu', 0, 3,
  NULL, NULL, NULL,
  1, 0, 0, 0, 0, 0, 0, 0,
  NOW(), NOW()
);

