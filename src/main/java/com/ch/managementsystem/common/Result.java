package com.ch.managementsystem.common; // 定义当前类所在的包路径，用于存放系统中的通用返回结果类

import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter、toString等方法

@Data // Lombok注解，自动生成getter、setter、equals、hashCode和toString方法
public class Result<T> { // 定义一个泛型返回结果类，用于统一封装接口返回数据

    private Integer code; // 状态码，用于表示接口请求结果状态（如200成功、500错误等）

    private String message; // 返回信息，用于描述接口执行结果

    private T data; // 返回数据，使用泛型T可以适配不同类型的数据返回

    public static <T> Result<T> success() { // 定义成功返回方法（无数据）
        return new Result<>(200, "成功", null); // 返回状态码200，提示信息为成功，数据为空
    }

    public static <T> Result<T> success(T data) { // 定义成功返回方法（带数据）
        return new Result<>(200, "成功", data); // 返回状态码200，提示信息为成功，并返回具体数据
    }

    public static <T> Result<T> error(String msg) { // 定义错误返回方法（默认状态码500）
        return new Result<>(500, msg, null); // 返回状态码500，提示信息为传入的错误信息
    }

    public static <T> Result<T> error(Integer code, String msg) { // 定义错误返回方法（自定义状态码）
        return new Result<>(code, msg, null); // 返回指定状态码和错误信息
    }

    private Result(Integer code, String message, T data) { // 定义私有构造方法，用于初始化返回结果对象
        this.code = code; // 给code字段赋值
        this.message = message; // 给message字段赋值
        this.data = data; // 给data字段赋值
    }
}