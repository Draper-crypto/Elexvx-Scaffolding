package com.elexvx.scaffold.config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaTokenPermissionImpl implements StpInterface {
    private final JdbcTemplate jdbc;
    public SaTokenPermissionImpl(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long uid = Long.valueOf(String.valueOf(loginId));
        return jdbc.query(
                "SELECT DISTINCT p.perm_code FROM sys_permission p " +
                        "JOIN sys_role_permission rp ON rp.permission_id = p.id " +
                        "JOIN sys_user_role ur ON ur.role_id = rp.role_id " +
                        "WHERE ur.user_id = ?",
                (rs, i) -> rs.getString(1), uid
        );
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long uid = Long.valueOf(String.valueOf(loginId));
        return jdbc.query(
                "SELECT DISTINCT r.role_code FROM sys_role r " +
                        "JOIN sys_user_role ur ON ur.role_id = r.id " +
                        "WHERE ur.user_id = ?",
                (rs, i) -> rs.getString(1), uid
        );
    }
}