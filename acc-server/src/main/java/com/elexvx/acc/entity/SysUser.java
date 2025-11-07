package com.elexvx.acc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_user")
public class SysUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username", length = 64, nullable = false, unique = true)
  private String username;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(name = "name", length = 100)
  private String name;

  @Column(name = "nickname", length = 100)
  private String nickname;

  @Column(name = "gender")
  private Integer gender;

  @Column(name = "email", length = 255)
  private String email;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name = "avatar_url", length = 512)
  private String avatarUrl;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "bio")
  private String bio;

  @Column(name = "status")
  private Integer status;

  @Column(name = "presence_status")
  private Integer presenceStatus;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "updated_by")
  private Long updatedBy;

  // getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
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
  public Integer getStatus() { return status; }
  public void setStatus(Integer status) { this.status = status; }
  public Integer getPresenceStatus() { return presenceStatus; }
  public void setPresenceStatus(Integer presenceStatus) { this.presenceStatus = presenceStatus; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public Long getCreatedBy() { return createdBy; }
  public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
  public Long getUpdatedBy() { return updatedBy; }
  public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}

