package com.elexvx.acc.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.dto.UserDtos.UserDetail;
import com.elexvx.acc.entity.SysUser;
import com.elexvx.acc.repo.SysUserRepository;
import org.springframework.http.ResponseEntity;
import com.elexvx.acc.common.ApiResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
  private final SysUserRepository userRepo;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthController(SysUserRepository userRepo) { this.userRepo = userRepo; }

  public static class LoginRequest { public String username; public String userName; public String password; public String captchaToken; public String captchaCode; }
  public static class LoginResp { public String token; public String refreshToken; }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResp>> login(@RequestBody LoginRequest req) {
    String uname = (req.username != null && !req.username.isEmpty()) ? req.username : req.userName;
    SysUser u = userRepo.findByUsername(uname).orElse(null);
    if (u == null) {
      // 如果无用户，创建默认 admin
      u = new SysUser();
      u.setUsername("admin");
      u.setPasswordHash(encoder.encode("admin123"));
      u.setStatus(1); u.setPresenceStatus(0);
      u.setCreatedAt(LocalDateTime.now()); u.setUpdatedAt(LocalDateTime.now());
      userRepo.save(u);
    }
    if (!encoder.matches(req.password, u.getPasswordHash())) {
      return ResponseEntity.ok(ApiResponse.failure(401, "用户名或密码错误"));
    }
    StpUtil.login(u.getId());
    String token = "Bearer " + StpUtil.getTokenValue();
    LoginResp resp = new LoginResp();
    resp.token = token;
    resp.refreshToken = "";
    return ResponseEntity.ok(ApiResponse.success(resp));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    StpUtil.logout();
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<Map<String, Object>>> me() {
    Long uid = StpUtil.getLoginIdAsLong();
    SysUser u = userRepo.findById(uid).orElseThrow(() -> new RuntimeException("User not found"));
    // 适配前端 Api.Auth.UserInfo
    java.util.Map<String, Object> resp = new java.util.HashMap<>();
    resp.put("buttons", java.util.Collections.emptyList());
    resp.put("roles", java.util.Collections.singletonList("R_SUPER"));
    resp.put("userId", u.getId());
    resp.put("userName", u.getUsername());
    resp.put("email", u.getEmail() == null ? "" : u.getEmail());
    resp.put("avatar", u.getAvatarUrl() == null ? "" : u.getAvatarUrl());
    return ResponseEntity.ok(ApiResponse.success(resp));
  }

  @GetMapping("/captcha")
  public ResponseEntity<ApiResponse<Map<String, Object>>> captcha() {
    String token = "ct-" + System.currentTimeMillis();
    String imageBase64 = Base64.getEncoder().encodeToString("captcha".getBytes());
    java.util.Map<String, Object> resp = new java.util.HashMap<>();
    resp.put("captchaToken", token);
    resp.put("imageBase64", imageBase64);
    resp.put("expireInSeconds", 180);
    return ResponseEntity.ok(ApiResponse.success(resp));
  }
}
