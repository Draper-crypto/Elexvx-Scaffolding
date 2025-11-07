package com.elexvx.acc.dto.profile;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProfileUpdateRequest {
    @Size(max = 100)
    private String name;
    @Size(max = 100)
    private String nickname;
    private Integer gender;
    @Size(max = 255)
    private String email;
    @Size(max = 30)
    private String phone;
    @Size(max = 512)
    private String avatarUrl;
    @Size(max = 255)
    private String address;
    private String bio;
    private List<String> tags;
}

