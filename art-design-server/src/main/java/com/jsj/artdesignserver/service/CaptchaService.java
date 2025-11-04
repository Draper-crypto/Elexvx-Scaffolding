package com.jsj.artdesignserver.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jsj.artdesignserver.dto.CaptchaResponse;
import com.jsj.artdesignserver.util.CaptchaUtil;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Service
public class CaptchaService {
    private final Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(3))
            .maximumSize(10000)
            .build();

    public CaptchaResponse createCaptcha() {
        String token = UUID.randomUUID().toString();
        String code = String.valueOf(1000 + (int)(Math.random()*9000));
        cache.put(token, code);
        byte[] img = CaptchaUtil.renderPng(code);
        String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(img);
        return CaptchaResponse.builder()
                .captchaToken(token)
                .imageBase64(base64)
                .expireInSeconds(180)
                .build();
    }

    public boolean validate(String token, String code) {
        if (token == null || code == null) return false;
        String real = cache.getIfPresent(token);
        return real != null && real.equalsIgnoreCase(code);
    }
}

