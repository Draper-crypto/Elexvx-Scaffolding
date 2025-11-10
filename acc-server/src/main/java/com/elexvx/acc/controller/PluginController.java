package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.elexvx.acc.common.ApiResponse;
import com.elexvx.acc.dto.PluginDtos.PluginInfo;
import com.elexvx.acc.logging.OperationLog;
import com.elexvx.acc.logging.OperationLogType;
import com.elexvx.acc.service.PluginService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system/plugins")
@Validated
public class PluginController {
  private final PluginService pluginService;

  public PluginController(PluginService pluginService) {
    this.pluginService = pluginService;
  }

  @GetMapping
  @SaCheckPermission("sys:plugin:list")
  public ResponseEntity<ApiResponse<List<PluginInfo>>> list() {
    return ResponseEntity.ok(ApiResponse.success(pluginService.list()));
  }

  @PostMapping("/{pluginKey}/load")
  @SaCheckPermission("sys:plugin:load")
  @OperationLog(value = "载入插件", type = OperationLogType.PLUGIN_LOAD, detail = "插件={{pluginKey}}")
  public ResponseEntity<ApiResponse<PluginInfo>> load(@PathVariable("pluginKey") String pluginKey) {
    return ResponseEntity.ok(ApiResponse.success(pluginService.load(pluginKey)));
  }

  @PostMapping("/{pluginKey}/unload")
  @SaCheckPermission("sys:plugin:unload")
  @OperationLog(value = "卸载插件", type = OperationLogType.PLUGIN_UNLOAD, detail = "插件={{pluginKey}}")
  public ResponseEntity<ApiResponse<PluginInfo>> unload(@PathVariable("pluginKey") String pluginKey) {
    return ResponseEntity.ok(ApiResponse.success(pluginService.unload(pluginKey)));
  }
}
