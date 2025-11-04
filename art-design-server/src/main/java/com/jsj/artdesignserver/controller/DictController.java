package com.jsj.artdesignserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dicts")
public class DictController {
    private static final Map<String, List<Map<String, Object>>> DICTS = Map.of(
            "gender", List.of(
                    Map.of("value", 0, "label", "未知"),
                    Map.of("value", 1, "label", "男"),
                    Map.of("value", 2, "label", "女"),
                    Map.of("value", 9, "label", "其他")
            ),
            "user_status", List.of(
                    Map.of("value", 0, "label", "禁用"),
                    Map.of("value", 1, "label", "启用")
            ),
            "presence_status", List.of(
                    Map.of("value", 0, "label", "离线"),
                    Map.of("value", 1, "label", "在线"),
                    Map.of("value", 2, "label", "异常"),
                    Map.of("value", 3, "label", "注销")
            ),
            "menu_type", List.of(
                    Map.of("value", 1, "label", "目录"),
                    Map.of("value", 2, "label", "菜单"),
                    Map.of("value", 3, "label", "按钮")
            ),
            "perm_type", List.of(
                    Map.of("value", 1, "label", "接口"),
                    Map.of("value", 2, "label", "页面"),
                    Map.of("value", 3, "label", "数据域")
            ),
            "allow_deny", List.of(
                    Map.of("value", 1, "label", "允许"),
                    Map.of("value", 2, "label", "拒绝")
            )
    );

    @GetMapping("/{type}")
    public ResponseEntity<List<Map<String, Object>>> getDict(@PathVariable String type) {
        var list = DICTS.get(type);
        if (list == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}

