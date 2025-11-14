package com.elexvx.scaffold.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system/settings")
public class SystemSettingsController {
    private final JdbcTemplate jdbc;
    public SystemSettingsController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/watermark")
    public Map<String, Object> getWatermark() {
        StpUtil.checkLogin();
        String mode = jdbc.query("SELECT param_value FROM sys_param WHERE param_key='watermark.mode' LIMIT 1",
                (rs, i) -> rs.getString(1)).stream().findFirst().orElse("username");
        String custom = jdbc.query("SELECT param_value FROM sys_param WHERE param_key='watermark.custom' LIMIT 1",
                (rs, i) -> rs.getString(1)).stream().findFirst().orElse("");
        Map<String, Object> m = new HashMap<>();
        m.put("mode", mode);
        m.put("custom", custom);
        return m;
    }

    @PutMapping("/watermark")
    @SaCheckPermission(value = "sys:settings:watermark:update", orRole = "ADMIN")
    public void setWatermark(@RequestBody Map<String, String> body) {
        StpUtil.checkLogin();
        String mode = body.getOrDefault("mode", "username");
        String custom = body.getOrDefault("custom", "");
        jdbc.update("INSERT INTO sys_param(param_key,param_value) VALUES('watermark.mode',?) ON DUPLICATE KEY UPDATE param_value=?",
                mode, mode);
        jdbc.update("INSERT INTO sys_param(param_key,param_value) VALUES('watermark.custom',?) ON DUPLICATE KEY UPDATE param_value=?",
                custom, custom);
    }
}