package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.dto.BaseResponse;
import com.elexvx.acc.dto.PaginatedResponse;
import com.elexvx.acc.model.User;
import com.elexvx.acc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping("/info")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<Map<String, Object>>> info() {
        Long uid = StpUtil.getLoginIdAsLong();
        Optional<User> opt = userService.findById(uid);
        if (opt.isEmpty()) {
            return ResponseEntity.ok(BaseResponse.error(404, "用户不存在"));
        }
        User u = opt.get();
        List<String> roles;
        if (u.getRoles() != null && !u.getRoles().isEmpty()) {
            roles = u.getRoles().stream().map(r -> Optional.ofNullable(r.getRoleCode()).orElse("R_USER")).toList();
        } else if (Objects.equals(u.getRole(), "admin")) {
            roles = Arrays.asList("R_SUPER", "R_ADMIN");
        } else {
            roles = Collections.singletonList("R_USER");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("buttons", Arrays.asList("add", "edit", "delete"));
        data.put("roles", roles);
        data.put("userId", u.getId());
        data.put("userName", u.getUsername());
        data.put("email", "");
        data.put("avatar", "");
        return ResponseEntity.ok(BaseResponse.ok(data));
    }

    @GetMapping("/list")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<PaginatedResponse<Map<String, Object>>>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<User> all = userService.listAll();
        int total = all.size();
        int fromIndex = Math.max((current - 1) * size, 0);
        int toIndex = Math.min(fromIndex + size, total);
        List<User> pageItems = fromIndex < toIndex ? all.subList(fromIndex, toIndex) : Collections.emptyList();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(fmt);

        List<Map<String, Object>> records = new ArrayList<>();
        for (User u : pageItems) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getId());
            item.put("avatar", Optional.ofNullable(u.getAvatarUrl()).orElse(""));
            item.put("status", Optional.ofNullable(u.getStatus()).map(Object::toString).orElse("1"));
            item.put("userName", u.getUsername());
            item.put("userGender", Optional.ofNullable(u.getGender()).map(Object::toString).orElse("0"));
            item.put("nickName", Optional.ofNullable(u.getNickname()).orElse(u.getUsername()));
            item.put("userPhone", "");
            item.put("userEmail", "");
            List<String> roles;
            if (u.getRoles() != null && !u.getRoles().isEmpty()) {
                roles = u.getRoles().stream().map(r -> Optional.ofNullable(r.getRoleCode()).orElse("R_USER")).toList();
            } else if (Objects.equals(u.getRole(), "admin")) {
                roles = Arrays.asList("R_SUPER", "R_ADMIN");
            } else {
                roles = Collections.singletonList("R_USER");
            }
            item.put("userRoles", roles);
            item.put("createBy", "system");
            item.put("createTime", now);
            item.put("updateBy", "system");
            item.put("updateTime", now);
            records.add(item);
        }

        PaginatedResponse<Map<String, Object>> page = new PaginatedResponse<>(records, current, size, total);
        return ResponseEntity.ok(BaseResponse.ok(page));
    }
}
