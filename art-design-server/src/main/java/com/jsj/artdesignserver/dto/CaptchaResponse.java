package com.jsj.artdesignserver.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaptchaResponse {
    private String captchaToken;
    private String imageBase64;
    private int expireInSeconds;
}

