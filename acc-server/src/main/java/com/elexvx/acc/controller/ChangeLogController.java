package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.common.ApiResponse;
import com.elexvx.acc.common.PageResponse;
import com.elexvx.acc.dto.ChangeLogDtos.ChangeLogItem;
import com.elexvx.acc.dto.ChangeLogDtos.ChangeLogRequest;
import com.elexvx.acc.service.ChangeLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/change-logs")
@Validated
public class ChangeLogController {
  private final ChangeLogService changeLogService;

  public ChangeLogController(ChangeLogService changeLogService) {
    this.changeLogService = changeLogService;
  }

  @GetMapping
  @SaCheckPermission("sys:changelog:list")
  public ResponseEntity<ApiResponse<PageResponse<ChangeLogItem>>> list(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "releaseDate", required = false) String releaseDate) {
    return ResponseEntity.ok(ApiResponse.success(changeLogService.list(page, size, keyword, releaseDate)));
  }

  @GetMapping("/{id}")
  @SaCheckPermission("sys:changelog:read")
  public ResponseEntity<ApiResponse<ChangeLogItem>> detail(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(changeLogService.get(id)));
  }

  @PostMapping
  @SaCheckPermission("sys:changelog:create")
  public ResponseEntity<ApiResponse<ChangeLogItem>> create(@RequestBody ChangeLogRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(changeLogService.create(req, operator)));
  }

  @PutMapping("/{id}")
  @SaCheckPermission("sys:changelog:update")
  public ResponseEntity<ApiResponse<ChangeLogItem>> update(@PathVariable Long id, @RequestBody ChangeLogRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(changeLogService.update(id, req, operator)));
  }

  @DeleteMapping("/{id}")
  @SaCheckPermission("sys:changelog:delete")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    changeLogService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/public")
  public ResponseEntity<ApiResponse<List<ChangeLogItem>>> publicList() {
    return ResponseEntity.ok(ApiResponse.success(changeLogService.listPublic()));
  }
}
