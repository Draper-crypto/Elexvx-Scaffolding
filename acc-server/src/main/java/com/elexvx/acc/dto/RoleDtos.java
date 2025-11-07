package com.elexvx.acc.dto;

import java.util.List;

public class RoleDtos {
  public static class RoleItem {
    public Long id;
    public String roleName;
    public String roleCode;
    public String description;
    public Integer status;
    public java.time.LocalDateTime createdAt;
  }

  public static class RoleDetail extends RoleItem {}

  public static class RoleCreateRequest {
    public String roleName;
    public String roleCode;
    public String description;
    public Integer status;
  }

  public static class RoleUpdateRequest extends RoleCreateRequest {}

  public static class AssignRoleMenusRequest { public List<Long> menuIds; }
  public static class AssignRolePermissionsRequest { public List<Long> permissionIds; }
}
