package com.elexvx.acc.dto.admin;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String nickname;

    private Integer gender;

    @Size(max = 255)
    private String email;

    @Size(max = 30)
    private String phone;

    @Size(max = 255)
    private String address;

    private Integer status;
}

