package com.elexvx.scaffold.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elexvx.scaffold.entity.SysUser;
import com.elexvx.scaffold.entity.SysUserRole;
import com.elexvx.scaffold.mapper.SysUserMapper;
import com.elexvx.scaffold.mapper.SysUserRoleMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ConditionalOnProperty(name = "app.admin.enabled", havingValue = "true")
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminUserController(SysUserMapper userMapper, SysUserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @GetMapping
    @SaCheckPermission("sys:user:list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size,
                                    @RequestParam(required = false) String username) {
        Page<SysUser> p = new Page<>(page, size);
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) qw.like("username", username);
        Page<SysUser> res = userMapper.selectPage(p, qw);
        Map<String, Object> resp = new HashMap<>();
        resp.put("records", res.getRecords());
        resp.put("total", res.getTotal());
        resp.put("page", res.getCurrent());
        resp.put("size", res.getSize());
        return resp;
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:user:read")
    public SysUser detail(@PathVariable Long id) {
        return userMapper.selectById(id);
    }

    @PostMapping
    @SaCheckPermission("sys:user:create")
    public SysUser create(@RequestBody SysUser req) {
        req.setPasswordHash(encoder.encode(req.getPasswordHash()));
        userMapper.insert(req);
        return userMapper.selectById(req.getId());
    }

    @PutMapping("/{id}")
    @SaCheckPermission("sys:user:update")
    public SysUser update(@PathVariable Long id, @RequestBody SysUser req) {
        req.setId(id);
        req.setPasswordHash(null); // 忽略密码更新
        userMapper.updateById(req);
        return userMapper.selectById(id);
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:user:delete")
    public void delete(@PathVariable Long id) {
        userMapper.deleteById(id);
    }

    @PutMapping("/{id}/status")
    @SaCheckPermission("sys:user:status")
    public void setStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        SysUser u = userMapper.selectById(id);
        u.setStatus(body.getOrDefault("status", 1));
        userMapper.updateById(u);
    }

    @PutMapping("/{id}/reset-password")
    @SaCheckPermission("sys:user:resetpwd")
    public void resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPwd = body.get("newPassword");
        SysUser u = userMapper.selectById(id);
        u.setPasswordHash(encoder.encode(newPwd));
        userMapper.updateById(u);
    }

    @PutMapping("/{id}/roles")
    @SaCheckPermission("sys:user:setroles")
    public void setRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> roleIds = body.get("roleIds");
        userRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("user_id", id));
        if (roleIds != null) {
            for (Long rid : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(id); ur.setRoleId(rid);
                userRoleMapper.insert(ur);
            }
        }
    }
}
