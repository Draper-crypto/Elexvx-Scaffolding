package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.elexvx.acc.common.ApiResponse;
import com.elexvx.acc.common.PageResponse;
import com.elexvx.acc.dto.OperationLogDtos.OperationLogItem;
import com.elexvx.acc.dto.OperationLogDtos.OperationLogTypeMeta;
import com.elexvx.acc.service.OperationLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system/operation-logs")
@Validated
public class OperationLogController {
  private final OperationLogService operationLogService;

  public OperationLogController(OperationLogService operationLogService) {
    this.operationLogService = operationLogService;
  }

  @GetMapping
  @SaCheckPermission("sys:operation-log:list")
  public ResponseEntity<ApiResponse<PageResponse<OperationLogItem>>> list(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "actionType", required = false) String actionType,
      @RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "success", required = false) Boolean success) {
    PageResponse<OperationLogItem> response = operationLogService.list(page, size, actionType, username, success);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/types")
  @SaCheckPermission("sys:operation-log:list")
  public ResponseEntity<ApiResponse<List<OperationLogTypeMeta>>> types() {
    return ResponseEntity.ok(ApiResponse.success(operationLogService.getTypeMetadata()));
  }
}
