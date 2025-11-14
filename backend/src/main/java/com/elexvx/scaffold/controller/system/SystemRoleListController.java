package com.elexvx.scaffold.controller.system;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role")
public class SystemRoleListController {
    private final JdbcTemplate jdbc;
    public SystemRoleListController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String roleName
    ) {
        int offset = Math.max(0, (current - 1) * size);
        String base = "FROM sys_role r" + (roleName != null && !roleName.isEmpty() ? " WHERE r.role_name LIKE ?" : "");
        List<SysRoleRow> rows = jdbc.query(
                "SELECT r.* " + base + " ORDER BY r.id LIMIT ? OFFSET ?",
                new BeanPropertyRowMapper<>(SysRoleRow.class),
                (roleName != null && !roleName.isEmpty()) ? new Object[]{"%" + roleName + "%", size, offset} : new Object[]{size, offset}
        );
        Long total = jdbc.queryForObject("SELECT COUNT(1) " + base,
                (roleName != null && !roleName.isEmpty()) ? Long.class : Long.class,
                (roleName != null && !roleName.isEmpty()) ? new Object[]{"%" + roleName + "%"} : new Object[]{}
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("records", rows.stream().map(this::toItem).toList());
        resp.put("current", current);
        resp.put("size", size);
        resp.put("total", total);
        return resp;
    }

    private Map<String, Object> toItem(SysRoleRow r) {
        Map<String, Object> m = new HashMap<>();
        m.put("roleId", r.getId());
        m.put("roleName", r.getRoleName());
        m.put("roleCode", r.getRoleCode());
        m.put("description", r.getDescription());
        m.put("enabled", (r.getStatus() == null ? 1 : r.getStatus()) == 1);
        m.put("createTime", r.getCreatedAt());
        return m;
    }

    public static class SysRoleRow {
        private Long id; private String roleName; private String roleCode; private String description; private Integer status; private java.time.LocalDateTime createdAt;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getRoleName() { return roleName; } public void setRoleName(String roleName) { this.roleName = roleName; }
        public String getRoleCode() { return roleCode; } public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
        public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
        public Integer getStatus() { return status; } public void setStatus(Integer status) { this.status = status; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}

