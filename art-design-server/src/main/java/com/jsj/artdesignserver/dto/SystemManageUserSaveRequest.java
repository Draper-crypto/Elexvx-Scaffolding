package com.jsj.artdesignserver.dto;

import lombok.Data;

import java.util.List;

/**
 * 前端用户管理保存/更新请求
 * 字段命名与前端 Api.SystemManage.UserListItem 保持一致，便于对接
 */
@Data
public class SystemManageUserSaveRequest {
    private String userName;
    private String userPhone;
    private String userEmail;
    /** '0'未知/'1'男/'2'女 */
    private String userGender;
    /** '1'启用 '2'禁用（或注销） */
    private String status;
    /** 角色编码列表，如 ["R_ADMIN", "R_USER"] */
    private List<String> userRoles;
}

