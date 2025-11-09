package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
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
  public ResponseEntity<ApiResponse<MenuDetail>> detail(@PathVariable("id") Long id) { return ResponseEntity.ok(ApiResponse.success(menuService.get(id))); }

  @PostMapping
  @SaCheckPermission("sys:menu:create")
  public ResponseEntity<ApiResponse<MenuDetail>> create(@RequestBody MenuCreateRequest req) { return ResponseEntity.ok(ApiResponse.success(menuService.create(req))); }

  @PutMapping("/{id}")
  @SaCheckPermission("sys:menu:update")
  public ResponseEntity<ApiResponse<MenuDetail>> update(@PathVariable("id") Long id, @RequestBody MenuUpdateRequest req) { return ResponseEntity.ok(ApiResponse.success(menuService.update(id, req))); }

  @DeleteMapping("/{id}")
  @SaCheckPermission("sys:menu:delete")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) { menuService.delete(id); return ResponseEntity.noContent().build(); }

  @PutMapping("/{id}/permissions")
  @SaCheckPermission("sys:menu:bindperms")
  public ResponseEntity<Void> bindPermissions(@PathVariable("id") Long id, @RequestBody BindMenuPermissionsRequest req) { menuService.bindPermissions(id, req); return ResponseEntity.noContent().build(); }

  @PostMapping("/{id}/auths")
  @SaCheckPermission("sys:menu:auth:create")
  public ResponseEntity<ApiResponse<MenuAuth>> createAuth(@PathVariable("id") Long id, @RequestBody MenuAuthRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(menuService.createAuth(id, req, operator)));
  }

  @PutMapping("/{id}/auths/{authId}")
  @SaCheckPermission("sys:menu:auth:update")
  public ResponseEntity<ApiResponse<MenuAuth>> updateAuth(@PathVariable("id") Long id, @PathVariable("authId") Long authId, @RequestBody MenuAuthRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(menuService.updateAuth(id, authId, req, operator)));
  }

  @DeleteMapping("/{id}/auths/{authId}")
  @SaCheckPermission("sys:menu:auth:delete")
  public ResponseEntity<Void> deleteAuth(@PathVariable("id") Long id, @PathVariable("authId") Long authId) {
    menuService.deleteAuth(id, authId);
    return ResponseEntity.noContent().build();
  }

  // 初始化演示菜单：仅当数据库为空时插入基础菜单
  @PostMapping("/init-demo")
  @SaCheckPermission("sys:menu:create")
  public ResponseEntity<ApiResponse<Object>> initDemoMenus() {
    menuService.seedDemoMenusIfEmpty();
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
