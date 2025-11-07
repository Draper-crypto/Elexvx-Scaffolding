package com.elexvx.acc.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateRequest {
    @NotBlank
    @Size(max = 64)
    private String username;

    @NotBlank
    @Size(max = 128)
    private String password;

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

    private Integer status = 1;

    private List<Long> roleIds;
}

