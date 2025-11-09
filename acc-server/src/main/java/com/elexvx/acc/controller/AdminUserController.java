package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.elexvx.acc.common.PageResponse;
import com.elexvx.acc.dto.UserDtos.*;
import com.elexvx.acc.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.elexvx.acc.common.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@Validated
public class AdminUserController {
  private final UserService userService;

  public AdminUserController(UserService userService) { this.userService = userService; }

  @GetMapping
  @SaCheckPermission("sys:user:list")
  public ResponseEntity<ApiResponse<PageResponse<UserListItem>>> list(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "userName", required = false) String userName,
      @RequestParam(value = "userGender", required = false) String userGender,
      @RequestParam(value = "userPhone", required = false) String userPhone,
      @RequestParam(value = "userEmail", required = false) String userEmail,
      @RequestParam(value = "status", required = false) String status) {
    Page<UserListItem> p = userService.list(page, size, userName, userGender, userPhone, userEmail, status);
    PageResponse<UserListItem> resp = new PageResponse<>(p.getContent(), p.getTotalElements(), page, size);
    return ResponseEntity.ok(ApiResponse.success(resp));
  }

  @GetMapping("/{id}")
  @SaCheckPermission("sys:user:read")
  public ResponseEntity<ApiResponse<UserDetail>> detail(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(userService.get(id)));
  }

  @PostMapping
  @SaCheckPermission("sys:user:create")
  public ResponseEntity<ApiResponse<UserDetail>> create(@RequestBody UserCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(userService.create(req)));
  }

  @PutMapping("/{id}")
  @SaCheckPermission("sys:user:update")
  public ResponseEntity<ApiResponse<UserDetail>> update(@PathVariable("id") Long id, @RequestBody UserUpdateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(userService.update(id, req)));
  }

  @DeleteMapping("/{id}")
  @SaCheckPermission("sys:user:delete")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id) {
    userService.delete(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PutMapping("/{id}/status")
  @SaCheckPermission("sys:user:status")
  public ResponseEntity<ApiResponse<Object>> updateStatus(@PathVariable("id") Long id, @RequestBody StatusUpdateRequest req) {
    userService.updateStatus(id, req);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PutMapping("/{id}/reset-password")
  @SaCheckPermission("sys:user:resetpwd")
  public ResponseEntity<ApiResponse<Object>> resetPassword(@PathVariable("id") Long id, @RequestBody ResetPasswordRequest req) {
    userService.resetPassword(id, req);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PutMapping("/{id}/roles")
  @SaCheckPermission("sys:user:setroles")
  public ResponseEntity<ApiResponse<Object>> setRoles(@PathVariable("id") Long id, @RequestBody SetUserRolesRequest req) {
    userService.setRoles(id, req);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
