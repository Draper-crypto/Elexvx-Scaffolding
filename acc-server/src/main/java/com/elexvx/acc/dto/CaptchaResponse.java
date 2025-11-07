package com.elexvx.acc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse {
    private String captchaToken;
    private String imageBase64;
    private int expireInSeconds;
}

