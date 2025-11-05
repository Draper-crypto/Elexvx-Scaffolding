package com.jsj.artdesignserver.controller;

import com.jsj.artdesignserver.dto.CaptchaResponse;
import com.jsj.artdesignserver.dto.LoginRequest;
import com.jsj.artdesignserver.dto.LoginResponse;
import com.jsj.artdesignserver.dto.UserDto;
import com.jsj.artdesignserver.service.AuthService;
import com.jsj.artdesignserver.service.CaptchaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CaptchaService captchaService;
    private final AuthService authService;

    public AuthController(CaptchaService captchaService, AuthService authService) {
        this.captchaService = captchaService;
        this.authService = authService;
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> captcha() {
        return ResponseEntity.ok(captchaService.createCaptcha());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // 前端删除 Token；如需黑名单可在此实现
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            // 匿名用户或非预期的 principal 类型，一律视为未认证
            return ResponseEntity.status(401).build();
        }
        Long userId = (Long) principal;
        return ResponseEntity.ok(authService.currentUser(userId));
    }
}
