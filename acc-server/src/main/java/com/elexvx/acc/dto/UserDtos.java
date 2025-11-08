package com.elexvx.acc.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserDtos {
  public static class UserListItem {
    public Long id;
    public String avatar;
    public String status; // '1' 启用 / '2' 禁用
    public String userName;
    public String userGender;
    public String nickName;
    public String userPhone;
    public String userEmail;
    public java.util.List<String> userRoles;
    public String createBy;
    public String createTime;
    public String updateBy;
    public String updateTime;
  }

  public static class RoleBrief {
    public Long id;
    public String roleName;
    public String roleCode;
    public Integer status;
  }

  public static class UserDetail {
    public Long id;
    public String username;
    public String name;
    public String nickname;
    public Integer gender;
    public String email;
    public String phone;
    public String avatarUrl;
    public String address;
    public String bio;
    public Integer status;
    public Integer presenceStatus;
    public List<RoleBrief> roles;
    public List<String> permissions;
    public LocalDateTime createdAt;
  }

  public static class UserCreateRequest {
    public String username;
    public String password;
    public String name;
    public String nickname;
    public Integer gender;
    public String email;
    public String phone;
    public String address;
    public Integer status;
    public List<Long> roleIds;
  }

  public static class UserUpdateRequest {
    public String name;
    public String nickname;
    public Integer gender;
    public String email;
    public String phone;
    public String address;
    public Integer status;
  }

  public static class SetUserRolesRequest { public List<Long> roleIds; }
  public static class StatusUpdateRequest { public Integer status; }
  public static class ResetPasswordRequest { public String newPassword; }

  public static class UserProfileUpdateRequest {
    public String name;
    public String nickname;
    public Integer gender;
    public String email;
    public String phone;
    public String address;
    public String bio;
  }

  public static class UserChangePasswordRequest {
    public String currentPassword;
    public String newPassword;
  }
}
