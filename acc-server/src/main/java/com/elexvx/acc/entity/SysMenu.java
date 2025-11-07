package com.elexvx.acc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_menu")
public class SysMenu {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "menu_type")
  private Integer menuType;

  @Column(name = "menu_name", length = 100)
  private String menuName;

  @Column(name = "route_path", length = 255)
  private String routePath;

  @Column(name = "component_path", length = 255)
  private String componentPath;

  @Column(name = "permission_hint", length = 100)
  private String permissionHint;

  @Column(name = "icon", length = 100)
  private String icon;

  @Column(name = "use_icon_picker")
  private Integer useIconPicker;

  @Column(name = "order_num")
  private Integer orderNum;

  @Column(name = "external_link", length = 255)
  private String externalLink;

  @Column(name = "badge_text", length = 20)
  private String badgeText;

  @Column(name = "active_path", length = 255)
  private String activePath;

  @Column(name = "enabled")
  private Integer enabled;

  @Column(name = "cache_page")
  private Integer cachePage;

  @Column(name = "hidden_menu")
  private Integer hiddenMenu;

  @Column(name = "embedded")
  private Integer embedded;

  @Column(name = "show_badge")
  private Integer showBadge;

  @Column(name = "affix")
  private Integer affix;

  @Column(name = "hide_tab")
  private Integer hideTab;

  @Column(name = "full_screen")
  private Integer fullScreen;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "updated_by")
  private Long updatedBy;

  // getters/setters
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
  public Integer getUseIconPicker() { return useIconPicker; }
  public void setUseIconPicker(Integer useIconPicker) { this.useIconPicker = useIconPicker; }
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
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public Long getCreatedBy() { return createdBy; }
  public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
  public Long getUpdatedBy() { return updatedBy; }
  public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}

