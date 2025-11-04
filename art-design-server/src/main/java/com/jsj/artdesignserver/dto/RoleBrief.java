package com.jsj.artdesignserver.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleBrief {
    private Long id;
    private String roleName;
    private String roleCode;
    private Integer status;
}

