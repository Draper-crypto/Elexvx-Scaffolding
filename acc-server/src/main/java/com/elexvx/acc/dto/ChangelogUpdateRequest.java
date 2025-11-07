package com.elexvx.acc.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 更新日志更新请求
 */
@Data
public class ChangelogUpdateRequest {
    @Size(max = 32)
    private String version;

    @Size(max = 255)
    private String title;

    /** 格式：yyyy-MM-dd */
    private String date;

    private List<String> detail;

    private Boolean requireReLogin;

    @Size(max = 512)
    private String remark;
}

