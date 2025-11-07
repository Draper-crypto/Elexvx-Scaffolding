package com.elexvx.acc.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sys_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    /** 账号状态：0禁用/1启用 */
    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    /** 在线状态：0离线/1在线/2异常/3注销 */
    @Builder.Default
    private Integer presenceStatus = 0;

    /** 兼容旧表结构：enabled 非空，无默认。true 表示启用 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String nickname;

    /** 0未知/1男/2女/9其他 */
    @Builder.Default
    private Integer gender = 0;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 512)
    private String avatarUrl;

    @Column(length = 255)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @ElementCollection
    @CollectionTable(name = "sys_user_tag", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag")
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 简单角色编码用于兼容旧数据（admin/user）；建议使用 roles 关系 */
    @Column(length = 20)
    private String role;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
