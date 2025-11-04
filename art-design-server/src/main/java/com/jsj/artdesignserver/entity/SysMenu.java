package com.jsj.artdesignserver.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parentId; // 根为 null

    // 1目录/2菜单/3按钮
    private Integer menuType;

    @Column(nullable = false, length = 100)
    private String menuName;

    @Column(length = 255)
    private String routePath;

    @Column(length = 100)
    private String routeName;

    @Column(length = 255)
    private String componentPath;

    @Column(length = 100)
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

    private Boolean enabled = true;
    private Boolean cachePage;
    private Boolean hiddenMenu;
    private Boolean embedded;
    private Boolean showBadge;
    private Boolean affix;
    private Boolean hideTab;
    private Boolean fullScreen;
}
