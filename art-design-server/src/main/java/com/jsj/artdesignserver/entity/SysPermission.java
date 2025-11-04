package com.jsj.artdesignserver.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_permission", indexes = {
        @Index(name = "idx_sys_permission_code", columnList = "permCode", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String permCode; // 如 sys:user:list

    @Column(nullable = false, length = 100)
    private String permName;

    // 1接口/2页面/3数据域
    private Integer permType;

    @Column(length = 100)
    private String resource;

    @Column(length = 50)
    private String action;

    @Column(length = 10)
    private String httpMethod;

    @Column(length = 255)
    private String httpPath;

    // 1允许/2拒绝
    private Integer effect;

    @Column(length = 255)
    private String description;
}

