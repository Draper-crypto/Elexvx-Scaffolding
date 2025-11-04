package com.jsj.artdesignserver.service;

import com.jsj.artdesignserver.entity.SysMenu;
import com.jsj.artdesignserver.repository.SysMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final SysMenuRepository menuRepository;

    /**
     * 获取所有菜单（按 parentId、orderNum、id 排序）
     */
    public List<SysMenu> listAllOrdered() {
        return menuRepository.findAllOrdered();
    }

    /**
     * 构建菜单树（按 parentId 组织）
     */
    public List<SysMenuNode> buildTree(List<SysMenu> menus) {
        Map<Long, List<SysMenu>> childrenMap = menus.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        List<SysMenuNode> roots = new ArrayList<>();
        for (SysMenu m : menus) {
            if (m.getParentId() == null) {
                roots.add(toNode(m, childrenMap));
            }
        }
        return roots;
    }

    private SysMenuNode toNode(SysMenu menu, Map<Long, List<SysMenu>> childrenMap) {
        SysMenuNode node = new SysMenuNode(menu);
        List<SysMenu> children = childrenMap.getOrDefault(menu.getId(), Collections.emptyList());
        for (SysMenu child : children) {
            node.getChildren().add(toNode(child, childrenMap));
        }
        return node;
    }

    /** 简单菜单树节点封装 */
    public static class SysMenuNode {
        private final SysMenu data;
        private final List<SysMenuNode> children = new ArrayList<>();

        public SysMenuNode(SysMenu data) {
            this.data = data;
        }

        public SysMenu getData() { return data; }
        public List<SysMenuNode> getChildren() { return children; }
    }
}

