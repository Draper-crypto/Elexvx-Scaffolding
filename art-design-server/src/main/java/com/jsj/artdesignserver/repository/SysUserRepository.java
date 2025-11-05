package com.jsj.artdesignserver.repository;

import com.jsj.artdesignserver.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {
    Optional<SysUser> findByUsername(String username);
    Optional<SysUser> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
