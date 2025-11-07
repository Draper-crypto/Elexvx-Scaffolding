package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.dto.BaseResponse;
import com.elexvx.acc.dto.UserDto;
import com.elexvx.acc.dto.profile.ChangePasswordRequest;
import com.elexvx.acc.dto.profile.ProfileUpdateRequest;
import com.elexvx.acc.model.User;
import com.elexvx.acc.repository.UserRepository;
import com.elexvx.acc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<UserDto>> me() {
        Long uid = StpUtil.getLoginIdAsLong();
        return userService.findById(uid)
                .map(u -> ResponseEntity.ok(BaseResponse.ok(UserDto.from(u))))
                .orElseGet(() -> ResponseEntity.ok(BaseResponse.error(404, "用户不存在")));
    }

    @PutMapping("/me")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<UserDto>> update(@Valid @RequestBody ProfileUpdateRequest req) {
        Long uid = StpUtil.getLoginIdAsLong();
        Optional<User> opt = userService.findById(uid);
        if (opt.isEmpty()) return ResponseEntity.ok(BaseResponse.error(404, "用户不存在"));
        User u = opt.get();
        if (req.getName() != null) u.setName(req.getName());
        if (req.getNickname() != null) u.setNickname(req.getNickname());
        if (req.getGender() != null) u.setGender(req.getGender());
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getPhone() != null) u.setPhone(req.getPhone());
        if (req.getAvatarUrl() != null) u.setAvatarUrl(req.getAvatarUrl());
        if (req.getAddress() != null) u.setAddress(req.getAddress());
        if (req.getBio() != null) u.setBio(req.getBio());
        if (req.getTags() != null) { u.getTags().clear(); u.getTags().addAll(req.getTags()); }
        User saved = userRepository.save(u);
        return ResponseEntity.ok(BaseResponse.ok(UserDto.from(saved)));
    }

    @PutMapping("/password")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        Long uid = StpUtil.getLoginIdAsLong();
        Optional<User> opt = userService.findById(uid);
        if (opt.isEmpty()) return ResponseEntity.ok(BaseResponse.error(404, "用户不存在"));
        User u = opt.get();
        if (!passwordEncoder.matches(req.getOldPassword(), u.getPasswordHash())) {
            return ResponseEntity.ok(BaseResponse.error(400, "原密码不正确"));
        }
        u.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(u);
        return ResponseEntity.ok(BaseResponse.ok("密码已更新"));
    }
}

