package com.elexvx.acc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结构：{ code, msg, data }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(200, "success", data);
    }

    public static <T> BaseResponse<T> ok(String msg, T data) {
        return new BaseResponse<>(200, msg, data);
    }

    public static <T> BaseResponse<T> error(int code, String msg) {
        return new BaseResponse<>(code, msg, null);
    }
}

