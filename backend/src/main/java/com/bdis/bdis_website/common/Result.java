package com.bdis.bdis_website.common;

import lombok.Data;

// 统一返回结果类，支持泛型
@Data
public class Result<T> {
    // 状态码：0成功，1失败
    private Integer code;
    // 提示信息
    private String message;
    // 数据（泛型，支持任意类型）
    private T data;

    // 私有构造，不让外部直接创建
    private Result() {}

    // 成功：带数据
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("ok");
        result.setData(data);
        return result;
    }

    // 成功：不带数据
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败：自定义提示
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}


