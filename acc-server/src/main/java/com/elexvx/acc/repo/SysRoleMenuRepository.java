package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysRoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRoleMenuRepository extends JpaRepository<SysRoleMenu, Long> {
  List<SysRoleMenu> findByRoleId(Long roleId);
  List<SysRoleMenu> findByRoleIdIn(List<Long> roleIds);
  void deleteByRoleId(Long roleId);
  void deleteByMenuId(Long menuId);
}
