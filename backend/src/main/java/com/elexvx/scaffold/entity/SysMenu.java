package com.elexvx.scaffold.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_menu")
public class SysMenu {
    @TableId
    private Long id;
    private Long parentId;
    private Integer menuType;
    private String menuName;
    private String routePath;
    private String componentPath;
    private String permissionHint;
    private String icon;
    private Integer orderNum;
    private String externalLink;
    private String badgeText;
    private String activePath;
    private Integer enabled;
    private Integer cachePage;
    private Integer hiddenMenu;
    private Integer embedded;
    private Integer showBadge;
    private Integer affix;
    private Integer hideTab;
    private Integer fullScreen;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getMenuType() { return menuType; }
    public void setMenuType(Integer menuType) { this.menuType = menuType; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getRoutePath() { return routePath; }
    public void setRoutePath(String routePath) { this.routePath = routePath; }
    public String getComponentPath() { return componentPath; }
    public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
    public String getPermissionHint() { return permissionHint; }
    public void setPermissionHint(String permissionHint) { this.permissionHint = permissionHint; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
    public String getExternalLink() { return externalLink; }
    public void setExternalLink(String externalLink) { this.externalLink = externalLink; }
    public String getBadgeText() { return badgeText; }
    public void setBadgeText(String badgeText) { this.badgeText = badgeText; }
    public String getActivePath() { return activePath; }
    public void setActivePath(String activePath) { this.activePath = activePath; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public Integer getCachePage() { return cachePage; }
    public void setCachePage(Integer cachePage) { this.cachePage = cachePage; }
    public Integer getHiddenMenu() { return hiddenMenu; }
    public void setHiddenMenu(Integer hiddenMenu) { this.hiddenMenu = hiddenMenu; }
    public Integer getEmbedded() { return embedded; }
    public void setEmbedded(Integer embedded) { this.embedded = embedded; }
    public Integer getShowBadge() { return showBadge; }
    public void setShowBadge(Integer showBadge) { this.showBadge = showBadge; }
    public Integer getAffix() { return affix; }
    public void setAffix(Integer affix) { this.affix = affix; }
    public Integer getHideTab() { return hideTab; }
    public void setHideTab(Integer hideTab) { this.hideTab = hideTab; }
    public Integer getFullScreen() { return fullScreen; }
    public void setFullScreen(Integer fullScreen) { this.fullScreen = fullScreen; }
}

