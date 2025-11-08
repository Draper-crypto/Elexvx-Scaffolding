package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.common.ApiResponse;
import com.elexvx.acc.dto.SystemSettingDtos.*;
import com.elexvx.acc.service.SystemSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/settings")
@Validated
public class SystemSettingController {
  private final SystemSettingService systemSettingService;

  public SystemSettingController(SystemSettingService systemSettingService) {
    this.systemSettingService = systemSettingService;
  }

  @GetMapping("/public")
  public ResponseEntity<ApiResponse<SystemSettingResponse>> publicSettings() {
    return ResponseEntity.ok(ApiResponse.success(systemSettingService.getPublicSettings()));
  }

  @GetMapping
  @SaCheckPermission("sys:setting:read")
  public ResponseEntity<ApiResponse<SystemSettingResponse>> adminSettings() {
    return ResponseEntity.ok(ApiResponse.success(systemSettingService.getAdminSettings()));
  }

  @PutMapping("/brand")
  @SaCheckPermission("sys:setting:brand")
  public ResponseEntity<ApiResponse<BrandSetting>> updateBrand(@RequestBody UpdateBrandRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(systemSettingService.updateBrand(req, operator)));
  }

  @PutMapping("/watermark")
  @SaCheckPermission("sys:setting:watermark")
  public ResponseEntity<ApiResponse<WatermarkSetting>> updateWatermark(@RequestBody UpdateWatermarkRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(systemSettingService.updateWatermark(req, operator)));
  }
}
