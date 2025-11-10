package com.elexvx.acc.service;

import com.elexvx.acc.common.PageResponse;
import com.elexvx.acc.dto.OperationLogDtos.OperationLogItem;
import com.elexvx.acc.dto.OperationLogDtos.OperationLogTypeMeta;
import com.elexvx.acc.entity.SysOperationLog;
import com.elexvx.acc.entity.SysUser;
import com.elexvx.acc.logging.OperationLogType;
import com.elexvx.acc.repo.SysOperationLogRepository;
import com.elexvx.acc.repo.SysUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OperationLogService {
  private final SysOperationLogRepository logRepository;
  private final SysUserRepository userRepository;

  public OperationLogService(SysOperationLogRepository logRepository, SysUserRepository userRepository) {
    this.logRepository = logRepository;
    this.userRepository = userRepository;
  }

  public void record(OperationLogType type,
                     String summary,
                     String detail,
                     HttpServletRequest request,
                     Long userId,
                     String username,
                     boolean success,
                     String errorMessage) {
    SysOperationLog log = new SysOperationLog();
    log.setActionType(type == null ? OperationLogType.OTHER : type);
    log.setActionName(log.getActionType().getLabel());
    log.setActionSummary(truncate(summary, 255));
    String resolvedDetail = detail == null || detail.isBlank() ? log.getActionSummary() : detail;
    log.setActionDetail(truncate(resolvedDetail, 1000));
    log.setColorHex(log.getActionType().getColorHex());
    log.setTagType(log.getActionType().getTagType());
    log.setSuccess(success);
    log.setErrorMessage(truncate(errorMessage, 1000));
    log.setCreatedAt(LocalDateTime.now());

    if (userId != null) {
      log.setUserId(userId);
    }
    String resolvedUsername = username;
    if ((resolvedUsername == null || resolvedUsername.isBlank()) && userId != null) {
      resolvedUsername = userRepository.findById(userId).map(SysUser::getUsername).orElse(null);
    }
    log.setUsername(truncate(resolvedUsername, 100));

    if (request != null) {
      log.setRequestMethod(request.getMethod());
      log.setRequestUri(truncate(request.getRequestURI(), 255));
      log.setRequestParams(truncate(extractRequestParams(request), 1000));
      log.setIpAddress(truncate(extractClientIp(request), 64));
      log.setUserAgent(truncate(request.getHeader("User-Agent"), 512));
    }

    logRepository.save(log);
  }

  public PageResponse<OperationLogItem> list(int page, int size, String actionType, String username, Boolean success) {
    int pageIndex = Math.max(page - 1, 0);
    Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Specification<SysOperationLog> specification = (root, query, cb) -> {
      var predicate = cb.conjunction();
      if (actionType != null && !actionType.isBlank()) {
        try {
          OperationLogType type = OperationLogType.fromValue(actionType);
          predicate = cb.and(predicate, cb.equal(root.get("actionType"), type));
        } catch (IllegalArgumentException ignored) {}
      }
      if (username != null && !username.isBlank()) {
        predicate = cb.and(predicate, cb.like(root.get("username"), "%" + username + "%"));
      }
      if (success != null) {
        predicate = cb.and(predicate, cb.equal(root.get("success"), success));
      }
      return predicate;
    };

    Page<SysOperationLog> result = logRepository.findAll(specification, pageable);
    List<OperationLogItem> items = result.getContent().stream()
        .map(this::toItem)
        .collect(Collectors.toList());
    return new PageResponse<>(items, result.getTotalElements(), page, size);
  }

  public List<OperationLogTypeMeta> getTypeMetadata() {
    return Arrays.stream(OperationLogType.values())
        .map(type -> {
          OperationLogTypeMeta meta = new OperationLogTypeMeta();
          meta.actionType = type.name();
          meta.label = type.getLabel();
          meta.colorHex = type.getColorHex();
          meta.tagType = type.getTagType();
          return meta;
        })
        .collect(Collectors.toList());
  }

  private OperationLogItem toItem(SysOperationLog log) {
    OperationLogItem item = new OperationLogItem();
    item.id = log.getId();
    item.userId = log.getUserId();
    item.username = log.getUsername();
    OperationLogType type = log.getActionType();
    item.actionType = type != null ? type.name() : OperationLogType.OTHER.name();
    item.actionTypeLabel = type != null ? type.getLabel() : OperationLogType.OTHER.getLabel();
    item.actionSummary = log.getActionSummary();
    item.actionDetail = log.getActionDetail();
    item.colorHex = log.getColorHex();
    item.tagType = log.getTagType();
    item.requestMethod = log.getRequestMethod();
    item.requestUri = log.getRequestUri();
    item.requestParams = log.getRequestParams();
    item.ipAddress = log.getIpAddress();
    item.userAgent = log.getUserAgent();
    item.success = log.getSuccess() != null ? log.getSuccess() : Boolean.TRUE;
    item.errorMessage = log.getErrorMessage();
    item.createdAt = log.getCreatedAt();
    return item;
  }

  private String extractClientIp(HttpServletRequest request) {
    if (request == null) {
      return null;
    }
    List<String> headers = List.of("X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP");
    for (String header : headers) {
      String value = request.getHeader(header);
      if (value != null && !value.isBlank() && !"unknown".equalsIgnoreCase(value)) {
        return value.split(",")[0].trim();
      }
    }
    return request.getRemoteAddr();
  }

  private String extractRequestParams(HttpServletRequest request) {
    if (request == null) {
      return null;
    }
    if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
      return request.getQueryString();
    }
    Map<String, String[]> map = request.getParameterMap();
    if (map == null || map.isEmpty()) {
      return null;
    }
    List<String> pairs = new ArrayList<>();
    map.forEach((key, values) -> {
      String value = values == null ? "" : String.join(",", values);
      pairs.add(key + "=" + value);
    });
    return String.join("&", pairs);
  }

  private String truncate(String value, int maxLen) {
    if (value == null) {
      return null;
    }
    return value.length() <= maxLen ? value : value.substring(0, maxLen);
  }
}
