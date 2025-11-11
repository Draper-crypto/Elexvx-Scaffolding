package com.elexvx.acc.service;

import com.elexvx.acc.dto.RoleDtos.*;
import com.elexvx.acc.entity.SysRole;
import com.elexvx.acc.entity.SysRoleMenu;
import com.elexvx.acc.entity.SysRolePermission;
import com.elexvx.acc.repo.SysRoleMenuRepository;
import com.elexvx.acc.repo.SysRolePermissionRepository;
import com.elexvx.acc.repo.SysRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoleService {
  private final SysRoleRepository roleRepo;
  private final SysRoleMenuRepository roleMenuRepo;
  private final SysRolePermissionRepository rolePermRepo;

  public RoleService(SysRoleRepository roleRepo, SysRoleMenuRepository roleMenuRepo, SysRolePermissionRepository rolePermRepo) {
    this.roleRepo = roleRepo;
    this.roleMenuRepo = roleMenuRepo;
    this.rolePermRepo = rolePermRepo;
  }

  public Page<RoleItem> list(int page, int size) {
    return roleRepo.findAll(PageRequest.of(Math.max(page - 1, 0), size))
        .map(r -> {
          RoleItem i = new RoleItem();
          i.id = r.getId();
          i.roleName = r.getRoleName();
          i.roleCode = r.getRoleCode();
          i.description = r.getDescription();
          i.status = r.getStatus();
          i.createdAt = r.getCreatedAt();
          return i;
        });
  }

  public RoleDetail get(Long id) {
    SysRole r = roleRepo.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
    RoleDetail d = new RoleDetail();
    d.id = r.getId();
    d.roleName = r.getRoleName();
    d.roleCode = r.getRoleCode();
    d.description = r.getDescription();
    d.status = r.getStatus();
    d.createdAt = r.getCreatedAt();
    return d;
  }

  @Transactional
  public RoleDetail create(RoleCreateRequest req) {
    SysRole r = new SysRole();
    r.setRoleName(req.roleName);
    r.setRoleCode(req.roleCode);
    r.setDescription(req.description);
    r.setStatus(req.status != null ? req.status : 1);
    roleRepo.save(r);
    return get(r.getId());
  }

  @Transactional
  public RoleDetail update(Long id, RoleUpdateRequest req) {
    SysRole r = roleRepo.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
    if (req.roleName != null) r.setRoleName(req.roleName);
    if (req.roleCode != null) r.setRoleCode(req.roleCode);
    if (req.description != null) r.setDescription(req.description);
    if (req.status != null) r.setStatus(req.status);
    roleRepo.save(r);
    return get(id);
  }

  @Transactional
  public void delete(Long id) {
    roleMenuRepo.deleteByRoleId(id);
    rolePermRepo.deleteByRoleId(id);
    roleRepo.deleteById(id);
  }

  @Transactional
  public void setMenus(Long id, AssignRoleMenusRequest req) {
    // 先删除旧绑定并立即刷新，确保后续插入不会与未提交的旧记录发生唯一键冲突
    roleMenuRepo.deleteByRoleId(id);
    roleMenuRepo.flush();

    if (req.menuIds != null) {
      // 去重并过滤空值，避免重复插入触发 (role_id, menu_id) 唯一约束
      List<SysRoleMenu> newBindings = req.menuIds.stream()
          .filter(Objects::nonNull)
          .distinct()
          .map(mid -> {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(id);
            rm.setMenuId(mid);
            return rm;
          })
          .collect(Collectors.toList());
      if (!newBindings.isEmpty()) {
        roleMenuRepo.saveAll(newBindings);
      }
    }
  }

  public List<Long> getMenuIds(Long id) {
    return roleMenuRepo.findByRoleId(id).stream()
        .map(SysRoleMenu::getMenuId)
        .filter(java.util.Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Transactional
  public void setPermissions(Long id, AssignRolePermissionsRequest req) {
    // 删除旧绑定并刷新，避免未提交的旧记录导致唯一键冲突
    rolePermRepo.deleteByRoleId(id);
    rolePermRepo.flush();

    if (req.permissionIds != null) {
      List<SysRolePermission> newBindings = req.permissionIds.stream()
          .filter(Objects::nonNull)
          .distinct()
          .map(pid -> {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(id);
            rp.setPermissionId(pid);
            return rp;
          })
          .collect(Collectors.toList());
      if (!newBindings.isEmpty()) {
        rolePermRepo.saveAll(newBindings);
      }
    }
  }

  public List<Long> getPermissionIds(Long id) {
    return rolePermRepo.findByRoleId(id).stream()
        .map(SysRolePermission::getPermissionId)
        .filter(java.util.Objects::nonNull)
        .collect(Collectors.toList());
  }
}
