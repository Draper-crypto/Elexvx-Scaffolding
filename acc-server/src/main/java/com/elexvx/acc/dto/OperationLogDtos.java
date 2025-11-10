package com.elexvx.acc.dto;

import java.time.LocalDateTime;

public class OperationLogDtos {
  public static class OperationLogItem {
    public Long id;
    public Long userId;
    public String username;
    public String actionType;
    public String actionTypeLabel;
    public String actionSummary;
    public String actionDetail;
    public String colorHex;
    public String tagType;
    public String requestMethod;
    public String requestUri;
    public String requestParams;
    public String ipAddress;
    public String userAgent;
    public Boolean success;
    public String errorMessage;
    public LocalDateTime createdAt;
  }

  public static class OperationLogTypeMeta {
    public String actionType;
    public String label;
    public String colorHex;
    public String tagType;
  }
}
