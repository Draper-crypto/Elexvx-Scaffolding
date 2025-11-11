package com.elexvx.acc.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.dto.UserDtos.UserDetail;
import com.elexvx.acc.entity.SysPermission;
import com.elexvx.acc.entity.SysRole;
import com.elexvx.acc.entity.SysRolePermission;
import com.elexvx.acc.entity.SysUser;
import com.elexvx.acc.entity.SysUserRole;
import com.elexvx.acc.repo.SysPermissionRepository;
import com.elexvx.acc.repo.SysRolePermissionRepository;
import com.elexvx.acc.repo.SysRoleRepository;
import com.elexvx.acc.repo.SysUserRepository;
import com.elexvx.acc.repo.SysUserRoleRepository;
import com.elexvx.acc.logging.OperationLog;
import com.elexvx.acc.logging.OperationLogType;
import com.elexvx.acc.service.OperationLogService;
import org.springframework.http.ResponseEntity;
import com.elexvx.acc.common.ApiResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
  private final SysUserRepository userRepo;
  private final SysUserRoleRepository userRoleRepo;
  private final SysRoleRepository roleRepo;
  private final SysRolePermissionRepository rolePermRepo;
  private final SysPermissionRepository permRepo;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
  private final OperationLogService operationLogService;

  public AuthController(SysUserRepository userRepo,
                        SysUserRoleRepository userRoleRepo,
                        SysRoleRepository roleRepo,
                        SysRolePermissionRepository rolePermRepo,
                        SysPermissionRepository permRepo,
                        OperationLogService operationLogService) {
    this.userRepo = userRepo;
    this.userRoleRepo = userRoleRepo;
    this.roleRepo = roleRepo;
    this.rolePermRepo = rolePermRepo;
    this.permRepo = permRepo;
    this.operationLogService = operationLogService;
  }

  public static class LoginRequest { public String username; public String userName; public String password; public String captchaToken; public String captchaCode; }
  public static class LoginResp { public String token; public String refreshToken; }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResp>> login(@RequestBody LoginRequest req, HttpServletRequest request) {
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
      operationLogService.record(
          OperationLogType.LOGIN,
          "登录失败",
          "用户名或密码错误",
          request,
          null,
          uname,
          false,
          "用户名或密码错误");
      return ResponseEntity.ok(ApiResponse.failure(401, "用户名或密码错误"));
    }
    StpUtil.login(u.getId());
    String token = "Bearer " + StpUtil.getTokenValue();
    LoginResp resp = new LoginResp();
    resp.token = token;
    resp.refreshToken = "";
    operationLogService.record(
        OperationLogType.LOGIN,
        "用户登录",
        "登录成功",
        request,
        u.getId(),
        u.getUsername(),
        true,
        null);
    return ResponseEntity.ok(ApiResponse.success(resp));
  }

  @PostMapping("/logout")
  @OperationLog(value = "退出登录", type = OperationLogType.LOGOUT)
  public ResponseEntity<Void> logout() {
    StpUtil.logout();
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<Map<String, Object>>> me() {
    Long uid = StpUtil.getLoginIdAsLong();
    SysUser u = userRepo.findById(uid).orElseThrow(() -> new RuntimeException("User not found"));
    List<SysUserRole> userRoles = userRoleRepo.findByUserId(uid);
    List<Long> roleIds = userRoles.stream()
        .map(SysUserRole::getRoleId)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
    List<String> roleCodes = roleIds.isEmpty()
        ? new java.util.ArrayList<>()
        : roleRepo.findAllById(roleIds).stream()
            .filter(Objects::nonNull)
            .map(SysRole::getRoleCode)
            .filter(code -> code != null && !code.isBlank())
            .map(String::trim)
            .collect(Collectors.toCollection(java.util.ArrayList::new));
    boolean isSuper = roleCodes.stream().anyMatch(code ->
        "R_SUPER".equalsIgnoreCase(code) || "sys_admin".equalsIgnoreCase(code));
    if (!isSuper && "admin".equalsIgnoreCase(u.getUsername())) {
      isSuper = true;
    }
    if (isSuper && roleCodes.stream().noneMatch(code -> "R_SUPER".equalsIgnoreCase(code))) {
      roleCodes.add("R_SUPER");
    }
    List<String> buttons = Collections.emptyList();
    if (!roleIds.isEmpty()) {
      List<Long> permIds = rolePermRepo.findByRoleIdIn(roleIds).stream()
          .map(SysRolePermission::getPermissionId)
          .filter(Objects::nonNull)
          .distinct()
          .collect(Collectors.toList());
      if (!permIds.isEmpty()) {
        buttons = permRepo.findAllById(permIds).stream()
            .filter(Objects::nonNull)
            .map(SysPermission::getPermCode)
            .filter(code -> code != null && !code.isBlank())
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
      }
    }
    // 适配前端 Api.Auth.UserInfo
    java.util.Map<String, Object> resp = new java.util.HashMap<>();
    resp.put("buttons", buttons);
    resp.put("roles", roleCodes);
    resp.put("userId", u.getId());
    resp.put("userName", u.getUsername());
    resp.put("email", u.getEmail() == null ? "" : u.getEmail());
    resp.put("avatar", u.getAvatarUrl() == null ? "" : u.getAvatarUrl());
    resp.put("fullName", u.getName() == null ? "" : u.getName());
    resp.put("nickname", u.getNickname() == null ? "" : u.getNickname());
    String displayName = u.getName();
    if (displayName == null || displayName.isEmpty()) displayName = u.getNickname();
    if (displayName == null || displayName.isEmpty()) displayName = u.getUsername();
    resp.put("displayName", displayName);
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

  /**
   * 公开接口：返回系统中的角色选项（用于登录页下拉）
   * 无需登录即可访问，仅返回角色代码与名称
   */
  @GetMapping("/public/roles")
  public ResponseEntity<ApiResponse<List<Map<String, String>>>> publicRoleOptions() {
    List<Map<String, String>> list = roleRepo.findAll().stream()
        .filter(Objects::nonNull)
        .map(r -> {
          String code = r.getRoleCode() == null ? "" : r.getRoleCode().trim();
          String name = r.getRoleName() == null ? code : r.getRoleName();
          java.util.Map<String, String> m = new java.util.HashMap<>();
          m.put("code", code);
          m.put("name", name);
          return m;
        })
        .filter(m -> m.get("code") != null && !m.get("code").isBlank())
        .collect(java.util.stream.Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list));
  }
}
