package com.elexvx.acc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_permission")
public class SysPermission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "perm_code", length = 100, nullable = false, unique = true)
  private String permCode;

  @Column(name = "perm_name", length = 100, nullable = false)
  private String permName;

  @Column(name = "perm_type")
  private Integer permType;

  @Column(name = "resource", length = 100)
  private String resource;

  @Column(name = "action", length = 50)
  private String action;

  @Column(name = "http_method", length = 10)
  private String httpMethod;

  @Column(name = "http_path", length = 255)
  private String httpPath;

  @Column(name = "effect")
  private Integer effect;

  @Column(name = "description", length = 255)
  private String description;

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
  public String getPermCode() { return permCode; }
  public void setPermCode(String permCode) { this.permCode = permCode; }
  public String getPermName() { return permName; }
  public void setPermName(String permName) { this.permName = permName; }
  public Integer getPermType() { return permType; }
  public void setPermType(Integer permType) { this.permType = permType; }
  public String getResource() { return resource; }
  public void setResource(String resource) { this.resource = resource; }
  public String getAction() { return action; }
  public void setAction(String action) { this.action = action; }
  public String getHttpMethod() { return httpMethod; }
  public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
  public String getHttpPath() { return httpPath; }
  public void setHttpPath(String httpPath) { this.httpPath = httpPath; }
  public Integer getEffect() { return effect; }
  public void setEffect(Integer effect) { this.effect = effect; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public Long getCreatedBy() { return createdBy; }
  public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
  public Long getUpdatedBy() { return updatedBy; }
  public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}

