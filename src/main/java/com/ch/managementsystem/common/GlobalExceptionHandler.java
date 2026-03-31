package com.ch.managementsystem.common; // 定义当前类所在的包路径，用于存放系统中的公共组件类

import com.ch.managementsystem.common.exception.BizException; // 导入自定义业务异常类
import org.slf4j.Logger; // 导入日志接口Logger
import org.slf4j.LoggerFactory; // 导入日志工厂类，用于创建Logger对象
import org.springframework.security.access.AccessDeniedException; // 导入Spring Security权限不足异常
import org.springframework.security.authentication.BadCredentialsException; // 导入Spring Security登录失败异常
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler; // 导入异常处理注解，用于指定处理哪种异常
import org.springframework.web.bind.annotation.RestControllerAdvice; // 导入全局异常处理注解，用于统一处理Controller层异常

@RestControllerAdvice // 声明当前类为全局异常处理类，可以拦截所有Controller抛出的异常
public class GlobalExceptionHandler { // 定义全局异常处理器类

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class); // 创建日志对象，用于记录系统异常信息

    @ExceptionHandler(Exception.class) // 指定处理所有Exception类型的异常
    public Result<?> handleException(Exception e) { // 定义异常处理方法，接收Exception对象
        log.error("System Error: ", e); // 记录系统错误日志，打印异常堆栈信息
        return Result.error("系统异常，请稍后重试"); // 返回统一的错误响应结果
    }

    @ExceptionHandler(RuntimeException.class) // 指定处理RuntimeException运行时异常
    public Result<?> handleRuntimeException(RuntimeException e) { // 定义运行时异常处理方法
        log.error("Runtime Error: ", e); // 记录运行时异常日志
        return Result.error("运行异常，请稍后重试"); // 返回统一错误信息
    }

    @ExceptionHandler(AccessDeniedException.class) // 指定处理权限不足异常（Spring Security抛出）
    public ResponseEntity<Result<?>> handleAccessDeniedException(AccessDeniedException e) { // 定义权限不足异常处理方法
        log.warn("AccessDenied: {}", e.getMessage()); // 记录警告日志，输出异常信息
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.error(403, "无权限访问该功能")); // 返回403状态码及提示信息
    }

    @ExceptionHandler(BadCredentialsException.class) // 指定处理登录认证失败异常
    public Result<?> handleBadCredentialsException(BadCredentialsException e) { // 定义登录失败异常处理方法
        log.warn("BadCredentials: {}", e.getMessage()); // 记录登录失败日志
        return Result.error(401, "登录失败，请检查用户名和密码"); // 返回401未授权状态码及提示信息
    }

    @ExceptionHandler(BizException.class) // 指定处理自定义业务异常
    public Result<?> handleBizException(BizException e) { // 定义业务异常处理方法
        log.warn("Business Error: {}", e.getMessage()); // 记录业务异常日志
        return Result.error(e.getCode(), e.getMessage()); // 返回业务异常中的状态码和错误信息
    }
}
