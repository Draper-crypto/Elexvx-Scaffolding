package com.jsj.artdesignserver.dto;

import com.jsj.artdesignserver.entity.SysRole;
import com.jsj.artdesignserver.entity.SysUser;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemManageUserItem {
    private Long id;
    private String avatar;
    private String status; // '1'启用 '2'禁用
    private String userName;
    private String userGender; // '0'未知/'1'男/'2'女
    private String nickName;
    private String userPhone;
    private String userEmail;
    private List<String> userRoles;
    private String createBy;
    private String createTime;
    private String updateBy;
    private String updateTime;

    public static SystemManageUserItem from(SysUser u) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return SystemManageUserItem.builder()
                .id(u.getId())
                .avatar(Objects.requireNonNullElse(u.getAvatarUrl(), ""))
                .status((u.getStatus() != null && u.getStatus() == 1) ? "1" : "2")
                .userName(Objects.requireNonNullElse(u.getUsername(), ""))
                .userGender(String.valueOf(Objects.requireNonNullElse(u.getGender(), 0)))
                .nickName(Objects.requireNonNullElse(u.getNickname(), ""))
                .userPhone(Objects.requireNonNullElse(u.getPhone(), ""))
                .userEmail(Objects.requireNonNullElse(u.getEmail(), ""))
                .userRoles(u.getRoles().stream().map(SysRole::getRoleCode).filter(Objects::nonNull).collect(Collectors.toList()))
                .createBy("system")
                .createTime(u.getCreatedAt() == null ? "" : u.getCreatedAt().format(fmt))
                .updateBy("")
                .updateTime("")
                .build();
    }
}

