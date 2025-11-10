package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.common.ApiResponse;
import com.elexvx.acc.dto.SystemSettingDtos.*;
import com.elexvx.acc.logging.OperationLog;
import com.elexvx.acc.logging.OperationLogType;
import com.elexvx.acc.service.FileStorageService;
import com.elexvx.acc.service.SystemSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/system/settings")
@Validated
public class SystemSettingController {
  private final SystemSettingService systemSettingService;
  private final FileStorageService fileStorageService;

  public SystemSettingController(
      SystemSettingService systemSettingService, FileStorageService fileStorageService) {
    this.systemSettingService = systemSettingService;
    this.fileStorageService = fileStorageService;
  }

  @GetMapping("/public")
  public ResponseEntity<ApiResponse<SystemSettingResponse>> publicSettings() {
    return ResponseEntity.ok(ApiResponse.success(systemSettingService.getPublicSettings()));
  }

  @GetMapping
  @SaCheckPermission("sys:setting:read")
  @OperationLog(value = "查询系统设置", type = OperationLogType.QUERY)
  public ResponseEntity<ApiResponse<SystemSettingResponse>> adminSettings() {
    return ResponseEntity.ok(ApiResponse.success(systemSettingService.getAdminSettings()));
  }

  @PutMapping("/brand")
  @SaCheckPermission("sys:setting:brand")
  @OperationLog(value = "更新品牌设置", type = OperationLogType.SETTING)
  public ResponseEntity<ApiResponse<BrandSetting>> updateBrand(@RequestBody UpdateBrandRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(systemSettingService.updateBrand(req, operator)));
  }

  @PostMapping("/brand/logo")
  @SaCheckPermission("sys:setting:brand")
  @OperationLog(value = "上传品牌Logo", type = OperationLogType.SETTING)
  public ResponseEntity<ApiResponse<String>> uploadBrandLogo(@RequestParam("file") MultipartFile file) {
    String url = fileStorageService.store(file, "brand");
    return ResponseEntity.ok(ApiResponse.success(url));
  }

  @PutMapping("/watermark")
  @SaCheckPermission("sys:setting:watermark")
  @OperationLog(value = "更新水印设置", type = OperationLogType.SETTING)
  public ResponseEntity<ApiResponse<WatermarkSetting>> updateWatermark(@RequestBody UpdateWatermarkRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(systemSettingService.updateWatermark(req, operator)));
  }
}
