package com.elexvx.scaffold.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final JdbcTemplate jdbc;
    public ProfileController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/info")
    public Map<String, Object> info() {
        StpUtil.checkLogin();
        Long uid = StpUtil.getLoginIdAsLong();
        var list = jdbc.query("SELECT * FROM sys_user WHERE id = ? LIMIT 1", new BeanPropertyRowMapper<>(com.elexvx.scaffold.entity.SysUser.class), uid);
        if (list.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        var u = list.get(0);
        Map<String, Object> m = new HashMap<>();
        m.put("realName", u.getName());
        m.put("nikeName", u.getNickname());
        m.put("email", u.getEmail());
        m.put("mobile", u.getPhone());
        m.put("address", u.getAddress());
        m.put("sex", u.getGender());
        m.put("des", u.getBio());
        m.put("avatarUrl", u.getAvatarUrl());
        return m;
    }

    @PutMapping("/info")
    public void updateInfo(@RequestBody Map<String, Object> body) {
        StpUtil.checkLogin();
        Long uid = StpUtil.getLoginIdAsLong();
        Integer gender = null;
        Object g = body.get("sex");
        if (g instanceof Number) gender = ((Number) g).intValue();
        else if (g != null) {
            try { gender = Integer.valueOf(String.valueOf(g)); } catch (Exception ignored) { gender = null; }
        }
        String name = body.get("realName") == null ? null : String.valueOf(body.get("realName"));
        String nick = body.get("nikeName") == null ? null : String.valueOf(body.get("nikeName"));
        String email = body.get("email") == null ? null : String.valueOf(body.get("email"));
        String phone = body.get("mobile") == null ? null : String.valueOf(body.get("mobile"));
        String address = body.get("address") == null ? null : String.valueOf(body.get("address"));
        String bio = body.get("des") == null ? null : String.valueOf(body.get("des"));
        String avatar = body.get("avatarUrl") == null ? null : String.valueOf(body.get("avatarUrl"));
        jdbc.update("UPDATE sys_user SET name=?, nickname=?, email=?, phone=?, address=?, bio=?, gender=?, avatar_url=? WHERE id=?",
                name, nick, email, phone, address, bio, gender, avatar, uid);
    }

    @PutMapping("/password")
    public void updatePassword(@RequestBody Map<String, String> body) {
        StpUtil.checkLogin();
        Long uid = StpUtil.getLoginIdAsLong();
        String current = body.get("password");
        String newPwd = body.get("newPassword");
        String confirm = body.get("confirmPassword");
        if (newPwd == null || !newPwd.equals(confirm)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "两次密码不一致");
        var list = jdbc.query("SELECT * FROM sys_user WHERE id = ? LIMIT 1", new BeanPropertyRowMapper<>(com.elexvx.scaffold.entity.SysUser.class), uid);
        if (list.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        var u = list.get(0);
        var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        if (current == null || !encoder.matches(current, u.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "当前密码错误");
        String hash = encoder.encode(newPwd);
        jdbc.update("UPDATE sys_user SET password_hash = ? WHERE id = ?", hash, uid);
    }
}