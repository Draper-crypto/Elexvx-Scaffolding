package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.elexvx.acc.dto.BaseResponse;
import com.elexvx.acc.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final MenuService menuService;

    /**
     * 返回后端菜单（AppRouteRecord[] 结构）
     */
    @GetMapping("/menus")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<List<Map<String, Object>>>> menus() {
        List<Map<String, Object>> menuList = menuService.getMenusForCurrentUser();
        if (!menuList.isEmpty()) {
            return ResponseEntity.ok(BaseResponse.ok(menuList));
        }

        // Fallback: 静态菜单
        menuList = new ArrayList<>();

        // 顶级：/system
        Map<String, Object> system = new HashMap<>();
        system.put("path", "/system");
        system.put("name", "System");
        system.put("component", "/index/index");
        system.put("meta", Map.of(
                "title", "menus.system.title",
                "icon", "&#xe7b9;",
                "roles", Arrays.asList("R_SUPER", "R_ADMIN")
        ));

        // children
        List<Map<String, Object>> children = new ArrayList<>();

        children.add(createRoute("user", "User", "/system/user", Map.of(
                "title", "menus.system.user",
                "keepAlive", true,
                "roles", Arrays.asList("R_SUPER", "R_ADMIN")
        )));

        children.add(createRoute("role", "Role", "/system/role", Map.of(
                "title", "menus.system.role",
                "keepAlive", true,
                "roles", Collections.singletonList("R_SUPER")
        )));

        children.add(createRoute("user-center", "UserCenter", "/system/user-center", Map.of(
                "title", "menus.system.userCenter",
                "isHide", true,
                "keepAlive", true,
                "isHideTab", true
        )));

        Map<String, Object> menuMeta = new HashMap<>();
        menuMeta.put("title", "menus.system.menu");
        menuMeta.put("keepAlive", true);
        menuMeta.put("roles", Collections.singletonList("R_SUPER"));
        menuMeta.put("authList", Arrays.asList(
                Map.of("title", "新增", "authMark", "add"),
                Map.of("title", "编辑", "authMark", "edit"),
                Map.of("title", "删除", "authMark", "delete")
        ));

        children.add(createRoute("menu", "Menus", "/system/menu", menuMeta));

        // 更新日志页面
        children.add(createRoute("changelog", "Changelog", "/system/changelog", Map.of(
                "title", "更新日志",
                "keepAlive", true,
                "roles", Arrays.asList("R_SUPER", "R_ADMIN")
        )));

        system.put("children", children);
        menuList.add(system);

        return ResponseEntity.ok(BaseResponse.ok(menuList));
    }

    private Map<String, Object> createRoute(String path, String name, String component, Map<String, Object> meta) {
        Map<String, Object> r = new HashMap<>();
        r.put("path", path);
        r.put("name", name);
        r.put("component", component);
        r.put("meta", meta);
        return r;
    }
}
