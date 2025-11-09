package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.common.ApiResponse;
import com.elexvx.acc.dto.UserDtos.UserChangePasswordRequest;
import com.elexvx.acc.dto.UserDtos.UserDetail;
import com.elexvx.acc.dto.UserDtos.UserProfileUpdateRequest;
import com.elexvx.acc.service.FileStorageService;
import com.elexvx.acc.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@Validated
public class UserProfileController {
  private final UserService userService;
  private final FileStorageService fileStorageService;

  public UserProfileController(UserService userService, FileStorageService fileStorageService) {
    this.userService = userService;
    this.fileStorageService = fileStorageService;
  }

  @GetMapping
  @SaCheckLogin
  public ResponseEntity<ApiResponse<UserDetail>> profile() {
    Long uid = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(userService.get(uid)));
  }

  @PutMapping
  @SaCheckLogin
  public ResponseEntity<ApiResponse<UserDetail>> update(@RequestBody UserProfileUpdateRequest req) {
    Long uid = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(uid, req)));
  }

  @PutMapping("/password")
  @SaCheckLogin
  public ResponseEntity<ApiResponse<Object>> changePassword(@RequestBody UserChangePasswordRequest req) {
    Long uid = StpUtil.getLoginIdAsLong();
    userService.changePassword(uid, req);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/avatar")
  @SaCheckLogin
  public ResponseEntity<ApiResponse<String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
    Long uid = StpUtil.getLoginIdAsLong();
    String path = fileStorageService.store(file, "avatar");
    userService.updateAvatar(uid, path);
    return ResponseEntity.ok(ApiResponse.success(path));
  }
}
