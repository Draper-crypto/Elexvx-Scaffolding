package com.jsj.artdesignserver.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_user_tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysUserTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SysUser user;

    @Column(nullable = false)
    private String tag;
}

