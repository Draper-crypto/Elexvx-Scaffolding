package com.jsj.artdesignserver.repository;

import com.jsj.artdesignserver.entity.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {
    Optional<SysPermission> findByPermCode(String permCode);
    List<SysPermission> findByResource(String resource);
}
