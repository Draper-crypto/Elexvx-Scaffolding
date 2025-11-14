package com.elexvx.scaffold.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.scaffold.dto.CaptchaResponse;
import com.elexvx.scaffold.dto.LoginRequest;
import com.elexvx.scaffold.dto.RegisterRequest;
import com.elexvx.scaffold.dto.ForgotPasswordRequest;
import com.elexvx.scaffold.dto.UserDto;
import com.elexvx.scaffold.entity.SysUser;
import com.elexvx.scaffold.service.AuthService;
import com.elexvx.scaffold.service.CaptchaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthV2Controller {
    private final AuthService authService;
    private final CaptchaService captchaService;
    private final JdbcTemplate jdbc;

    public AuthV2Controller(AuthService authService, CaptchaService captchaService, JdbcTemplate jdbc) {
        this.authService = authService;
        this.captchaService = captchaService;
        this.jdbc = jdbc;
    }

    @GetMapping("/captcha")
    public CaptchaResponse captcha() {
        return captchaService.generate();
    }

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest req) {
        SysUser exists = authService.findByUsername(req.getUsername());
        if (exists != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
        SysUser u = new SysUser();
        u.setUsername(req.getUsername());
        u.setPasswordHash(authService.encodePassword(req.getPassword()));
        u.setGender(req.getGender());
        u.setPhone(req.getPhone());
        u.setStatus(1);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = con -> {
            var ps = con.prepareStatement("INSERT INTO sys_user (username, password_hash, status, gender, phone) VALUES (?, ?, ?, ?, ?)", new String[]{"id"});
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setInt(3, u.getStatus());
            if (u.getGender() == null) ps.setNull(4, java.sql.Types.TINYINT); else ps.setInt(4, u.getGender());
            ps.setString(5, u.getPhone());
            return ps;
        };
        jdbc.update(psc, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "注册失败");
        u.setId(key.longValue());
        jdbc.update("INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)", u.getId(), 2L);
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", u.getId());
        return resp;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        if (req.getCaptchaToken() != null && req.getCaptchaCode() != null) {
            if (!captchaService.verify(req.getCaptchaToken(), req.getCaptchaCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码错误或过期");
            }
        }
        SysUser u = authService.findByUsername(req.getUsername());
        if (u == null || !authService.matchesPassword(req.getPassword(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号或密码错误");
        }
        if (req.getRoleCode() == null || req.getRoleCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "角色必选");
        }
        var roles = jdbc.query(
                "SELECT r.* FROM sys_role r JOIN sys_user_role ur ON ur.role_id = r.id WHERE ur.user_id = ?",
                new BeanPropertyRowMapper<>(com.elexvx.scaffold.entity.SysRole.class), u.getId()
        );
        boolean hasRole = false;
        for (var r : roles) {
            if (req.getRoleCode().equals(r.getRoleCode())) { hasRole = true; break; }
        }
        if (!hasRole) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "角色不匹配");
        }
        StpUtil.login(u.getId());
        StpUtil.getTokenSession().set("currentRole", req.getRoleCode());
        SaTokenInfo info = StpUtil.getTokenInfo();
        UserDto dto = authService.buildUserDto(u);
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", info.getTokenValue());
        resp.put("user", dto);
        resp.put("roleCode", req.getRoleCode());
        return resp;
    }

    @GetMapping("/me")
    public UserDto me() {
        StpUtil.checkLogin();
        Long id = StpUtil.getLoginIdAsLong();
        var list = jdbc.query(
                "SELECT * FROM sys_user WHERE id = ? LIMIT 1",
                new BeanPropertyRowMapper<>(SysUser.class), id
        );
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return authService.buildUserDto(list.get(0));
    }

    @GetMapping("/roles")
    public Map<String, Object> roles(@RequestParam("username") String username) {
        SysUser u = authService.findByUsername(username);
        if (u == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        var roles = jdbc.query(
                "SELECT r.* FROM sys_role r JOIN sys_user_role ur ON ur.role_id = r.id WHERE ur.user_id = ?",
                new BeanPropertyRowMapper<>(com.elexvx.scaffold.entity.SysRole.class), u.getId()
        );
        var list = new java.util.ArrayList<UserDto.RoleBrief>();
        for (var r : roles) {
            UserDto.RoleBrief b = new UserDto.RoleBrief();
            b.id = r.getId(); b.roleName = r.getRoleName(); b.roleCode = r.getRoleCode(); b.status = r.getStatus();
            list.add(b);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("roles", list);
        return resp;
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest req) {
        if (req.getCaptchaToken() != null && req.getCaptchaCode() != null) {
            if (!captchaService.verify(req.getCaptchaToken(), req.getCaptchaCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码错误或过期");
            }
        }
        SysUser u = authService.findByUsername(req.getUsername());
        if (u == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        String hash = authService.encodePassword(req.getNewPassword());
        jdbc.update("UPDATE sys_user SET password_hash = ? WHERE id = ?", hash, u.getId());
    }

    @PostMapping("/logout")
    public void logout() {
        StpUtil.logout();
    }

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap() {
        StpUtil.checkLogin();
        Long uid = StpUtil.getLoginIdAsLong();
        var users = jdbc.query(
                "SELECT * FROM sys_user WHERE id = ? LIMIT 1",
                new BeanPropertyRowMapper<>(SysUser.class), uid
        );
        if (users.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        var user = users.get(0);
        UserDto dto = authService.buildUserDto(user);

        var rows = jdbc.query(
                "SELECT id, parent_id AS parentId, menu_name AS menuName, route_path AS routePath, " +
                        "component_path AS componentPath, icon, permission_hint AS permissionHint, enabled " +
                        "FROM sys_menu m WHERE m.enabled = 1 AND (m.permission_hint IS NULL OR m.permission_hint IN (" +
                        "SELECT DISTINCT p.perm_code FROM sys_permission p " +
                        "JOIN sys_role_permission rp ON rp.permission_id = p.id " +
                        "JOIN sys_user_role ur ON ur.role_id = rp.role_id WHERE ur.user_id = ?)) " +
                        "ORDER BY parent_id, order_num, id",
                new BeanPropertyRowMapper<>(com.elexvx.scaffold.controller.v3.SystemMenuController.SysMenuRow.class), uid);

        java.util.Map<Long, java.util.Map<String, Object>> nodeMap = new java.util.HashMap<>();
        java.util.List<java.util.Map<String, Object>> roots = new java.util.ArrayList<>();
        for (var r : rows) {
            java.util.Map<String, Object> node = new java.util.HashMap<>();
            node.put("id", r.getId());
            node.put("path", r.getRoutePath());
            node.put("component", r.getComponentPath());
            node.put("name", deriveName(r));
            java.util.Map<String, Object> meta = new java.util.HashMap<>();
            meta.put("title", r.getMenuName());
            meta.put("icon", r.getIcon());
            if (r.getPermissionHint() != null) meta.put("permissionHint", r.getPermissionHint());
            node.put("meta", meta);
            node.put("children", new java.util.ArrayList<>());
            nodeMap.put(r.getId(), node);
        }
        for (var r : rows) {
            java.util.Map<String, Object> node = nodeMap.get(r.getId());
            if (r.getParentId() == null) {
                node.put("component", "/index/index");
                roots.add(node);
            } else {
                java.util.Map<String, Object> parent = nodeMap.get(r.getParentId());
                if (parent != null) {
                    ((java.util.List<java.util.Map<String, Object>>) parent.get("children")).add(node);
                } else {
                    roots.add(node);
                }
            }
        }

        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("user", dto);
        resp.put("menus", roots);
        return resp;
    }

    private String deriveName(com.elexvx.scaffold.controller.v3.SystemMenuController.SysMenuRow r) {
        String path = r.getRoutePath();
        if (path == null || path.isBlank() || "/".equals(path)) {
            return "Root" + r.getId();
        }
        String p = path.replaceAll("^/+", "").replaceAll("/+", "_");
        String s = p.substring(0, 1).toUpperCase() + (p.length() > 1 ? p.substring(1) : "");
        return s;
    }
}
