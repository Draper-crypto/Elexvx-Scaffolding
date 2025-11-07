package com.elexvx.acc.dto;

import com.elexvx.acc.model.Role;
import com.elexvx.acc.model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String nickname;
    private Integer gender;
    private String email;
    private String phone;
    private String avatarUrl;
    private String address;
    private String bio;
    private List<String> tags;
    private Integer status;
    private Integer presenceStatus;
    private List<RoleBrief> roles;
    private List<String> permissions; // 预留，后续计算
    private LocalDateTime createdAt;

    @Data
    public static class RoleBrief {
        private Long id;
        private String roleName;
        private String roleCode;
        private Integer status;
    }

    public static UserDto from(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setName(u.getName());
        dto.setNickname(u.getNickname());
        dto.setGender(u.getGender());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setAvatarUrl(u.getAvatarUrl());
        dto.setAddress(u.getAddress());
        dto.setBio(u.getBio());
        dto.setTags(u.getTags() == null ? List.of() : u.getTags().stream().toList());
        dto.setStatus(u.getStatus());
        dto.setPresenceStatus(u.getPresenceStatus());
        dto.setCreatedAt(u.getCreatedAt());
        Set<Role> roles = u.getRoles();
        if (roles != null && !roles.isEmpty()) {
            dto.setRoles(roles.stream().map(r -> {
                RoleBrief rb = new RoleBrief();
                rb.setId(r.getId());
                rb.setRoleName(r.getRoleName());
                rb.setRoleCode(r.getRoleCode());
                rb.setStatus(r.getStatus());
                return rb;
            }).collect(Collectors.toList()));
        } else {
            dto.setRoles(List.of());
        }
        dto.setPermissions(List.of());
        return dto;
    }
}

