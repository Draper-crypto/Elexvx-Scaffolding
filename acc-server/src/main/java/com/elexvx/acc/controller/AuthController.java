package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.dto.BaseResponse;
import com.elexvx.acc.dto.LoginRequest;
import com.elexvx.acc.dto.UserDto;
import com.elexvx.acc.dto.CaptchaResponse;
import com.elexvx.acc.model.User;
import com.elexvx.acc.service.CaptchaService;
import com.elexvx.acc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CaptchaService captchaService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest req) {
        Optional<User> opt = userService.findByUsername(req.getUsername());
        if (opt.isEmpty() || !userService.checkPassword(opt.get(), req.getPassword())) {
            return ResponseEntity.ok(BaseResponse.error(401, "用户名或密码错误"));
        }
        // 校验验证码（如果传入）
        if (req.getCaptchaToken() != null && req.getCaptchaCode() != null) {
            boolean ok = captchaService.verify(req.getCaptchaToken(), req.getCaptchaCode());
            if (!ok) {
                return ResponseEntity.ok(BaseResponse.error(400, "验证码错误或过期"));
            }
        }
        User user = opt.get();
        StpUtil.login(user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("token", StpUtil.getTokenValue());
        data.put("refreshToken", StpUtil.getTokenValue());
        data.put("user", UserDto.from(user));
        return ResponseEntity.ok(BaseResponse.ok(data));
    }

    @GetMapping("/captcha")
    public ResponseEntity<BaseResponse<CaptchaResponse>> captcha() {
        CaptchaService.Captcha c = captchaService.createCaptcha(100, 40, 180);
        CaptchaResponse resp = new CaptchaResponse(c.token(), c.base64(), c.expireSec());
        return ResponseEntity.ok(BaseResponse.ok(resp));
    }

    @GetMapping("/me")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<UserDto>> me() {
        Long uid = StpUtil.getLoginIdAsLong();
        return userService.findById(uid)
                .map(u -> ResponseEntity.ok(BaseResponse.ok(UserDto.from(u))))
                .orElseGet(() -> ResponseEntity.ok(BaseResponse.error(404, "用户不存在")));
    }

    @PostMapping("/logout")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<Map<String, String>>> logout() {
        StpUtil.logout();
        return ResponseEntity.ok(BaseResponse.ok(Map.of("message", "已退出")));
    }
}
