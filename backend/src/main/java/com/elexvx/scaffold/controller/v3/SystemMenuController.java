package com.elexvx.scaffold.controller.v3;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v3/system")
public class SystemMenuController {
    private final JdbcTemplate jdbc;

    public SystemMenuController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/menus")
    public List<Map<String, Object>> menus() {
        StpUtil.checkLogin();
        Long uid = StpUtil.getLoginIdAsLong();

        List<SysMenuRow> rows = jdbc.query(
                "SELECT id, parent_id AS parentId, menu_name AS menuName, route_path AS routePath, " +
                        "component_path AS componentPath, icon, permission_hint AS permissionHint, enabled " +
                        "FROM sys_menu m WHERE m.enabled = 1 AND (m.permission_hint IS NULL OR m.permission_hint IN (" +
                        "SELECT DISTINCT p.perm_code FROM sys_permission p " +
                        "JOIN sys_role_permission rp ON rp.permission_id = p.id " +
                        "JOIN sys_user_role ur ON ur.role_id = rp.role_id WHERE ur.user_id = ?)) " +
                        "ORDER BY parent_id, order_num, id",
                new BeanPropertyRowMapper<>(SysMenuRow.class), uid);
        Map<Long, Map<String, Object>> nodeMap = new HashMap<>();
        List<Map<String, Object>> roots = new ArrayList<>();
        for (SysMenuRow r : rows) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", r.getId());
            node.put("path", r.getRoutePath());
            node.put("component", r.getComponentPath());
            node.put("name", deriveName(r));
            Map<String, Object> meta = new HashMap<>();
            meta.put("title", r.getMenuName());
            meta.put("icon", r.getIcon());
            if (r.getPermissionHint() != null) meta.put("permissionHint", r.getPermissionHint());
            node.put("meta", meta);
            node.put("children", new ArrayList<>());
            nodeMap.put(r.getId(), node);
        }
        for (SysMenuRow r : rows) {
            Map<String, Object> node = nodeMap.get(r.getId());
            if (r.getParentId() == null) {
                Map<String, Object> nodeForRoot = node;
                nodeForRoot.put("component", "/index/index");
                roots.add(nodeForRoot);
            } else {
                Map<String, Object> parent = nodeMap.get(r.getParentId());
                if (parent != null) {
                    ((List<Map<String, Object>>) parent.get("children")).add(node);
                } else {
                    roots.add(node);
                }
            }
        }
        return dedupTree(roots);
    }

    private String deriveName(SysMenuRow r) {
        String path = r.getRoutePath();
        if (path == null || path.isBlank() || "/".equals(path)) {
            return "Root" + r.getId();
        }
        String p = path.replaceAll("^/+", "").replaceAll("/+", "_");
        String s = p.substring(0, 1).toUpperCase() + (p.length() > 1 ? p.substring(1) : "");
        return s;
    }

    private List<Map<String, Object>> dedupTree(List<Map<String, Object>> roots) {
        Map<String, Map<String, Object>> uniqueRoots = new HashMap<>();
        for (Map<String, Object> root : roots) {
            String key = buildKey(root);
            Map<String, Object> exist = uniqueRoots.get(key);
            if (exist == null) {
                uniqueRoots.put(key, root);
            } else {
                mergeChildren(exist, root);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>(uniqueRoots.values());
        for (Map<String, Object> r : result) {
            dedupChildren(r);
        }
        return result;
    }

    private void dedupChildren(Map<String, Object> node) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
        if (children == null || children.isEmpty()) return;
        Map<String, Map<String, Object>> unique = new HashMap<>();
        for (Map<String, Object> c : children) {
            String key = buildKey(c);
            Map<String, Object> exist = unique.get(key);
            if (exist == null) {
                unique.put(key, c);
            } else {
                mergeChildren(exist, c);
            }
        }
        List<Map<String, Object>> deduped = new ArrayList<>(unique.values());
        node.put("children", deduped);
        for (Map<String, Object> c : deduped) dedupChildren(c);
    }

    private void mergeChildren(Map<String, Object> target, Map<String, Object> source) {
        List<Map<String, Object>> tc = (List<Map<String, Object>>) target.get("children");
        List<Map<String, Object>> sc = (List<Map<String, Object>>) source.get("children");
        if (tc == null) {
            tc = new ArrayList<>();
            target.put("children", tc);
        }
        if (sc == null || sc.isEmpty()) return;
        tc.addAll(sc);
    }

    private String buildKey(Map<String, Object> node) {
        String path = String.valueOf(node.getOrDefault("path", ""));
        String comp = String.valueOf(node.getOrDefault("component", ""));
        return (path + "|" + comp).toLowerCase();
    }

    public static class SysMenuRow {
        private Long id;
        private Long parentId;
        private String menuName;
        private String routePath;
        private String componentPath;
        private String icon;
        private String permissionHint;
        private Integer enabled;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
        public String getMenuName() { return menuName; }
        public void setMenuName(String menuName) { this.menuName = menuName; }
        public String getRoutePath() { return routePath; }
        public void setRoutePath(String routePath) { this.routePath = routePath; }
        public String getComponentPath() { return componentPath; }
        public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getPermissionHint() { return permissionHint; }
        public void setPermissionHint(String permissionHint) { this.permissionHint = permissionHint; }
        public Integer getEnabled() { return enabled; }
        public void setEnabled(Integer enabled) { this.enabled = enabled; }
    }
}

