package com.elexvx.acc;

import com.elexvx.acc.model.User;
import com.elexvx.acc.model.Role;
import com.elexvx.acc.model.Menu;
import com.elexvx.acc.repository.UserRepository;
import com.elexvx.acc.repository.RoleRepository;
import com.elexvx.acc.repository.MenuRepository;
import com.elexvx.acc.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.net.Socket;

@SpringBootApplication
public class AccServerApplication {

    public static void main(String[] args) {
        // 启动前检测端口占用（仅允许使用 8080）
        checkPortOrExit(8080);
        SpringApplication.run(AccServerApplication.class, args);
    }

    private static void checkPortOrExit(int port) {
        // 通过尝试连接判断端口是否被占用，避免 TIME_WAIT 误判
        try (Socket socket = new Socket("127.0.0.1", port)) {
            // 连接成功，说明已有服务在监听该端口
            System.err.println("[启动失败] 端口 " + port + " 已被占用，后端无法启动。请释放该端口后重试。");
            System.exit(1);
        } catch (IOException ignored) {
            // 连接失败表示端口未被占用，继续启动
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner dataInitializer(UserRepository userRepository, RoleRepository roleRepository, MenuRepository menuRepository, UserService userService) {
        return args -> {
            // 初始化角色
            if (roleRepository.count() == 0) {
                Role superAdmin = Role.builder().roleName("超级管理员").roleCode("R_SUPER").description("拥有所有权限").status(1).build();
                Role admin = Role.builder().roleName("管理员").roleCode("R_ADMIN").description("拥有管理权限").status(1).build();
                Role userRole = Role.builder().roleName("普通用户").roleCode("R_USER").description("基础访问权限").status(1).build();
                roleRepository.save(superAdmin);
                roleRepository.save(admin);
                roleRepository.save(userRole);
            }

            // 初始化用户
            if (userRepository.count() == 0) {
                User adminUser = userService.createUser("admin", "admin123", "admin", "管理员");
                User normalUser = userService.createUser("user", "user123", "user", "普通用户");

                // 绑定角色
                Role superAdminRole = roleRepository.findByRoleCode("R_SUPER").orElse(null);
                Role adminRole = roleRepository.findByRoleCode("R_ADMIN").orElse(null);
                Role userRole = roleRepository.findByRoleCode("R_USER").orElse(null);
                if (adminUser != null) {
                    if (superAdminRole != null) adminUser.getRoles().add(superAdminRole);
                    if (adminRole != null) adminUser.getRoles().add(adminRole);
                    userRepository.save(adminUser);
                }
                if (normalUser != null && userRole != null) {
                    normalUser.getRoles().add(userRole);
                    userRepository.save(normalUser);
                }
            }

            // 初始化菜单
            if (menuRepository.count() == 0) {
                Menu system = Menu.builder()
                        .menuType(1)
                        .menuName("System")
                        .routePath("/system")
                        .componentPath("/index/index")
                        .orderNum(1)
                        .enabled(true)
                        .build();
                system = menuRepository.save(system);

                Menu userMenu = Menu.builder()
                        .parentId(system.getId())
                        .menuType(2)
                        .menuName("User")
                        .routePath("/system/user")
                        .componentPath("/index/index")
                        .orderNum(1)
                        .enabled(true)
                        .build();
                menuRepository.save(userMenu);

                Menu roleMenu = Menu.builder()
                        .parentId(system.getId())
                        .menuType(2)
                        .menuName("Role")
                        .routePath("/system/role")
                        .componentPath("/index/index")
                        .orderNum(2)
                        .enabled(true)
                        .build();
                menuRepository.save(roleMenu);

                Menu menusMenu = Menu.builder()
                        .parentId(system.getId())
                        .menuType(2)
                        .menuName("Menus")
                        .routePath("/system/menu")
                        .componentPath("/index/index")
                        .orderNum(3)
                        .enabled(true)
                        .build();
                menuRepository.save(menusMenu);

                // 更新日志菜单
                Menu changelogMenu = Menu.builder()
                        .parentId(system.getId())
                        .menuType(2)
                        .menuName("Changelog")
                        .routePath("/system/changelog")
                        .componentPath("/change/log/index")
                        .orderNum(4)
                        .enabled(true)
                        .build();
                menuRepository.save(changelogMenu);
            }
        };
    }
}
