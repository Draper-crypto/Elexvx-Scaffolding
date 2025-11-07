package com.elexvx.acc.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sys_role_permission")
public class SysRolePermission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "role_id", nullable = false)
  private Long roleId;

  @Column(name = "permission_id", nullable = false)
  private Long permissionId;

  @Column(name = "created_at", insertable = false, updatable = false)
  private java.time.LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getRoleId() { return roleId; }
  public void setRoleId(Long roleId) { this.roleId = roleId; }
  public Long getPermissionId() { return permissionId; }
  public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }
  public java.time.LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
