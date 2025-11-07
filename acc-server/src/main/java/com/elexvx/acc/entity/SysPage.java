package com.elexvx.acc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_page")
public class SysPage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "page_name", length = 100, nullable = false)
  private String pageName;

  @Column(name = "route_path", length = 255, nullable = false)
  private String routePath;

  @Column(name = "component_path", length = 255, nullable = false)
  private String componentPath;

  @Column(name = "permission_hint", length = 100)
  private String permissionHint;

  @Column(name = "enabled")
  private Integer enabled;

  @Column(name = "cache_page")
  private Integer cachePage;

  @Column(name = "embedded")
  private Integer embedded;

  @Column(name = "full_screen")
  private Integer fullScreen;

  @Column(name = "hide_tab")
  private Integer hideTab;

  @Column(name = "affix")
  private Integer affix;

  @Column(name = "active_path", length = 255)
  private String activePath;

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
  public String getPageName() { return pageName; }
  public void setPageName(String pageName) { this.pageName = pageName; }
  public String getRoutePath() { return routePath; }
  public void setRoutePath(String routePath) { this.routePath = routePath; }
  public String getComponentPath() { return componentPath; }
  public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
  public String getPermissionHint() { return permissionHint; }
  public void setPermissionHint(String permissionHint) { this.permissionHint = permissionHint; }
  public Integer getEnabled() { return enabled; }
  public void setEnabled(Integer enabled) { this.enabled = enabled; }
  public Integer getCachePage() { return cachePage; }
  public void setCachePage(Integer cachePage) { this.cachePage = cachePage; }
  public Integer getEmbedded() { return embedded; }
  public void setEmbedded(Integer embedded) { this.embedded = embedded; }
  public Integer getFullScreen() { return fullScreen; }
  public void setFullScreen(Integer fullScreen) { this.fullScreen = fullScreen; }
  public Integer getHideTab() { return hideTab; }
  public void setHideTab(Integer hideTab) { this.hideTab = hideTab; }
  public Integer getAffix() { return affix; }
  public void setAffix(Integer affix) { this.affix = affix; }
  public String getActivePath() { return activePath; }
  public void setActivePath(String activePath) { this.activePath = activePath; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public Long getCreatedBy() { return createdBy; }
  public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
  public Long getUpdatedBy() { return updatedBy; }
  public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}

