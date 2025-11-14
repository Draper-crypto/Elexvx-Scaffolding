package com.elexvx.scaffold.controller.system;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class SystemUserListController {
    private final JdbcTemplate jdbc;
    public SystemUserListController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String userName
    ) {
        int offset = Math.max(0, (current - 1) * size);
        String base = "FROM sys_user u" + (userName != null && !userName.isEmpty() ? " WHERE u.username LIKE ?" : "");
        List<SysUserRow> rows = jdbc.query(
                "SELECT u.* " + base + " ORDER BY u.id LIMIT ? OFFSET ?",
                new BeanPropertyRowMapper<>(SysUserRow.class),
                (userName != null && !userName.isEmpty()) ? new Object[]{"%" + userName + "%", size, offset} : new Object[]{size, offset}
        );
        Long total = jdbc.queryForObject("SELECT COUNT(1) " + base,
                (userName != null && !userName.isEmpty()) ? Long.class : Long.class,
                (userName != null && !userName.isEmpty()) ? new Object[]{"%" + userName + "%"} : new Object[]{}
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("records", rows.stream().map(this::toItem).toList());
        resp.put("current", current);
        resp.put("size", size);
        resp.put("total", total);
        return resp;
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer gender = null;
        Object g = body.get("gender");
        if (g instanceof Number) gender = ((Number) g).intValue();
        else if (g instanceof String s) {
            try { gender = Integer.valueOf(s); } catch (Exception ignored) {}
            if (gender == null) {
                if ("男".equals(s)) gender = 1; else if ("女".equals(s)) gender = 2; else gender = 0;
            }
        }
        String phone = body.get("phone") == null ? null : String.valueOf(body.get("phone"));
        jdbc.update("UPDATE sys_user SET gender = ?, phone = ? WHERE id = ?",
                gender, phone, id);
    }

    private Map<String, Object> toItem(SysUserRow u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("avatar", u.getAvatarUrl());
        m.put("status", String.valueOf(u.getStatus() == null ? 1 : u.getStatus()));
        m.put("userName", u.getUsername());
        m.put("userGender", String.valueOf(u.getGender() == null ? 0 : u.getGender()));
        m.put("nickName", u.getNickname());
        m.put("userPhone", u.getPhone());
        m.put("userEmail", u.getEmail());
        List<String> roles = jdbc.query("SELECT r.role_code FROM sys_role r JOIN sys_user_role ur ON ur.role_id=r.id WHERE ur.user_id=?",
                (rs, rn) -> rs.getString(1), u.getId());
        m.put("userRoles", roles);
        m.put("createBy", "system");
        m.put("createTime", u.getCreatedAt());
        m.put("updateBy", "system");
        m.put("updateTime", u.getUpdatedAt());
        return m;
    }

    public static class SysUserRow {
        private Long id; private String username; private String avatarUrl; private Integer status; private Integer gender;
        private String nickname; private String phone; private String email; private java.time.LocalDateTime createdAt; private java.time.LocalDateTime updatedAt;
        public Long getId() { return id; } public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
        public String getAvatarUrl() { return avatarUrl; } public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public Integer getStatus() { return status; } public void setStatus(Integer status) { this.status = status; }
        public Integer getGender() { return gender; } public void setGender(Integer gender) { this.gender = gender; }
        public String getNickname() { return nickname; } public void setNickname(String nickname) { this.nickname = nickname; }
        public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}

