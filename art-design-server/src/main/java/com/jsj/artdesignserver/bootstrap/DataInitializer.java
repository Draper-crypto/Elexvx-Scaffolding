package com.jsj.artdesignserver.bootstrap;

import com.jsj.artdesignserver.entity.SysPermission;
import com.jsj.artdesignserver.entity.SysRole;
import com.jsj.artdesignserver.entity.SysUser;
import com.jsj.artdesignserver.repository.SysPermissionRepository;
import com.jsj.artdesignserver.repository.SysRoleRepository;
import com.jsj.artdesignserver.repository.SysUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@org.springframework.transaction.annotation.Transactional
public class DataInitializer {
    @Bean
    CommandLineRunner initData(SysUserRepository userRepo, SysRoleRepository roleRepo, SysPermissionRepository permRepo, PasswordEncoder encoder, PlatformTransactionManager txManager) {
        return args -> {
            TransactionTemplate tx = new TransactionTemplate(txManager);
            tx.execute(status -> {
            // 初始化基础权限
            SysPermission addPerm = permRepo.findByPermCode("sys:menu:add").orElseGet(() ->
                    permRepo.save(SysPermission.builder().permCode("sys:menu:add").permName("新增").permType(2).effect(1).build()));
            SysPermission editPerm = permRepo.findByPermCode("sys:menu:edit").orElseGet(() ->
                    permRepo.save(SysPermission.builder().permCode("sys:menu:edit").permName("编辑").permType(2).effect(1).build()));
            SysPermission delPerm = permRepo.findByPermCode("sys:menu:delete").orElseGet(() ->
                    permRepo.save(SysPermission.builder().permCode("sys:menu:delete").permName("删除").permType(2).effect(1).build()));

            // 初始化角色
            SysRole superRole = roleRepo.findByRoleCode("R_SUPER").orElseGet(() ->
                    roleRepo.save(SysRole.builder().roleName("超级管理员").roleCode("R_SUPER").description("拥有所有权限").status(1).build()));
            SysRole adminRole = roleRepo.findByRoleCode("R_ADMIN").orElseGet(() ->
                    roleRepo.save(SysRole.builder().roleName("管理员").roleCode("R_ADMIN").description("管理后台").status(1).build()));
            SysRole userRole = roleRepo.findByRoleCode("R_USER").orElseGet(() ->
                    roleRepo.save(SysRole.builder().roleName("普通用户").roleCode("R_USER").description("普通用户角色").status(1).build()));

            // 给超级管理员角色一些权限示例
            superRole.getPermissions().add(addPerm);
            superRole.getPermissions().add(editPerm);
            superRole.getPermissions().add(delPerm);
            roleRepo.save(superRole);

            // 初始化管理员账号并授予角色
            SysUser admin = userRepo.findByUsername("admin").orElseGet(() -> {
                SysUser u = SysUser.builder()
                        .username("admin")
                        .passwordHash(encoder.encode("admin123"))
                        .name("管理员")
                        .status(1)
                        .presenceStatus(1)
                        .build();
                return userRepo.save(u);
            });
            admin.getRoles().add(superRole);
            admin.getRoles().add(adminRole);
            userRepo.save(admin);
                return null;
            });
        };
    }
}
