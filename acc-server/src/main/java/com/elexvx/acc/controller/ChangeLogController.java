package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.common.ApiResponse;
import com.elexvx.acc.common.PageResponse;
import com.elexvx.acc.dto.ChangeLogDtos.ChangeLogItem;
import com.elexvx.acc.dto.ChangeLogDtos.ChangeLogRequest;
import com.elexvx.acc.logging.OperationLog;
import com.elexvx.acc.logging.OperationLogType;
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
  @OperationLog(value = "查询更新日志", type = OperationLogType.QUERY)
  public ResponseEntity<ApiResponse<PageResponse<ChangeLogItem>>> list(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "releaseDate", required = false) String releaseDate) {
    return ResponseEntity.ok(ApiResponse.success(changeLogService.list(page, size, keyword, releaseDate)));
  }

  @GetMapping("/{id}")
  @SaCheckPermission("sys:changelog:read")
  @OperationLog(value = "查看更新日志详情", type = OperationLogType.QUERY, detail = "日志ID={{id}}")
  public ResponseEntity<ApiResponse<ChangeLogItem>> detail(@PathVariable("id") Long id) {
    return ResponseEntity.ok(ApiResponse.success(changeLogService.get(id)));
  }

  @PostMapping
  @SaCheckPermission("sys:changelog:create")
  @OperationLog(value = "新增更新日志", type = OperationLogType.CREATE)
  public ResponseEntity<ApiResponse<ChangeLogItem>> create(@RequestBody ChangeLogRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(changeLogService.create(req, operator)));
  }

  @PutMapping("/{id}")
  @SaCheckPermission("sys:changelog:update")
  @OperationLog(value = "更新更新日志", type = OperationLogType.UPDATE, detail = "日志ID={{id}}")
  public ResponseEntity<ApiResponse<ChangeLogItem>> update(@PathVariable("id") Long id, @RequestBody ChangeLogRequest req) {
    Long operator = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(changeLogService.update(id, req, operator)));
  }

  @DeleteMapping("/{id}")
  @SaCheckPermission("sys:changelog:delete")
  @OperationLog(value = "删除更新日志", type = OperationLogType.DELETE, detail = "日志ID={{id}}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    changeLogService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/public")
  public ResponseEntity<ApiResponse<List<ChangeLogItem>>> publicList() {
    return ResponseEntity.ok(ApiResponse.success(changeLogService.listPublic()));
  }

  @GetMapping("/latest")
  public ResponseEntity<ApiResponse<ChangeLogItem>> latest() {
    return ResponseEntity.ok(ApiResponse.success(changeLogService.latest()));
  }
}
