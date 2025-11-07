package com.elexvx.acc.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SetUserRolesRequest {
    @NotNull
    private List<Long> roleIds;
}

