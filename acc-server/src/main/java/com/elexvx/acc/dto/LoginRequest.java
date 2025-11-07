package com.elexvx.acc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonAlias;

@Data
public class LoginRequest {
    @NotBlank
    @JsonAlias({"userName", "username"})
    private String username;

    @NotBlank
    private String password;

    // 可选验证码
    private String captchaToken;
    private String captchaCode;
}
