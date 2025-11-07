package com.elexvx.acc.config;

import com.elexvx.acc.entity.*;
import com.elexvx.acc.repo.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile({"seed"})
public class DevDataSeeder implements ApplicationRunner {
  private final SysUserRepository userRepo;
  private final SysRoleRepository roleRepo;
  private final SysPermissionRepository permRepo;
  private final SysUserRoleRepository userRoleRepo;
  private final SysRolePermissionRepository rolePermRepo;
  private final SysMenuRepository menuRepo;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public DevDataSeeder(SysUserRepository userRepo, SysRoleRepository roleRepo, SysPermissionRepository permRepo,
                       SysUserRoleRepository userRoleRepo, SysRolePermissionRepository rolePermRepo,
                       SysMenuRepository menuRepo) {
    this.userRepo = userRepo; this.roleRepo = roleRepo; this.permRepo = permRepo; this.userRoleRepo = userRoleRepo; this.rolePermRepo = rolePermRepo; this.menuRepo = menuRepo;
  }

  @Override
  public void run(ApplicationArguments args) {
    try {
      // Ensure admin user exists
      SysUser admin = userRepo.findByUsername("admin").orElse(null);
      if (admin == null) {
        admin = new SysUser();
        admin.setUsername("admin");
        admin.setPasswordHash(encoder.encode("admin123"));
        admin.setStatus(1); admin.setPresenceStatus(0);
        admin.setCreatedAt(LocalDateTime.now()); admin.setUpdatedAt(LocalDateTime.now());
        userRepo.save(admin);
      }

      // Ensure sys_admin role exists
      SysRole superRole = roleRepo.findByRoleCode("sys_admin").orElse(null);
      if (superRole == null) {
        superRole = new SysRole();
        superRole.setRoleName("超级管理员");
        superRole.setRoleCode("sys_admin");
        superRole.setStatus(1);
        superRole.setCreatedAt(LocalDateTime.now());
        superRole.setUpdatedAt(LocalDateTime.now());
        roleRepo.save(superRole);
      }

      // Bind admin with super role
      final Long superRoleId = superRole.getId();
      boolean hasBind = userRoleRepo.findByUserId(admin.getId()).stream().anyMatch(ur -> ur.getRoleId().equals(superRoleId));
      if (!hasBind) {
        SysUserRole ur = new SysUserRole();
        ur.setUserId(admin.getId()); ur.setRoleId(superRole.getId()); ur.setCreatedAt(LocalDateTime.now());
        userRoleRepo.save(ur);
      }

      // Seed permissions if empty
      if (permRepo.count() == 0) {
        List<String> codes = java.util.Arrays.asList(
            // user
            "sys:user:list","sys:user:read","sys:user:create","sys:user:update","sys:user:delete",
            "sys:user:status","sys:user:resetpwd","sys:user:setroles","sys:user:import","sys:user:export",
            // role
            "sys:role:list","sys:role:read","sys:role:create","sys:role:update","sys:role:delete",
            "sys:role:setmenus","sys:role:setperms",
            // menu
            "sys:menu:tree","sys:menu:read","sys:menu:create","sys:menu:update","sys:menu:delete","sys:menu:bindperms"
        );
        for (String c : codes) {
          SysPermission p = new SysPermission();
          p.setPermCode(c); p.setPermName(c); p.setPermType(1);
          p.setCreatedAt(LocalDateTime.now()); p.setUpdatedAt(LocalDateTime.now());
          permRepo.save(p);
        }
      }

      // Bind super role with all permissions
      List<SysPermission> allPerms = permRepo.findAll();
      for (SysPermission p : allPerms) {
        boolean exists = rolePermRepo.findByRoleId(superRoleId).stream().anyMatch(rp -> rp.getPermissionId().equals(p.getId()));
        if (!exists) {
          SysRolePermission rp = new SysRolePermission();
          rp.setRoleId(superRoleId); rp.setPermissionId(p.getId()); rp.setCreatedAt(LocalDateTime.now());
          rolePermRepo.save(rp);
        }
      }

      // Seed menus if empty
      if (menuRepo.count() == 0) {
        // Dashboard (目录)
        SysMenu dashboard = new SysMenu();
        dashboard.setMenuType(1); // 目录
        dashboard.setMenuName("仪表盘");
        dashboard.setRoutePath("/dashboard");
        dashboard.setOrderNum(1);
        dashboard.setEnabled(1);
        dashboard.setCreatedAt(LocalDateTime.now());
        dashboard.setUpdatedAt(LocalDateTime.now());
        menuRepo.save(dashboard);

        SysMenu console = new SysMenu();
        console.setParentId(dashboard.getId());
        console.setMenuType(2); // 页面
        console.setMenuName("控制台");
        console.setRoutePath("/dashboard/console");
        console.setComponentPath("/dashboard/console");
        console.setOrderNum(1);
        console.setEnabled(1);
        console.setCreatedAt(LocalDateTime.now());
        console.setUpdatedAt(LocalDateTime.now());
        menuRepo.save(console);

        // System (目录)
        SysMenu system = new SysMenu();
        system.setMenuType(1);
        system.setMenuName("系统设置");
        system.setRoutePath("/system");
        system.setOrderNum(2);
        system.setEnabled(1);
        system.setCreatedAt(LocalDateTime.now());
        system.setUpdatedAt(LocalDateTime.now());
        menuRepo.save(system);

        SysMenu user = new SysMenu();
        user.setParentId(system.getId());
        user.setMenuType(2);
        user.setMenuName("用户管理");
        user.setRoutePath("/system/user");
        user.setComponentPath("/system/user");
        user.setOrderNum(1);
        user.setEnabled(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        menuRepo.save(user);

        SysMenu role = new SysMenu();
        role.setParentId(system.getId());
        role.setMenuType(2);
        role.setMenuName("角色管理");
        role.setRoutePath("/system/role");
        role.setComponentPath("/system/role");
        role.setOrderNum(2);
        role.setEnabled(1);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        menuRepo.save(role);

        SysMenu menu = new SysMenu();
        menu.setParentId(system.getId());
        menu.setMenuType(2);
        menu.setMenuName("菜单管理");
        menu.setRoutePath("/system/menu");
        menu.setComponentPath("/system/menu");
        menu.setOrderNum(3);
        menu.setEnabled(1);
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        menuRepo.save(menu);
      }
    } catch (Exception e) {
      System.err.println("[DevDataSeeder] 初始化数据失败（可忽略，可能是数据库未导入脚本）：" + e.getMessage());
    }
  }
}
