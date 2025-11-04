package com.jsj.artdesignserver.service;

import com.jsj.artdesignserver.dto.LoginRequest;
import com.jsj.artdesignserver.dto.LoginResponse;
import com.jsj.artdesignserver.dto.UserDto;
import com.jsj.artdesignserver.entity.SysUser;
import com.jsj.artdesignserver.repository.SysUserRepository;
import com.jsj.artdesignserver.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {
    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CaptchaService captchaService;

    public AuthService(SysUserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, CaptchaService captchaService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.captchaService = captchaService;
    }

    public LoginResponse login(LoginRequest req) {
        if (StringUtils.hasText(req.getCaptchaToken())) {
            boolean ok = captchaService.validate(req.getCaptchaToken(), req.getCaptchaCode());
            if (!ok) {
                throw new IllegalArgumentException("验证码错误或过期");
            }
        }
        SysUser user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalStateException("账号已禁用");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("密码错误");
        }
        String token = jwtUtil.generateToken(user);
        return LoginResponse.builder()
                .token(token)
                .user(UserDto.from(user))
                .build();
    }

    public UserDto currentUser(Long userId) {
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return UserDto.from(user);
    }
}

