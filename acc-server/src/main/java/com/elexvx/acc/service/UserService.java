package com.elexvx.acc.service;

import com.elexvx.acc.dto.UserDtos.*;
import com.elexvx.acc.entity.SysUser;
import com.elexvx.acc.entity.SysUserRole;
import com.elexvx.acc.repo.SysRoleRepository;
import com.elexvx.acc.repo.SysUserRepository;
import com.elexvx.acc.repo.SysUserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {
  private final SysUserRepository userRepo;
  private final SysUserRoleRepository userRoleRepo;
  private final SysRoleRepository roleRepo;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public UserService(SysUserRepository userRepo, SysUserRoleRepository userRoleRepo, SysRoleRepository roleRepo) {
    this.userRepo = userRepo;
    this.userRoleRepo = userRoleRepo;
    this.roleRepo = roleRepo;
  }

  public Page<UserListItem> list(int page, int size,
                                 String userName,
                                 String userGender,
                                 String userPhone,
                                 String userEmail,
                                 String status) {
    Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
    Specification<SysUser> spec = (root, query, cb) -> {
      jakarta.persistence.criteria.Predicate predicate = cb.conjunction();
      if (userName != null && !userName.isEmpty()) {
        predicate = cb.and(predicate, cb.like(root.get("username"), "%" + userName + "%"));
      }
      if (userPhone != null && !userPhone.isEmpty()) {
        predicate = cb.and(predicate, cb.like(root.get("phone"), "%" + userPhone + "%"));
      }
      if (userEmail != null && !userEmail.isEmpty()) {
        predicate = cb.and(predicate, cb.like(root.get("email"), "%" + userEmail + "%"));
      }
      if (userGender != null && !userGender.isEmpty()) {
        try {
          Integer g = null;
          if ("男".equals(userGender)) g = 1; else if ("女".equals(userGender)) g = 2; else g = Integer.parseInt(userGender);
          predicate = cb.and(predicate, cb.equal(root.get("gender"), g));
        } catch (Exception ignored) {}
      }
      if (status != null && !status.isEmpty()) {
        try {
          // 前端 '1' 表示启用，其余视为禁用
          Integer s = "1".equals(status) ? 1 : 0;
          predicate = cb.and(predicate, cb.equal(root.get("status"), s));
        } catch (Exception ignored) {}
      }
      return predicate;
    };
    Page<SysUser> p = userRepo.findAll(spec, pageable);
    return p.map(u -> {
      UserListItem item = new UserListItem();
      item.id = u.getId();
      item.avatar = u.getAvatarUrl() == null ? "" : u.getAvatarUrl();
      item.status = (u.getStatus() != null && u.getStatus() == 1) ? "1" : "2";
      item.userName = u.getUsername();
      item.userGender = u.getGender() == null ? "" : String.valueOf(u.getGender());
      item.nickName = u.getNickname() == null ? "" : u.getNickname();
      item.userPhone = u.getPhone() == null ? "" : u.getPhone();
      item.userEmail = u.getEmail() == null ? "" : u.getEmail();
      item.userRoles = userRoleRepo.findByUserId(u.getId()).stream()
          .map(ur -> roleRepo.findById(ur.getRoleId()).map(r -> r.getRoleCode()).orElse(""))
          .filter(s -> !s.isEmpty())
          .collect(Collectors.toList());
      item.createBy = u.getCreatedBy() == null ? "" : String.valueOf(u.getCreatedBy());
      item.createTime = u.getCreatedAt() == null ? "" : String.valueOf(u.getCreatedAt());
      item.updateBy = u.getUpdatedBy() == null ? "" : String.valueOf(u.getUpdatedBy());
      item.updateTime = u.getUpdatedAt() == null ? "" : String.valueOf(u.getUpdatedAt());
      return item;
    });
  }

  public UserDetail get(Long id) {
    SysUser u = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    UserDetail d = new UserDetail();
    d.id = u.getId();
    d.username = u.getUsername();
    d.name = u.getName();
    d.nickname = u.getNickname();
    d.gender = u.getGender();
    d.email = u.getEmail();
    d.phone = u.getPhone();
    d.avatarUrl = u.getAvatarUrl();
    d.address = u.getAddress();
    d.bio = u.getBio();
    d.status = u.getStatus();
    d.presenceStatus = u.getPresenceStatus();
    d.createdAt = u.getCreatedAt();
    d.roles = userRoleRepo.findByUserId(id).stream().map(ur -> {
      com.elexvx.acc.entity.SysRole role = roleRepo.findById(ur.getRoleId()).orElse(null);
      RoleBrief rb = new RoleBrief();
      if (role != null) {
        rb.id = role.getId();
        rb.roleName = role.getRoleName();
        rb.roleCode = role.getRoleCode();
        rb.status = role.getStatus();
      }
      return rb;
    }).collect(Collectors.toList());
    return d;
  }

  @Transactional
  public UserDetail create(UserCreateRequest req) {
    SysUser u = new SysUser();
    u.setUsername(req.username);
    u.setPasswordHash(passwordEncoder.encode(req.password));
    u.setName(req.name);
    u.setNickname(req.nickname);
    u.setGender(req.gender);
    u.setEmail(req.email);
    u.setPhone(req.phone);
    u.setAddress(req.address);
    u.setStatus(req.status != null ? req.status : 1);
    u.setPresenceStatus(0);
    u.setCreatedAt(LocalDateTime.now());
    u.setUpdatedAt(LocalDateTime.now());
    userRepo.save(u);
    if (req.roleIds != null && !req.roleIds.isEmpty()) {
      List<Long> dedupRoleIds = req.roleIds.stream()
          .filter(Objects::nonNull)
          .distinct()
          .collect(Collectors.toList());
      for (Long rid : dedupRoleIds) {
        if (userRoleRepo.existsByUserIdAndRoleId(u.getId(), rid)) continue;
        SysUserRole ur = new SysUserRole();
        ur.setUserId(u.getId());
        ur.setRoleId(rid);
        userRoleRepo.save(ur);
      }
    }
    return get(u.getId());
  }

  @Transactional
  public UserDetail update(Long id, UserUpdateRequest req) {
    SysUser u = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    if (req.name != null) u.setName(req.name);
    if (req.nickname != null) u.setNickname(req.nickname);
    if (req.gender != null) u.setGender(req.gender);
    if (req.email != null) u.setEmail(req.email);
    if (req.phone != null) u.setPhone(req.phone);
    if (req.address != null) u.setAddress(req.address);
    if (req.status != null) u.setStatus(req.status);
    u.setUpdatedAt(LocalDateTime.now());
    userRepo.save(u);
    return get(id);
  }

  @Transactional
  public void delete(Long id) {
    userRoleRepo.deleteByUserId(id);
    userRepo.deleteById(id);
  }

  @Transactional
  public void updateStatus(Long id, StatusUpdateRequest req) {
    SysUser u = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    u.setStatus(req.status);
    u.setUpdatedAt(LocalDateTime.now());
    userRepo.save(u);
  }

  @Transactional
  public void resetPassword(Long id, ResetPasswordRequest req) {
    SysUser u = userRepo.findById(id).orElseThrow();
    u.setPasswordHash(passwordEncoder.encode(req.newPassword));
    u.setUpdatedAt(LocalDateTime.now());
    userRepo.save(u);
  }

  @Transactional
  public void setRoles(Long id, SetUserRolesRequest req) {
    userRoleRepo.deleteByUserId(id);
    if (req.roleIds != null) {
      List<Long> dedupRoleIds = req.roleIds.stream()
          .filter(Objects::nonNull)
          .distinct()
          .collect(Collectors.toList());
      for (Long rid : dedupRoleIds) {
        if (userRoleRepo.existsByUserIdAndRoleId(id, rid)) continue;
        SysUserRole ur = new SysUserRole();
        ur.setUserId(id);
        ur.setRoleId(rid);
        userRoleRepo.save(ur);
      }
    }
  }

  @Transactional
  public UserDetail updateProfile(Long userId, UserProfileUpdateRequest req) {
    SysUser u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    if (req.name != null) u.setName(req.name);
    if (req.nickname != null) u.setNickname(req.nickname);
    if (req.gender != null) u.setGender(req.gender);
    if (req.email != null) u.setEmail(req.email);
    if (req.phone != null) u.setPhone(req.phone);
    if (req.address != null) u.setAddress(req.address);
    if (req.bio != null) u.setBio(req.bio);
    u.setUpdatedAt(LocalDateTime.now());
    userRepo.save(u);
    return get(userId);
  }

  @Transactional
  public void changePassword(Long userId, UserChangePasswordRequest req) {
    SysUser u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    if (req.currentPassword == null || !passwordEncoder.matches(req.currentPassword, u.getPasswordHash())) {
      throw new RuntimeException("当前密码错误");
    }
    if (req.newPassword == null || req.newPassword.length() < 6) {
      throw new RuntimeException("新密码长度不能小于6位");
    }
    u.setPasswordHash(passwordEncoder.encode(req.newPassword));
    u.setUpdatedAt(LocalDateTime.now());
    userRepo.save(u);
  }
}
