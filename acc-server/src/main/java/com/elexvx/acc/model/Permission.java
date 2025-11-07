package com.elexvx.acc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "perm_code", nullable = false, unique = true, length = 100)
    private String permCode;

    @Column(name = "perm_name", nullable = false, length = 100)
    private String permName;

    /** 1接口/2页面/3数据域 */
    private Integer permType;

    @Column(length = 100)
    private String resource;

    @Column(length = 50)
    private String action;

    @Column(length = 10)
    private String httpMethod;

    @Column(length = 255)
    private String httpPath;

    /** 1允许/2拒绝 */
    private Integer effect;

    @Column(length = 255)
    private String description;
}

