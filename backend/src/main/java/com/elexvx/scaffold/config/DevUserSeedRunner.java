package com.elexvx.scaffold.config;

import com.elexvx.scaffold.service.AuthService;
import com.elexvx.scaffold.entity.SysUser;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DevUserSeedRunner implements ApplicationRunner {
    private final JdbcTemplate jdbc;
    private final AuthService authService;

    public DevUserSeedRunner(JdbcTemplate jdbc, AuthService authService) {
        this.jdbc = jdbc;
        this.authService = authService;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<com.elexvx.scaffold.entity.SysRole> adminRole = jdbc.query("SELECT * FROM sys_role WHERE role_code = ?", new BeanPropertyRowMapper<>(com.elexvx.scaffold.entity.SysRole.class), "ADMIN");
        if (adminRole.isEmpty()) {
            jdbc.update("INSERT INTO sys_role (id, role_name, role_code, status) VALUES (1, ?, ?, 1)", "管理员", "ADMIN");
        }
        List<com.elexvx.scaffold.entity.SysRole> userRole = jdbc.query("SELECT * FROM sys_role WHERE role_code = ?", new BeanPropertyRowMapper<>(com.elexvx.scaffold.entity.SysRole.class), "USER");
        if (userRole.isEmpty()) {
            jdbc.update("INSERT INTO sys_role (id, role_name, role_code, status) VALUES (2, ?, ?, 1)", "用户", "USER");
        }
        String[] names = new String[]{"Super", "Admin", "User"};
        for (String name : names) {
            List<SysUser> list = jdbc.query("SELECT * FROM sys_user WHERE username = ?", new BeanPropertyRowMapper<>(SysUser.class), name);
            if (list.isEmpty()) {
                String hash = authService.encodePassword("123456");
                jdbc.update("INSERT INTO sys_user (username, password_hash, status, name) VALUES (?, ?, 1, ?)", name, hash, name);
                if ("User".equals(name)) {
                    jdbc.update("INSERT INTO sys_user_role (user_id, role_id) VALUES ((SELECT id FROM sys_user WHERE username = ?), (SELECT id FROM sys_role WHERE role_code = 'USER'))", name);
                } else {
                    jdbc.update("INSERT INTO sys_user_role (user_id, role_id) VALUES ((SELECT id FROM sys_user WHERE username = ?), (SELECT id FROM sys_role WHERE role_code = 'ADMIN'))", name);
                }
            } else {
                SysUser u = list.get(0);
                String hash = authService.encodePassword("123456");
                jdbc.update("UPDATE sys_user SET password_hash = ? WHERE id = ?", hash, u.getId());
            }
        }
    }
}
