package com.ch.managementsystem.common.exception; // 定义当前类所在的包路径，用于存放系统中的自定义异常类

public class BizException extends RuntimeException { // 定义业务异常类BizException，继承RuntimeException表示运行时异常

    private final Integer code; // 定义异常状态码，用于标识不同类型的业务错误

    public BizException(String message) { // 定义只包含错误信息的构造方法
        super(message); // 调用父类RuntimeException的构造方法，将错误信息传递给父类
        this.code = 500; // 默认设置异常状态码为500，表示服务器内部错误
    }

    public BizException(Integer code, String message) { // 定义包含错误状态码和错误信息的构造方法
        super(message); // 调用父类RuntimeException的构造方法，传递错误信息
        this.code = code; // 设置自定义的异常状态码
    }

    public Integer getCode() { // 获取异常状态码的方法
        return code; // 返回当前异常对象的状态码
    }
}