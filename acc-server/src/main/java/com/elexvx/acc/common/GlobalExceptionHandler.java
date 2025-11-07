package com.elexvx.acc.common;

import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotLoginException.class)
  public ApiResponse<?> handleNotLogin(NotLoginException e) {
    return ApiResponse.failure(401, "未登录或会话失效");
  }

  @ExceptionHandler(Exception.class)
  public ApiResponse<?> handleGeneric(Exception e) {
    return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
  }
}

