package com.jsj.artdesignserver.controller;

import com.jsj.artdesignserver.entity.SysMenu;
import com.jsj.artdesignserver.repository.SysMenuRepository;
import com.jsj.artdesignserver.service.MenuService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final SysMenuRepository menuRepository;

    /**
     * 从数据库读取菜单并返回给前端
     */
    @GetMapping("/menus")
    public ResponseEntity<List<MenuRoute>> getMenus() {
        List<SysMenu> all = menuService.listAllOrdered();
        Map<Long, List<SysMenu>> byParent = all.stream()
                .filter(m -> !isRootParent(m.getParentId()))
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        List<MenuRoute> routes = all.stream()
                .filter(m -> isRootParent(m.getParentId()) && (m.getMenuType() == null || m.getMenuType() != 3))
                .map(m -> toRoute(m, byParent))
                .collect(Collectors.toList());

        return ResponseEntity.ok(routes);
    }

    /**
     * 创建菜单或权限按钮
     */
    @PostMapping("/menus")
    @Transactional
    public ResponseEntity<SysMenu> createMenu(@RequestBody SysMenu payload) {
        SysMenu entity = SysMenu.builder()
                .parentId(payload.getParentId())
                .menuType(payload.getMenuType())
                .menuName(payload.getMenuName())
                .routePath(payload.getRoutePath())
                .routeName(payload.getRouteName())
                .componentPath(payload.getComponentPath())
                .permissionHint(payload.getPermissionHint())
                .icon(payload.getIcon())
                .orderNum(payload.getOrderNum())
                .externalLink(payload.getExternalLink())
                .badgeText(payload.getBadgeText())
                .activePath(payload.getActivePath())
                .enabled(payload.getEnabled())
                .cachePage(payload.getCachePage())
                .hiddenMenu(payload.getHiddenMenu())
                .embedded(payload.getEmbedded())
                .showBadge(payload.getShowBadge())
                .affix(payload.getAffix())
                .hideTab(payload.getHideTab())
                .fullScreen(payload.getFullScreen())
                .build();

        SysMenu saved = menuRepository.save(entity);
        return ResponseEntity.ok(saved);
    }

    /**
     * 更新菜单或权限按钮
     */
    @PutMapping("/menus/{id}")
    @Transactional
    public ResponseEntity<SysMenu> updateMenu(@PathVariable Long id, @RequestBody SysMenu payload) {
        Optional<SysMenu> optional = menuRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        SysMenu entity = optional.get();
        entity.setParentId(payload.getParentId());
        entity.setMenuType(payload.getMenuType());
        entity.setMenuName(payload.getMenuName());
        entity.setRoutePath(payload.getRoutePath());
        entity.setRouteName(payload.getRouteName());
        entity.setComponentPath(payload.getComponentPath());
        entity.setPermissionHint(payload.getPermissionHint());
        entity.setIcon(payload.getIcon());
        entity.setOrderNum(payload.getOrderNum());
        entity.setExternalLink(payload.getExternalLink());
        entity.setBadgeText(payload.getBadgeText());
        entity.setActivePath(payload.getActivePath());
        entity.setEnabled(payload.getEnabled());
        entity.setCachePage(payload.getCachePage());
        entity.setHiddenMenu(payload.getHiddenMenu());
        entity.setEmbedded(payload.getEmbedded());
        entity.setShowBadge(payload.getShowBadge());
        entity.setAffix(payload.getAffix());
        entity.setHideTab(payload.getHideTab());
        entity.setFullScreen(payload.getFullScreen());
        SysMenu saved = menuRepository.save(entity);
        return ResponseEntity.ok(saved);
    }

    /**
     * 删除菜单或权限按钮（递归删除子节点）
     */
    @DeleteMapping("/menus/{id}")
    @Transactional
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        deleteRecursive(id);
        return ResponseEntity.ok().build();
    }

    private void deleteRecursive(Long id) {
        List<SysMenu> children = menuRepository.findByParentId(id);
        for (SysMenu child : children) {
            deleteRecursive(child.getId());
        }
        menuRepository.deleteById(id);
    }

    /**
     * 导入静态路由到数据库（覆盖式）
     */
    @PostMapping("/menus/import")
    @Transactional
    public ResponseEntity<String> importMenus(@RequestBody List<MenuRoute> routes) {
        // 覆盖式导入：清空后重建
        menuRepository.deleteAll();
        // 递归保存
        int order = 0;
        for (MenuRoute route : routes) {
            saveRouteRecursive(route, null, order++);
        }
        return ResponseEntity.ok("OK");
    }

    private void saveRouteRecursive(MenuRoute route, Long parentId, int orderNum) {
        SysMenu menu = SysMenu.builder()
                .parentId(parentId)
                .menuType(resolveMenuType(route))
                .menuName(route.getMeta() != null ? route.getMeta().getTitle() : route.getName())
                .routePath(route.getPath())
                .routeName(route.getName())
                .componentPath(asString(route.getComponent()))
                .permissionHint(null)
                .icon(route.getMeta() != null ? route.getMeta().getIcon() : null)
                .orderNum(orderNum)
                .externalLink(route.getMeta() != null ? route.getMeta().getLink() : null)
                .badgeText(route.getMeta() != null ? route.getMeta().getShowTextBadge() : null)
                .activePath(route.getMeta() != null ? route.getMeta().getActivePath() : null)
                .enabled(true)
                .cachePage(route.getMeta() != null ? route.getMeta().getKeepAlive() : null)
                .hiddenMenu(route.getMeta() != null ? route.getMeta().getIsHide() : null)
                .embedded(route.getMeta() != null ? route.getMeta().getIsIframe() : null)
                .showBadge(route.getMeta() != null ? route.getMeta().getShowBadge() : null)
                .affix(route.getMeta() != null ? route.getMeta().getFixedTab() : null)
                .hideTab(route.getMeta() != null ? route.getMeta().getIsHideTab() : null)
                .fullScreen(route.getMeta() != null ? route.getMeta().getIsFullPage() : null)
                .build();

        SysMenu saved = menuRepository.save(menu);

        // 保存权限按钮为子菜单（menuType=3）
        if (route.getMeta() != null && route.getMeta().getAuthList() != null) {
            int idx = 0;
            for (AuthItem auth : route.getMeta().getAuthList()) {
                SysMenu btn = SysMenu.builder()
                        .parentId(saved.getId())
                        .menuType(3)
                        .menuName(auth.getTitle())
                        .permissionHint(auth.getAuthMark())
                        .orderNum(idx++)
                        .enabled(true)
                        .build();
                menuRepository.save(btn);
            }
        }

        // 递归保存子节点
        if (route.getChildren() != null) {
            int i = 0;
            for (MenuRoute child : route.getChildren()) {
                saveRouteRecursive(child, saved.getId(), i++);
            }
        }
    }

    private int resolveMenuType(MenuRoute route) {
        boolean isDir = route.getChildren() != null && !route.getChildren().isEmpty()
                && (Objects.equals(asString(route.getComponent()), "/index/index") || asString(route.getComponent()).isEmpty());
        return isDir ? 1 : 2;
    }

    private boolean isRootParent(Long parentId) {
        return parentId == null || parentId <= 0L;
    }

    private String asString(Object component) {
        return component instanceof String ? (String) component : (component == null ? "" : String.valueOf(component));
    }

    private MenuRoute toRoute(SysMenu m, Map<Long, List<SysMenu>> byParent) {
        List<SysMenu> children = byParent.getOrDefault(m.getId(), Collections.emptyList());

        // 收集按钮权限
        List<AuthItem> authList = children.stream()
                .filter(c -> Objects.equals(c.getMenuType(), 3))
                .map(c -> new AuthItem(c.getId(), c.getMenuName(), c.getPermissionHint()))
                .collect(Collectors.toList());

        // 非按钮子菜单
        List<MenuRoute> childRoutes = children.stream()
                .filter(c -> c.getMenuType() == null || !Objects.equals(c.getMenuType(), 3))
                .map(c -> toRoute(c, byParent))
                .collect(Collectors.toList());

        RouteMeta meta = RouteMeta.builder()
                .title(m.getMenuName())
                .icon(m.getIcon())
                .isHide(m.getHiddenMenu())
                .isHideTab(m.getHideTab())
                .keepAlive(m.getCachePage())
                .isIframe(m.getEmbedded())
                .link(m.getExternalLink())
                .showBadge(m.getShowBadge())
                .showTextBadge(m.getBadgeText())
                .activePath(m.getActivePath())
                .fixedTab(m.getAffix())
                .isFullPage(m.getFullScreen())
                .authList(authList.isEmpty() ? null : authList)
                .build();

        return MenuRoute.builder()
                .id(m.getId())
                .path(m.getRoutePath())
                .name(m.getRouteName())
                .component(m.getComponentPath())
                .meta(meta)
                .children(childRoutes.isEmpty() ? null : childRoutes)
                .build();
    }

    // ==== DTO（与前端路由结构对应）====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MenuRoute {
        private Long id; // 可选
        private String path;
        private String name;
        private String component; // 传字符串，前端按约定加载视图
        private RouteMeta meta;
        private List<MenuRoute> children;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RouteMeta {
        private String title;
        private String icon;
        private Boolean isHide;
        private Boolean isHideTab;
        private Boolean keepAlive;
        private Boolean isIframe;
        private String link;
        private List<AuthItem> authList;
        private List<String> roles;
        private String showTextBadge;
        private Boolean showBadge;
        private String activePath;
        private Boolean fixedTab;
        private Boolean isFullPage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthItem {
        private Long id;
        private String title;
        private String authMark;
    }
}
