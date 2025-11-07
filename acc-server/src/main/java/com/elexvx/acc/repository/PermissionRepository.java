package com.elexvx.acc.repository;

import com.elexvx.acc.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermCode(String permCode);
}

