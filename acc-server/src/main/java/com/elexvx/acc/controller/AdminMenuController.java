package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.elexvx.acc.dto.MenuDtos.*;
import com.elexvx.acc.service.MenuService;
import org.springframework.http.ResponseEntity;
import com.elexvx.acc.common.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menus")
@Validated
public class AdminMenuController {
  private final MenuService menuService;

  public AdminMenuController(MenuService menuService) { this.menuService = menuService; }

  @GetMapping("/tree")
  @SaCheckPermission("sys:menu:tree")
  public ResponseEntity<ApiResponse<List<MenuTree>>> tree() { return ResponseEntity.ok(ApiResponse.success(menuService.tree())); }

  @GetMapping("/{id}")
  @SaCheckPermission("sys:menu:read")
  public ResponseEntity<ApiResponse<MenuDetail>> detail(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.success(menuService.get(id))); }

  @PostMapping
  @SaCheckPermission("sys:menu:create")
  public ResponseEntity<ApiResponse<MenuDetail>> create(@RequestBody MenuCreateRequest req) { return ResponseEntity.ok(ApiResponse.success(menuService.create(req))); }

  @PutMapping("/{id}")
  @SaCheckPermission("sys:menu:update")
  public ResponseEntity<ApiResponse<MenuDetail>> update(@PathVariable Long id, @RequestBody MenuUpdateRequest req) { return ResponseEntity.ok(ApiResponse.success(menuService.update(id, req))); }

  @DeleteMapping("/{id}")
  @SaCheckPermission("sys:menu:delete")
  public ResponseEntity<Void> delete(@PathVariable Long id) { menuService.delete(id); return ResponseEntity.noContent().build(); }

  @PutMapping("/{id}/permissions")
  @SaCheckPermission("sys:menu:bindperms")
  public ResponseEntity<Void> bindPermissions(@PathVariable Long id, @RequestBody BindMenuPermissionsRequest req) { menuService.bindPermissions(id, req); return ResponseEntity.noContent().build(); }
}
