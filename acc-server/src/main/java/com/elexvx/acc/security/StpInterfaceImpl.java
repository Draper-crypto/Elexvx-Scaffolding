package com.elexvx.acc.security;

import cn.dev33.satoken.stp.StpInterface;
import com.elexvx.acc.entity.SysRolePermission;
import com.elexvx.acc.entity.SysUserRole;
import com.elexvx.acc.repo.SysPermissionRepository;
import com.elexvx.acc.repo.SysRolePermissionRepository;
import com.elexvx.acc.repo.SysUserRoleRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component
public class StpInterfaceImpl implements StpInterface {
  private final SysUserRoleRepository userRoleRepo;
  private final SysRolePermissionRepository rolePermRepo;
  private final SysPermissionRepository permRepo;

  public StpInterfaceImpl(SysUserRoleRepository userRoleRepo, SysRolePermissionRepository rolePermRepo, SysPermissionRepository permRepo) {
    this.userRoleRepo = userRoleRepo;
    this.rolePermRepo = rolePermRepo;
    this.permRepo = permRepo;
  }

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    try {
      Long uid = Long.parseLong(loginId.toString());
      List<SysUserRole> urs = userRoleRepo.findByUserId(uid);
      if (urs == null || urs.isEmpty()) {
        // 开发环境兜底：无角色时返回常用权限，便于联调
        return java.util.Arrays.asList(
            "sys:user:list","sys:user:read","sys:user:create","sys:user:update","sys:user:delete",
            "sys:user:status","sys:user:resetpwd","sys:user:setroles",
            "sys:role:list","sys:role:read","sys:role:create","sys:role:update","sys:role:delete",
            "sys:role:setmenus","sys:role:setperms",
            "sys:menu:tree","sys:menu:read","sys:menu:create","sys:menu:update","sys:menu:delete","sys:menu:bindperms"
        );
      }
      List<Long> roleIds = urs.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
      List<SysRolePermission> rps = new ArrayList<>();
      for (Long rid : roleIds) { rps.addAll(rolePermRepo.findByRoleId(rid)); }
      return rps.stream()
          .map(rp -> permRepo.findById(rp.getPermissionId()).map(p -> p.getPermCode()).orElse(null))
          .filter(s -> s != null)
          .distinct()
          .collect(Collectors.toList());
    } catch (Exception e) {
      return java.util.Collections.emptyList();
    }
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    Long uid = Long.parseLong(loginId.toString());
    return userRoleRepo.findByUserId(uid).stream().map(ur -> "R_" + ur.getRoleId()).collect(Collectors.toList());
  }
}
