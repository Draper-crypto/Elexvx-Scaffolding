package com.jsj.artdesignserver.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsj.artdesignserver.controller.MenuController;
import com.jsj.artdesignserver.entity.SysMenu;
import com.jsj.artdesignserver.repository.SysMenuRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Configuration
public class MenuInitializer {
    @Bean
    @Transactional
    CommandLineRunner importMenusOnStartup(SysMenuRepository menuRepository, ObjectMapper objectMapper) {
        return args -> {
            // 仅在菜单为空时导入，避免重复覆盖
            if (menuRepository.count() > 0) {
                return;
            }

            try {
                ClassPathResource resource = new ClassPathResource("seed-menus.json");
                if (!resource.exists()) {
                    return;
                }
                try (InputStream is = resource.getInputStream()) {
                    List<MenuController.MenuRoute> routes = objectMapper.readValue(is, new TypeReference<>() {});
                    // 覆盖式导入（此处菜单为空，deleteAll 可省略，但为稳妥保留）
                    menuRepository.deleteAll();
                    int order = 0;
                    for (MenuController.MenuRoute route : routes) {
                        saveRouteRecursive(menuRepository, route, null, order++);
                    }
                }
            } catch (Exception e) {
                // 启动初始化失败时不影响应用运行
                System.err.println("[MenuInitializer] 导入菜单失败: " + e.getMessage());
            }
        };
    }

    private void saveRouteRecursive(SysMenuRepository repo, MenuController.MenuRoute route, Long parentId, int orderNum) {
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

        SysMenu saved = repo.save(menu);

        // 保存权限按钮为子菜单（menuType=3）
        if (route.getMeta() != null && route.getMeta().getAuthList() != null) {
            int idx = 0;
            for (MenuController.AuthItem auth : route.getMeta().getAuthList()) {
                SysMenu btn = SysMenu.builder()
                        .parentId(saved.getId())
                        .menuType(3)
                        .menuName(auth.getTitle())
                        .permissionHint(auth.getAuthMark())
                        .orderNum(idx++)
                        .enabled(true)
                        .build();
                repo.save(btn);
            }
        }

        // 递归保存子节点
        if (route.getChildren() != null) {
            int i = 0;
            for (MenuController.MenuRoute child : route.getChildren()) {
                saveRouteRecursive(repo, child, saved.getId(), i++);
            }
        }
    }

    private int resolveMenuType(MenuController.MenuRoute route) {
        boolean isDir = route.getChildren() != null && !route.getChildren().isEmpty()
                && ("/index/index".equals(asString(route.getComponent())) || asString(route.getComponent()).isEmpty());
        return isDir ? 1 : 2;
    }

    private String asString(Object component) {
        return component instanceof String ? (String) component : (component == null ? "" : String.valueOf(component));
    }
}

