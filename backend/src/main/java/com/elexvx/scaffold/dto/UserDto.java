package com.elexvx.scaffold.dto;

import java.time.LocalDateTime;
import java.util.List;

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

    public static class RoleBrief {
        public Long id;
        public String roleName;
        public String roleCode;
        public Integer status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPresenceStatus() { return presenceStatus; }
    public void setPresenceStatus(Integer presenceStatus) { this.presenceStatus = presenceStatus; }
    public List<RoleBrief> getRoles() { return roles; }
    public void setRoles(List<RoleBrief> roles) { this.roles = roles; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

