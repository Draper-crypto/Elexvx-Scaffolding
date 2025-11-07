package com.elexvx.acc.controller;

import com.elexvx.acc.dto.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dicts")
public class DictController {

    @GetMapping("/{type}")
    public ResponseEntity<BaseResponse<List<Map<String, Object>>>> getDict(@PathVariable String type) {
        List<Map<String, Object>> list = switch (type) {
            case "gender" -> List.of(
                    Map.of("value", 0, "label", "未知"),
                    Map.of("value", 1, "label", "男"),
                    Map.of("value", 2, "label", "女"),
                    Map.of("value", 9, "label", "其他")
            );
            case "user_status", "enable_disable" -> List.of(
                    Map.of("value", 0, "label", "禁用"),
                    Map.of("value", 1, "label", "启用")
            );
            case "presence_status" -> List.of(
                    Map.of("value", 0, "label", "离线"),
                    Map.of("value", 1, "label", "在线"),
                    Map.of("value", 2, "label", "异常"),
                    Map.of("value", 3, "label", "注销")
            );
            case "perm_type" -> List.of(
                    Map.of("value", 1, "label", "接口"),
                    Map.of("value", 2, "label", "页面"),
                    Map.of("value", 3, "label", "数据域")
            );
            case "allow_deny" -> List.of(
                    Map.of("value", 1, "label", "允许"),
                    Map.of("value", 2, "label", "拒绝")
            );
            case "menu_type" -> List.of(
                    Map.of("value", 1, "label", "目录"),
                    Map.of("value", 2, "label", "菜单"),
                    Map.of("value", 3, "label", "按钮")
            );
            default -> List.of();
        };
        return ResponseEntity.ok(BaseResponse.ok(list));
    }
}

