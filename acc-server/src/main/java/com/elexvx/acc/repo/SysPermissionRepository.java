package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {
  Optional<SysPermission> findByPermCode(String permCode);
}

