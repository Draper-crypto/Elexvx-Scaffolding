package com.elexvx.acc.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sys_menu_permission")
public class SysMenuPermission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "menu_id", nullable = false)
  private Long menuId;

  @Column(name = "permission_id", nullable = false)
  private Long permissionId;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getMenuId() { return menuId; }
  public void setMenuId(Long menuId) { this.menuId = menuId; }
  public Long getPermissionId() { return permissionId; }
  public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }
}

