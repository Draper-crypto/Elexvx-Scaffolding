package com.elexvx.acc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ChangelogCreateRequest {
    @NotBlank
    @Size(max = 32)
    private String version;

    @NotBlank
    @Size(max = 255)
    private String title;

    /** 格式：yyyy-MM-dd */
    private String date;

    private List<String> detail;
    private Boolean requireReLogin;
    @Size(max = 512)
    private String remark;
}

