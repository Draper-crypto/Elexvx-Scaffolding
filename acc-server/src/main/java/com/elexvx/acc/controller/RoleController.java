package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.elexvx.acc.dto.BaseResponse;
import com.elexvx.acc.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import com.elexvx.acc.repository.RoleRepository;
import com.elexvx.acc.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleRepository roleRepository;

    @GetMapping("/list")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<PaginatedResponse<Map<String, Object>>>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<Role> all = roleRepository.findAll();
        // 回退：若数据库为空，返回静态角色
        if (all.isEmpty()) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now = LocalDateTime.now().format(fmt);
            List<Map<String, Object>> fallback = new ArrayList<>();
            fallback.add(createRole(1L, "超级管理员", "R_SUPER", "拥有所有权限", 1, now));
            fallback.add(createRole(2L, "管理员", "R_ADMIN", "拥有管理权限", 1, now));
            fallback.add(createRole(3L, "普通用户", "R_USER", "基础访问权限", 1, now));
            int total = fallback.size();
            int fromIndex = Math.max((current - 1) * size, 0);
            int toIndex = Math.min(fromIndex + size, total);
            List<Map<String, Object>> records = fromIndex < toIndex ? fallback.subList(fromIndex, toIndex) : Collections.emptyList();
            PaginatedResponse<Map<String, Object>> page = new PaginatedResponse<>(records, current, size, total);
            return ResponseEntity.ok(BaseResponse.ok(page));
        }

        int total = all.size();
        int fromIndex = Math.max((current - 1) * size, 0);
        int toIndex = Math.min(fromIndex + size, total);
        List<Role> slice = fromIndex < toIndex ? all.subList(fromIndex, toIndex) : Collections.emptyList();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> records = new ArrayList<>();
        for (Role r : slice) {
            String createTime = r.getCreatedAt() != null ? r.getCreatedAt().format(fmt) : "";
            records.add(createRole(r.getId(), r.getRoleName(), r.getRoleCode(), r.getDescription(), r.getStatus(), createTime));
        }
        PaginatedResponse<Map<String, Object>> page = new PaginatedResponse<>(records, current, size, total);
        return ResponseEntity.ok(BaseResponse.ok(page));
    }

    private Map<String, Object> createRole(Long id, String name, String code, String desc, Integer status, String createTime) {
        Map<String, Object> r = new HashMap<>();
        r.put("roleId", id);
        r.put("roleName", name);
        r.put("roleCode", code);
        r.put("description", desc);
        r.put("enabled", status != null ? status == 1 : true);
        r.put("createTime", createTime);
        return r;
    }
}
