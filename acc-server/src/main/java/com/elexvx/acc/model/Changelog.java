package com.elexvx.acc.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "changelog")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Changelog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String version;

    @Column(nullable = false, length = 255)
    private String title;

    @Column
    private LocalDate date;

    /** JSON 数组字符串，存储 detail 列表 */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String detailJson;

    @Column
    private Boolean requireReLogin;

    @Column(length = 512)
    private String remark;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

