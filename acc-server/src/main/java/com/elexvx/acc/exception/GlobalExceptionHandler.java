package com.elexvx.acc.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.DisableServiceException;
import com.elexvx.acc.dto.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<BaseResponse<Object>> handleNotLogin(NotLoginException e) {
        return ResponseEntity.status(401).body(BaseResponse.error(401, "未登录或登录已过期"));
    }

    @ExceptionHandler({NotPermissionException.class, NotRoleException.class, DisableServiceException.class})
    public ResponseEntity<BaseResponse<Object>> handleForbidden(RuntimeException e) {
        return ResponseEntity.status(403).body(BaseResponse.error(403, "没有权限访问该资源"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(BaseResponse.error(400, msg.isEmpty() ? "请求参数错误" : msg));
    }

    /**
     * 处理数据库唯一约束等数据完整性异常，返回 400 业务错误，避免 500 内部错误
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataIntegrity(DataIntegrityViolationException e) {
        String lowerMsg = (e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage());
        String msg = lowerMsg != null && lowerMsg.contains("Duplicate") ? "数据已存在或唯一约束冲突" : "数据完整性约束错误";
        return ResponseEntity.status(400).body(BaseResponse.error(400, msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGeneric(Exception e) {
        return ResponseEntity.status(500).body(BaseResponse.error(500, "服务内部错误"));
    }
}
