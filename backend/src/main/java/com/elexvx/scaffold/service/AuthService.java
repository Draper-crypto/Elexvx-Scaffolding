package com.elexvx.scaffold.service;

import com.elexvx.scaffold.dto.UserDto;
import com.elexvx.scaffold.entity.SysPermission;
import com.elexvx.scaffold.entity.SysRole;
import com.elexvx.scaffold.entity.SysUser;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {
    private final JdbcTemplate jdbc;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public SysUser findByUsername(String username) {
        List<SysUser> list = jdbc.query(
                "SELECT * FROM sys_user WHERE LOWER(username) = LOWER(?) LIMIT 1",
                new BeanPropertyRowMapper<>(SysUser.class), username
        );
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean matchesPassword(String raw, String hash) {
        return passwordEncoder.matches(raw, hash);
    }

    public String encodePassword(String raw) { return passwordEncoder.encode(raw); }

    public UserDto buildUserDto(SysUser u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setName(u.getName());
        dto.setNickname(u.getNickname());
        dto.setGender(u.getGender());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setAvatarUrl(u.getAvatarUrl());
        dto.setAddress(u.getAddress());
        dto.setBio(u.getBio());
        dto.setStatus(u.getStatus());
        dto.setPresenceStatus(u.getPresenceStatus());
        dto.setCreatedAt(u.getCreatedAt());

        List<SysRole> roles = jdbc.query(
                "SELECT r.* FROM sys_role r JOIN sys_user_role ur ON ur.role_id = r.id WHERE ur.user_id = ?",
                new BeanPropertyRowMapper<>(SysRole.class), u.getId()
        );
        List<UserDto.RoleBrief> briefs = new ArrayList<>();
        for (SysRole r : roles) {
            UserDto.RoleBrief b = new UserDto.RoleBrief();
            b.id = r.getId(); b.roleName = r.getRoleName(); b.roleCode = r.getRoleCode(); b.status = r.getStatus();
            briefs.add(b);
        }
        dto.setRoles(briefs);

        List<SysPermission> plist = jdbc.query(
                "SELECT DISTINCT p.* FROM sys_permission p " +
                        "JOIN sys_role_permission rp ON rp.permission_id = p.id " +
                        "JOIN sys_user_role ur ON ur.role_id = rp.role_id WHERE ur.user_id = ?",
                new BeanPropertyRowMapper<>(SysPermission.class), u.getId()
        );
        List<String> perms = new ArrayList<>();
        for (SysPermission p : plist) perms.add(p.getPermCode());
        dto.setPermissions(perms);
        return dto;
    }
}
