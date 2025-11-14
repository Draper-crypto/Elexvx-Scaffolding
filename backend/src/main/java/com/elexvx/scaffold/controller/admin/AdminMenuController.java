package com.elexvx.scaffold.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.elexvx.scaffold.entity.SysMenu;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/menus")
public class AdminMenuController {
    private final JdbcTemplate jdbc;
    public AdminMenuController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/tree")
    @SaCheckPermission("sys:menu:tree")
    public List<Map<String, Object>> tree() {
        List<SysMenu> all = jdbc.query("SELECT * FROM sys_menu", new BeanPropertyRowMapper<>(SysMenu.class));
        Map<Long, List<SysMenu>> byParent = all.stream().collect(Collectors.groupingBy(m -> Optional.ofNullable(m.getParentId()).orElse(0L)));
        return buildChildren(byParent, 0L);
    }

    private List<Map<String, Object>> buildChildren(Map<Long, List<SysMenu>> byParent, Long pid) {
        List<SysMenu> children = byParent.getOrDefault(pid, Collections.emptyList());
        children.sort(Comparator.comparing(m -> Optional.ofNullable(m.getOrderNum()).orElse(0)));
        List<Map<String, Object>> res = new ArrayList<>();
        for (SysMenu m : children) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", m.getId());
            node.put("parentId", m.getParentId());
            node.put("menuType", m.getMenuType());
            node.put("menuName", m.getMenuName());
            node.put("routePath", m.getRoutePath());
            node.put("permissionHint", m.getPermissionHint());
            node.put("icon", m.getIcon());
            node.put("orderNum", m.getOrderNum());
            node.put("enabled", m.getEnabled());
            node.put("children", buildChildren(byParent, m.getId()));
            res.add(node);
        }
        return res;
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:menu:read")
    public SysMenu detail(@PathVariable Long id) {
        return jdbc.queryForObject("SELECT * FROM sys_menu WHERE id=?", new BeanPropertyRowMapper<>(SysMenu.class), id);
    }

    @PostMapping
    @SaCheckPermission("sys:menu:create")
    public SysMenu create(@RequestBody SysMenu req) {
        jdbc.update("INSERT INTO sys_menu(parent_id, menu_type, menu_name, route_path, component_path, permission_hint, icon, order_num, external_link, badge_text, active_path, enabled, cache_page, hidden_menu, embedded, show_badge, affix, hide_tab, full_screen) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                req.getParentId(), req.getMenuType(), req.getMenuName(), req.getRoutePath(), req.getComponentPath(), req.getPermissionHint(), req.getIcon(), req.getOrderNum(), req.getExternalLink(), req.getBadgeText(), req.getActivePath(), req.getEnabled(), req.getCachePage(), req.getHiddenMenu(), req.getEmbedded(), req.getShowBadge(), req.getAffix(), req.getHideTab(), req.getFullScreen());
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return detail(id);
    }

    @PutMapping("/{id}")
    @SaCheckPermission("sys:menu:update")
    public SysMenu update(@PathVariable Long id, @RequestBody SysMenu req) {
        jdbc.update("UPDATE sys_menu SET parent_id=?, menu_type=?, menu_name=?, route_path=?, component_path=?, permission_hint=?, icon=?, order_num=?, external_link=?, badge_text=?, active_path=?, enabled=?, cache_page=?, hidden_menu=?, embedded=?, show_badge=?, affix=?, hide_tab=?, full_screen=? WHERE id=?",
                req.getParentId(), req.getMenuType(), req.getMenuName(), req.getRoutePath(), req.getComponentPath(), req.getPermissionHint(), req.getIcon(), req.getOrderNum(), req.getExternalLink(), req.getBadgeText(), req.getActivePath(), req.getEnabled(), req.getCachePage(), req.getHiddenMenu(), req.getEmbedded(), req.getShowBadge(), req.getAffix(), req.getHideTab(), req.getFullScreen(), id);
        return detail(id);
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:menu:delete")
    public void delete(@PathVariable Long id) { jdbc.update("DELETE FROM sys_menu WHERE id=?", id); }
}
