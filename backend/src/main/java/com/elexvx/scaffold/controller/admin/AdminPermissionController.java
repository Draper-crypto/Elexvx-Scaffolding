package com.elexvx.scaffold.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elexvx.scaffold.entity.SysPermission;
import com.elexvx.scaffold.mapper.SysPermissionMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@ConditionalOnProperty(name = "app.admin.enabled", havingValue = "true")
@RequestMapping("/api/admin/permissions")
public class AdminPermissionController {
    private final SysPermissionMapper permissionMapper;

    public AdminPermissionController(SysPermissionMapper permissionMapper) { this.permissionMapper = permissionMapper; }

    @GetMapping
    @SaCheckPermission("sys:perm:list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size,
                                    @RequestParam(required = false) String permCode) {
        Page<SysPermission> p = new Page<>(page, size);
        QueryWrapper<SysPermission> qw = new QueryWrapper<>();
        if (permCode != null && !permCode.isEmpty()) qw.like("perm_code", permCode);
        Page<SysPermission> res = permissionMapper.selectPage(p, qw);
        Map<String, Object> resp = new HashMap<>();
        resp.put("records", res.getRecords());
        resp.put("total", res.getTotal());
        resp.put("page", res.getCurrent());
        resp.put("size", res.getSize());
        return resp;
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:perm:read")
    public SysPermission detail(@PathVariable Long id) { return permissionMapper.selectById(id); }

    @PostMapping
    @SaCheckPermission("sys:perm:create")
    public SysPermission create(@RequestBody SysPermission req) { permissionMapper.insert(req); return permissionMapper.selectById(req.getId()); }

    @PutMapping("/{id}")
    @SaCheckPermission("sys:perm:update")
    public SysPermission update(@PathVariable Long id, @RequestBody SysPermission req) { req.setId(id); permissionMapper.updateById(req); return permissionMapper.selectById(id); }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:perm:delete")
    public void delete(@PathVariable Long id) { permissionMapper.deleteById(id); }
}
