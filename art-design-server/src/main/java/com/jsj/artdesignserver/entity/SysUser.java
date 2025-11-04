package com.jsj.artdesignserver.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sys_user", indexes = {
        @Index(name = "idx_sys_user_username", columnList = "username", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String username;

    @Column(nullable = false, length = 128)
    private String passwordHash;

    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String nickname;

    // 0未知/1男/2女/9其他
    private Integer gender;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 512)
    private String avatarUrl;

    @Column(length = 255)
    private String address;

    @Lob
    private String bio;

    // 账号状态：0禁用/1启用
    @Column(nullable = false)
    private Integer status = 1;

    // 0离线/1在线/2异常/3注销
    @Column(nullable = false)
    private Integer presenceStatus = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    private void prePersistDefaults() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = 1;
        }
        if (presenceStatus == null) {
            presenceStatus = 0;
        }
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<SysRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<SysUserTag> userTags = new HashSet<>();
}
