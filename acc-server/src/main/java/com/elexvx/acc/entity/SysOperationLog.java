package com.elexvx.acc.entity;

import com.elexvx.acc.logging.OperationLogType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_operation_log")
public class SysOperationLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "action_type", length = 40, nullable = false)
  private OperationLogType actionType;

  @Column(name = "action_name", length = 100, nullable = false)
  private String actionName;

  @Column(name = "action_summary", length = 255, nullable = false)
  private String actionSummary;

  @Column(name = "action_detail", length = 1000)
  private String actionDetail;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "username", length = 100)
  private String username;

  @Column(name = "request_method", length = 10)
  private String requestMethod;

  @Column(name = "request_uri", length = 255)
  private String requestUri;

  @Column(name = "request_params", length = 1000)
  private String requestParams;

  @Column(name = "ip_address", length = 64)
  private String ipAddress;

  @Column(name = "user_agent", length = 512)
  private String userAgent;

  @Column(name = "color_hex", length = 32)
  private String colorHex;

  @Column(name = "tag_type", length = 20)
  private String tagType;

  @Column(name = "success_flag")
  private Boolean success;

  @Column(name = "error_message", length = 1000)
  private String errorMessage;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OperationLogType getActionType() {
    return actionType;
  }

  public void setActionType(OperationLogType actionType) {
    this.actionType = actionType;
  }

  public String getActionName() {
    return actionName;
  }

  public void setActionName(String actionName) {
    this.actionName = actionName;
  }

  public String getActionSummary() {
    return actionSummary;
  }

  public void setActionSummary(String actionSummary) {
    this.actionSummary = actionSummary;
  }

  public String getActionDetail() {
    return actionDetail;
  }

  public void setActionDetail(String actionDetail) {
    this.actionDetail = actionDetail;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
  }

  public String getRequestUri() {
    return requestUri;
  }

  public void setRequestUri(String requestUri) {
    this.requestUri = requestUri;
  }

  public String getRequestParams() {
    return requestParams;
  }

  public void setRequestParams(String requestParams) {
    this.requestParams = requestParams;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getColorHex() {
    return colorHex;
  }

  public void setColorHex(String colorHex) {
    this.colorHex = colorHex;
  }

  public String getTagType() {
    return tagType;
  }

  public void setTagType(String tagType) {
    this.tagType = tagType;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
