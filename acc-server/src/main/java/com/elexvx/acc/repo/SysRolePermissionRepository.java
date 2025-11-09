package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission, Long> {
  List<SysRolePermission> findByRoleId(Long roleId);
  List<SysRolePermission> findByRoleIdIn(List<Long> roleIds);
  void deleteByRoleId(Long roleId);
  void deleteByPermissionId(Long permissionId);
}
