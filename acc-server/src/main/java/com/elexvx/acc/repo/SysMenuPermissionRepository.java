package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysMenuPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysMenuPermissionRepository extends JpaRepository<SysMenuPermission, Long> {
  List<SysMenuPermission> findByMenuId(Long menuId);
  void deleteByMenuId(Long menuId);
}

