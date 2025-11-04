package com.jsj.artdesignserver.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sys_role", indexes = {
        @Index(name = "idx_sys_role_code", columnList = "roleCode", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String roleName;

    @Column(nullable = false, length = 50, unique = true)
    private String roleCode;

    @Column(length = 255)
    private String description;

    // 启用/禁用 0/1
    @Column(nullable = false)
    private Integer status = 1;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Builder.Default
    private Set<SysPermission> permissions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_menu",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id"))
    @Builder.Default
    private Set<SysMenu> menus = new HashSet<>();
}

