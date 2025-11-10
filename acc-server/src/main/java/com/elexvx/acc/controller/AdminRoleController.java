package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.elexvx.acc.common.PageResponse;
import com.elexvx.acc.dto.RoleDtos.*;
import com.elexvx.acc.logging.OperationLog;
import com.elexvx.acc.logging.OperationLogType;
import com.elexvx.acc.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.elexvx.acc.common.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@Validated
public class AdminRoleController {
  private final RoleService roleService;

  public AdminRoleController(RoleService roleService) { this.roleService = roleService; }

  @GetMapping
  @SaCheckPermission("sys:role:list")
  @OperationLog(value = "查询角色列表", type = OperationLogType.QUERY)
  public ResponseEntity<ApiResponse<PageResponse<RoleItem>>> list(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<RoleItem> p = roleService.list(page, size);
    return ResponseEntity.ok(ApiResponse.success(new PageResponse<>(p.getContent(), p.getTotalElements(), page, size)));
  }

  @GetMapping("/{id}")
  @SaCheckPermission("sys:role:read")
  @OperationLog(value = "查看角色详情", type = OperationLogType.QUERY, detail = "角色ID={{id}}")
  public ResponseEntity<ApiResponse<RoleDetail>> detail(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(roleService.get(id)));
  }

  @PostMapping
  @SaCheckPermission("sys:role:create")
  @OperationLog(value = "新增角色", type = OperationLogType.CREATE)
  public ResponseEntity<ApiResponse<RoleDetail>> create(@RequestBody RoleCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(roleService.create(req)));
  }

  @PutMapping("/{id}")
  @SaCheckPermission("sys:role:update")
  @OperationLog(value = "更新角色", type = OperationLogType.UPDATE, detail = "角色ID={{id}}")
  public ResponseEntity<ApiResponse<RoleDetail>> update(@PathVariable("id") Long id, @RequestBody RoleUpdateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(roleService.update(id, req)));
  }

  @DeleteMapping("/{id}")
  @SaCheckPermission("sys:role:delete")
  @OperationLog(value = "删除角色", type = OperationLogType.DELETE, detail = "角色ID={{id}}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    roleService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/menus")
  @SaCheckPermission("sys:role:setmenus")
  @OperationLog(value = "分配角色菜单", type = OperationLogType.UPDATE, detail = "角色ID={{id}}")
  public ResponseEntity<Void> setMenus(@PathVariable("id") Long id, @RequestBody AssignRoleMenusRequest req) {
    roleService.setMenus(id, req);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/menus")
  @SaCheckPermission("sys:role:setmenus")
  @OperationLog(value = "查询角色菜单", type = OperationLogType.QUERY, detail = "角色ID={{id}}")
  public ResponseEntity<ApiResponse<List<Long>>> getMenus(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(roleService.getMenuIds(id)));
  }

  @PutMapping("/{id}/permissions")
  @SaCheckPermission("sys:role:setperms")
  @OperationLog(value = "分配角色权限", type = OperationLogType.UPDATE, detail = "角色ID={{id}}")
  public ResponseEntity<Void> setPermissions(@PathVariable("id") Long id, @RequestBody AssignRolePermissionsRequest req) {
    roleService.setPermissions(id, req);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/permissions")
  @SaCheckPermission("sys:role:setperms")
  @OperationLog(value = "查询角色权限", type = OperationLogType.QUERY, detail = "角色ID={{id}}")
  public ResponseEntity<ApiResponse<List<Long>>> getPermissions(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(roleService.getPermissionIds(id)));
  }
}
