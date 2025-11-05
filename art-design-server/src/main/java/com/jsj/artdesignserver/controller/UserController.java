package com.jsj.artdesignserver.controller;

import com.jsj.artdesignserver.dto.PaginatedResponse;
import com.jsj.artdesignserver.dto.SystemManageUserItem;
import com.jsj.artdesignserver.dto.SystemManageUserSaveRequest;
import com.jsj.artdesignserver.entity.SysRole;
import com.jsj.artdesignserver.entity.SysUser;
import com.jsj.artdesignserver.repository.SysRoleRepository;
import com.jsj.artdesignserver.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserRepository userRepo;
    private final SysRoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public ResponseEntity<PaginatedResponse<SystemManageUserItem>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            // 可选搜索条件（与前端保持一致）
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userPhone,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String userGender,
            @RequestParam(required = false) String status
    ) {
        int pageIndex = Math.max(current - 1, 0);

        Specification<SysUser> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userName != null && !userName.isBlank()) {
                predicates.add(cb.like(root.get("username"), "%" + userName.trim() + "%"));
            }
            if (userPhone != null && !userPhone.isBlank()) {
                predicates.add(cb.like(root.get("phone"), "%" + userPhone.trim() + "%"));
            }
            if (userEmail != null && !userEmail.isBlank()) {
                predicates.add(cb.like(root.get("email"), "%" + userEmail.trim() + "%"));
            }
            if (userGender != null && !userGender.isBlank()) {
                try {
                    predicates.add(cb.equal(root.get("gender"), Integer.parseInt(userGender)));
                } catch (NumberFormatException ignored) {}
            }
            if (status != null && !status.isBlank()) {
                try {
                    // status: '1'启用 -> 1, '2'禁用/注销 -> 0
                    Integer st = Objects.equals(status, "1") ? 1 : 0;
                    predicates.add(cb.equal(root.get("status"), st));
                } catch (Exception ignored) {}
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SysUser> page = userRepo.findAll(spec, PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "id")));

        List<SystemManageUserItem> records = page.getContent().stream()
                .map(SystemManageUserItem::from)
                .collect(Collectors.toList());

        PaginatedResponse<SystemManageUserItem> resp = PaginatedResponse.<SystemManageUserItem>builder()
                .records(records)
                .current(current)
                .size(size)
                .total(page.getTotalElements())
                .build();
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<SystemManageUserItem> create(@RequestBody SystemManageUserSaveRequest req) {
        // 手机号唯一校验
        if (req.getUserPhone() != null && !req.getUserPhone().isBlank()) {
            String phone = req.getUserPhone().trim();
            if (userRepo.existsByPhone(phone)) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.CONFLICT, "手机号已存在");
            }
        }

        SysUser user = SysUser.builder()
                .username(req.getUserName())
                .passwordHash(passwordEncoder.encode("admin123")) // 默认密码，可改为随机生成
                .phone(req.getUserPhone())
                .email(req.getUserEmail())
                .gender(parseIntOrNull(req.getUserGender()))
                .status(parseStatus(req.getStatus()))
                .build();

        // 设置角色
        user.setRoles(new HashSet<>());
        if (req.getUserRoles() != null) {
            req.getUserRoles().forEach(code -> roleRepo.findByRoleCode(code).ifPresent(user.getRoles()::add));
        }

        SysUser saved = userRepo.save(user);
        return ResponseEntity.ok(SystemManageUserItem.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SystemManageUserItem> update(@PathVariable Long id, @RequestBody SystemManageUserSaveRequest req) {
        SysUser user = userRepo.findById(id).orElseThrow();
        if (req.getUserName() != null && !req.getUserName().isBlank()) {
            user.setUsername(req.getUserName());
        }
        if (req.getUserPhone() != null && !req.getUserPhone().isBlank()) {
            String phone = req.getUserPhone().trim();
            var existing = userRepo.findByPhone(phone);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.CONFLICT, "手机号已存在");
            }
            user.setPhone(phone);
        } else {
            user.setPhone(null);
        }
        user.setEmail(req.getUserEmail());
        if (req.getUserGender() != null) {
            user.setGender(parseIntOrNull(req.getUserGender()));
        }
        if (req.getStatus() != null) {
            user.setStatus(parseStatus(req.getStatus()));
        }

        if (req.getUserRoles() != null) {
            var roles = new HashSet<SysRole>();
            req.getUserRoles().forEach(code -> roleRepo.findByRoleCode(code).ifPresent(roles::add));
            user.setRoles(roles);
        }

        SysUser saved = userRepo.save(user);
        return ResponseEntity.ok(SystemManageUserItem.from(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    private Integer parseIntOrNull(String s) {
        try { return s == null ? null : Integer.parseInt(s); } catch (Exception e) { return null; }
    }

    private Integer parseStatus(String s) {
        if (s == null) return 1;
        return "1".equals(s) ? 1 : 0;
    }
}
