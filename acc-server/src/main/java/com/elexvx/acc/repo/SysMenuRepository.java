package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysMenuRepository extends JpaRepository<SysMenu, Long> {
  List<SysMenu> findByParentIdOrderByOrderNumAsc(Long parentId);
  Optional<SysMenu> findByRoutePath(String routePath);
}
