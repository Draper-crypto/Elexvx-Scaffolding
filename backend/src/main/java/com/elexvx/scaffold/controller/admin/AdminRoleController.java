package com.elexvx.scaffold.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elexvx.scaffold.entity.SysRole;
import com.elexvx.scaffold.entity.SysRolePermission;
import com.elexvx.scaffold.mapper.SysRoleMapper;
import com.elexvx.scaffold.mapper.SysRolePermissionMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ConditionalOnProperty(name = "app.admin.enabled", havingValue = "true")
@RequestMapping("/api/admin/roles")
public class AdminRoleController {
    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermMapper;

    public AdminRoleController(SysRoleMapper roleMapper, SysRolePermissionMapper rolePermMapper) {
        this.roleMapper = roleMapper;
        this.rolePermMapper = rolePermMapper;
    }

    @GetMapping
    @SaCheckPermission("sys:role:list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size,
                                    @RequestParam(required = false) String roleName) {
        Page<SysRole> p = new Page<>(page, size);
        QueryWrapper<SysRole> qw = new QueryWrapper<>();
        if (roleName != null && !roleName.isEmpty()) qw.like("role_name", roleName);
        Page<SysRole> res = roleMapper.selectPage(p, qw);
        Map<String, Object> resp = new HashMap<>();
        resp.put("records", res.getRecords());
        resp.put("total", res.getTotal());
        resp.put("page", res.getCurrent());
        resp.put("size", res.getSize());
        return resp;
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:role:read")
    public SysRole detail(@PathVariable Long id) { return roleMapper.selectById(id); }

    @PostMapping
    @SaCheckPermission("sys:role:create")
    public SysRole create(@RequestBody SysRole req) { roleMapper.insert(req); return roleMapper.selectById(req.getId()); }

    @PutMapping("/{id}")
    @SaCheckPermission("sys:role:update")
    public SysRole update(@PathVariable Long id, @RequestBody SysRole req) { req.setId(id); roleMapper.updateById(req); return roleMapper.selectById(id); }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:role:delete")
    public void delete(@PathVariable Long id) { roleMapper.deleteById(id); }

    @PutMapping("/{id}/permissions")
    @SaCheckPermission("sys:role:setperms")
    public void setPermissions(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> permissionIds = body.get("permissionIds");
        rolePermMapper.delete(new QueryWrapper<SysRolePermission>().eq("role_id", id));
        if (permissionIds != null) {
            for (Long pid : permissionIds) {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(id); rp.setPermissionId(pid);
                rolePermMapper.insert(rp);
            }
        }
    }
}
