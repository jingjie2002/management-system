package com.ch.managementsystem.common.api; // 定义当前类所在的包路径，用于统一管理API相关的公共类

public enum ResultCode { // 定义一个枚举类ResultCode，用于统一管理系统返回状态码和提示信息

    SUCCESS(200, "Success"), // 成功状态码，表示请求处理成功

    ERROR(500, "Error"), // 服务器内部错误状态码，表示系统发生异常

    UNAUTHORIZED(401, "Unauthorized"), // 未授权状态码，表示用户未登录或Token无效

    FORBIDDEN(403, "Forbidden"), // 禁止访问状态码，表示用户没有权限访问该资源

    NOT_FOUND(404, "Not Found"), // 资源未找到状态码，表示请求的接口或资源不存在

    BAD_REQUEST(400, "Bad Request"); // 错误请求状态码，表示客户端请求参数不合法

    private final int code; // 定义状态码字段，用于存储HTTP状态码

    private final String message; // 定义状态信息字段，用于存储返回提示信息

    ResultCode(int code, String message) { // 枚举类构造方法，用于初始化状态码和提示信息
        this.code = code; // 给code字段赋值
        this.message = message; // 给message字段赋值
    }

    public int getCode() { // 获取状态码的方法
        return code; // 返回当前枚举对象的状态码
    }

    public String getMessage() { // 获取状态信息的方法
        return message; // 返回当前枚举对象的提示信息
    }
}