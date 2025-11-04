package com.jsj.artdesignserver.dto;

import com.jsj.artdesignserver.entity.SysRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemManageRoleItem {
    private Long roleId;
    private String roleName;
    private String roleCode;
    private String description;
    private Boolean enabled;
    private String createBy;
    private String createTime;
    private String updateBy;
    private String updateTime;

    public static SystemManageRoleItem from(SysRole r) {
        return SystemManageRoleItem.builder()
                .roleId(r.getId())
                .roleName(r.getRoleName())
                .roleCode(r.getRoleCode())
                .description(r.getDescription())
                .enabled(r.getStatus() != null && r.getStatus() == 1)
                .createBy("system")
                .createTime("")
                .updateBy("")
                .updateTime("")
                .build();
    }
}

