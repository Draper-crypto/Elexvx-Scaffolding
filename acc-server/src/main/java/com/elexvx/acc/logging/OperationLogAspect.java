package com.elexvx.acc.logging;

import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.entity.SysUser;
import com.elexvx.acc.repo.SysUserRepository;
import com.elexvx.acc.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

@Aspect
@Component
public class OperationLogAspect {
  private final OperationLogService operationLogService;
  private final SysUserRepository userRepository;

  public OperationLogAspect(OperationLogService operationLogService, SysUserRepository userRepository) {
    this.operationLogService = operationLogService;
    this.userRepository = userRepository;
  }

  @AfterReturning(pointcut = "@annotation(operationLog)")
  public void afterReturning(JoinPoint joinPoint, OperationLog operationLog) {
    record(joinPoint, operationLog, null);
  }

  @AfterThrowing(pointcut = "@annotation(operationLog)", throwing = "ex")
  public void afterThrowing(JoinPoint joinPoint, OperationLog operationLog, Throwable ex) {
    record(joinPoint, operationLog, ex);
  }

  private void record(JoinPoint joinPoint, OperationLog annotation, Throwable throwable) {
    HttpServletRequest request = getCurrentRequest();
    Long userId = null;
    String username = null;
    if (StpUtil.isLogin()) {
      userId = StpUtil.getLoginIdAsLong();
      username = userRepository.findById(userId).map(SysUser::getUsername).orElse(null);
    }
    boolean success = throwable == null;
    String errorMessage = throwable == null ? null : truncate(throwable.getMessage(), 500);
    String detail = resolveDetail(annotation.detail(), joinPoint);
    operationLogService.record(
        annotation.type(),
        annotation.value(),
        detail,
        request,
        userId,
        username,
        success,
        errorMessage);
  }

  private String resolveDetail(String template, JoinPoint joinPoint) {
    if (template == null || template.isBlank()) {
      return "";
    }
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = methodSignature.getParameterNames();
    Object[] args = joinPoint.getArgs();
    String result = template;
    if (parameterNames != null) {
      for (int i = 0; i < parameterNames.length; i++) {
        String placeholder = "{{" + parameterNames[i] + "}}";
        if (result.contains(placeholder)) {
          result = result.replace(placeholder, stringifyArg(args[i]));
        }
      }
    }
    return result;
  }

  private String stringifyArg(Object arg) {
    if (arg == null) {
      return "null";
    }
    if (arg instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) arg;
      return request.getRequestURI();
    }
    if (arg instanceof Throwable throwable) {
      return throwable.getMessage();
    }
    if (arg instanceof Collection<?>) {
      return stringifyCollection((Collection<?>) arg);
    }
    if (arg instanceof Map<?, ?> map) {
      return stringifyMap(map);
    }
    if (arg.getClass().isArray()) {
      return stringifyArray(arg);
    }
    return Objects.toString(arg);
  }

  private String stringifyCollection(Collection<?> collection) {
    StringJoiner joiner = new StringJoiner(", ", "[", "]");
    for (Object item : collection) {
      joiner.add(stringifyArg(item));
    }
    return joiner.toString();
  }

  private String stringifyMap(Map<?, ?> map) {
    StringJoiner joiner = new StringJoiner(", ", "{", "}");
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      joiner.add(stringifyArg(entry.getKey()) + "=" + stringifyArg(entry.getValue()));
    }
    return joiner.toString();
  }

  private String stringifyArray(Object array) {
    int length = Array.getLength(array);
    StringJoiner joiner = new StringJoiner(", ", "[", "]");
    for (int i = 0; i < length; i++) {
      joiner.add(stringifyArg(Array.get(array, i)));
    }
    return joiner.toString();
  }

  private HttpServletRequest getCurrentRequest() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attributes != null ? attributes.getRequest() : null;
  }

  private String truncate(String value, int max) {
    if (value == null) {
      return null;
    }
    return value.length() <= max ? value : value.substring(0, max);
  }
}
