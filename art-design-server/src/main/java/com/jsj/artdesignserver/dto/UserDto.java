package com.jsj.artdesignserver.dto;

import com.jsj.artdesignserver.entity.SysPermission;
import com.jsj.artdesignserver.entity.SysRole;
import com.jsj.artdesignserver.entity.SysUser;
import com.jsj.artdesignserver.entity.SysUserTag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private List<String> permissions;
    private LocalDateTime createdAt;

    public static UserDto from(SysUser user) {
        List<RoleBrief> roleBriefs = user.getRoles().stream()
                .map(r -> RoleBrief.builder()
                        .id(r.getId())
                        .roleName(r.getRoleName())
                        .roleCode(r.getRoleCode())
                        .status(r.getStatus())
                        .build())
                .collect(Collectors.toList());

        Set<String> perms = user.getRoles().stream()
                .map(SysRole::getPermissions)
                .flatMap(Set::stream)
                .map(SysPermission::getPermCode)
                .collect(Collectors.toSet());

        List<String> tags = user.getUserTags().stream()
                .map(SysUserTag::getTag)
                .collect(Collectors.toList());

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .address(user.getAddress())
                .bio(user.getBio())
                .tags(tags)
                .status(user.getStatus())
                .presenceStatus(user.getPresenceStatus())
                .roles(roleBriefs)
                .permissions(perms.stream().collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}

