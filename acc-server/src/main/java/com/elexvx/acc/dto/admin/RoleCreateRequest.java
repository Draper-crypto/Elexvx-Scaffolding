package com.elexvx.acc.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleCreateRequest {
    @NotBlank
    @Size(max = 100)
    private String roleName;

    @NotBlank
    @Size(max = 100)
    private String roleCode;

    @Size(max = 255)
    private String description;

    private Integer status = 1;
}

