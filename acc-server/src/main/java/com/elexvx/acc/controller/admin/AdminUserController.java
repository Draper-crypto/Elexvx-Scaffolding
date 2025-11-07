package com.elexvx.acc.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.elexvx.acc.dto.BaseResponse;
import com.elexvx.acc.dto.PaginatedResponse;
import com.elexvx.acc.dto.UserDto;
import com.elexvx.acc.dto.admin.SetUserRolesRequest;
import com.elexvx.acc.dto.admin.UserCreateRequest;
import com.elexvx.acc.dto.admin.UserUpdateRequest;
import com.elexvx.acc.model.Role;
import com.elexvx.acc.model.User;
import com.elexvx.acc.repository.RoleRepository;
import com.elexvx.acc.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @SaCheckLogin
    public ResponseEntity<BaseResponse<PaginatedResponse<UserDto>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<User> all = userRepository.findAll();
        int total = all.size();
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, total);
        List<User> slice = from < to ? all.subList(from, to) : Collections.emptyList();
        List<UserDto> records = slice.stream().map(UserDto::from).toList();
        PaginatedResponse<UserDto> resp = new PaginatedResponse<>(records, page, size, total);
        return ResponseEntity.ok(BaseResponse.ok(resp));
    }

    @GetMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<UserDto>> detail(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(BaseResponse.ok(UserDto.from(u))))
                .orElseGet(() -> ResponseEntity.ok(BaseResponse.error(404, "用户不存在")));
    }

    @PostMapping
    @SaCheckLogin
    public ResponseEntity<BaseResponse<UserDto>> create(@Valid @RequestBody UserCreateRequest req) {
        // 先校验用户名是否已存在，避免触发数据库唯一约束导致 500
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.ok(BaseResponse.error(400, "用户名已存在"));
        }
        User u = User.builder()
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                // 兼容旧表结构中 role 字段非空约束，默认赋值为 'user'
                .role("user")
                .name(req.getName())
                .nickname(req.getNickname())
                .gender(Optional.ofNullable(req.getGender()).orElse(0))
                .email(req.getEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .status(Optional.ofNullable(req.getStatus()).orElse(1))
                .build();
        if (req.getRoleIds() != null && !req.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(req.getRoleIds());
            u.getRoles().addAll(roles);
        }
        User saved = userRepository.save(u);
        return ResponseEntity.ok(BaseResponse.ok(UserDto.from(saved)));
    }

    @PutMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<UserDto>> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.ok(BaseResponse.error(404, "用户不存在"));
        User u = opt.get();
        if (req.getName() != null) u.setName(req.getName());
        if (req.getNickname() != null) u.setNickname(req.getNickname());
        if (req.getGender() != null) u.setGender(req.getGender());
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getPhone() != null) u.setPhone(req.getPhone());
        if (req.getAddress() != null) u.setAddress(req.getAddress());
        if (req.getStatus() != null) u.setStatus(req.getStatus());
        User saved = userRepository.save(u);
        return ResponseEntity.ok(BaseResponse.ok(UserDto.from(saved)));
    }

    @DeleteMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<Map<String, Object>>> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.ok(BaseResponse.error(404, "用户不存在"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(BaseResponse.ok(Map.of("deleted", true)));
    }

    @PutMapping("/{id}/roles")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<UserDto>> setRoles(@PathVariable Long id, @RequestBody SetUserRolesRequest req) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.ok(BaseResponse.error(404, "用户不存在"));
        User u = opt.get();
        List<Role> roles = roleRepository.findAllById(req.getRoleIds());
        u.getRoles().clear();
        u.getRoles().addAll(roles);
        User saved = userRepository.save(u);
        return ResponseEntity.ok(BaseResponse.ok(UserDto.from(saved)));
    }
}
