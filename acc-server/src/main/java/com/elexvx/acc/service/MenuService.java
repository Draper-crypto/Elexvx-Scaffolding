package com.elexvx.acc.service;

import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.model.Menu;
import com.elexvx.acc.model.Role;
import com.elexvx.acc.model.User;
import com.elexvx.acc.repository.MenuRepository;
import com.elexvx.acc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    /**
     * 返回后端菜单（AppRouteRecord[] 结构），基于当前用户角色过滤
     */
    public List<Map<String, Object>> getMenusForCurrentUser() {
        Long uid = StpUtil.getLoginIdAsLong();
        Optional<User> opt = userRepository.findById(uid);
        Set<String> roleCodes = opt.map(User::getRoles)
                .orElseGet(Collections::emptySet)
                .stream().map(Role::getRoleCode).collect(Collectors.toSet());

        // 如果尚未配置数据库菜单，返回空列表，由控制器决定回退
        List<Menu> enabledMenus = menuRepository.findByEnabledTrueOrderByOrderNumAsc();
        if (enabledMenus.isEmpty()) {
            return Collections.emptyList();
        }

        // 构建树结构：parentId -> children
        Map<Long, List<Menu>> childrenMap = new HashMap<>();
        for (Menu m : enabledMenus) {
            Long pid = m.getParentId();
            childrenMap.computeIfAbsent(pid == null ? 0L : pid, k -> new ArrayList<>()).add(m);
        }
        childrenMap.values().forEach(list -> list.sort(Comparator.comparing(m -> Optional.ofNullable(m.getOrderNum()).orElse(0))));

        // 构造根
        List<Menu> roots = childrenMap.getOrDefault(0L, Collections.emptyList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Menu root : roots) {
            result.add(menuToRoute(root, childrenMap, roleCodes));
        }
        return result;
    }

    private Map<String, Object> menuToRoute(Menu menu, Map<Long, List<Menu>> childrenMap, Set<String> roleCodes) {
        Map<String, Object> route = new HashMap<>();
        route.put("path", menu.getRoutePath());
        route.put("name", menu.getMenuName());
        route.put("component", Optional.ofNullable(menu.getComponentPath()).orElse("/index/index"));

        Map<String, Object> meta = new HashMap<>();
        meta.put("title", menu.getMenuName());
        if (menu.getIcon() != null) meta.put("icon", menu.getIcon());
        if (Boolean.TRUE.equals(menu.getCachePage())) meta.put("keepAlive", true);
        if (Boolean.TRUE.equals(menu.getHiddenMenu())) meta.put("isHide", true);
        if (Boolean.TRUE.equals(menu.getHideTab())) meta.put("isHideTab", true);
        meta.put("roles", new ArrayList<>(roleCodes));
        route.put("meta", meta);

        List<Menu> children = childrenMap.getOrDefault(menu.getId(), Collections.emptyList());
        if (!children.isEmpty()) {
            List<Map<String, Object>> childRoutes = new ArrayList<>();
            for (Menu child : children) {
                childRoutes.add(menuToRoute(child, childrenMap, roleCodes));
            }
            route.put("children", childRoutes);
        }
        return route;
    }
}
