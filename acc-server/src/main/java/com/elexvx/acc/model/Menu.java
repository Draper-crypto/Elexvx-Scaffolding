package com.elexvx.acc.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sys_menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parentId;

    /** 1目录/2菜单/3按钮 */
    @Column(nullable = false)
    private Integer menuType;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "route_path", length = 255)
    private String routePath;

    @Column(name = "component_path", length = 255)
    private String componentPath;

    @Column(name = "permission_hint", length = 100)
    private String permissionHint;

    @Column(length = 100)
    private String icon;

    private Boolean useIconPicker;
    private Integer orderNum;

    @Column(length = 255)
    private String externalLink;

    @Column(length = 20)
    private String badgeText;

    @Column(length = 255)
    private String activePath;

    private Boolean enabled;
    private Boolean cachePage;
    private Boolean hiddenMenu;
    private Boolean embedded;
    private Boolean showBadge;
    private Boolean affix;
    private Boolean hideTab;
    private Boolean fullScreen;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_menu_permission",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}

