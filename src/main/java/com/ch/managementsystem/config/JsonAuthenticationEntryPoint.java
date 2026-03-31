package com.ch.managementsystem.config; // 定义当前类所在的包路径，表示该类属于系统配置(config)模块

import com.ch.managementsystem.common.Result; // 导入系统统一返回结果类Result，用于封装接口返回数据
import com.fasterxml.jackson.databind.ObjectMapper; // 导入Jackson中的ObjectMapper，用于将Java对象转换为JSON字符串
import jakarta.servlet.ServletException; // 导入Servlet异常类，用于处理Servlet相关异常
import jakarta.servlet.http.HttpServletRequest; // 导入HttpServletRequest，用于获取客户端发送的HTTP请求信息
import jakarta.servlet.http.HttpServletResponse; // 导入HttpServletResponse，用于向客户端返回HTTP响应数据
import org.springframework.security.core.AuthenticationException; // 导入Spring Security中的认证异常类，当认证失败时会抛出该异常
import org.springframework.security.web.AuthenticationEntryPoint; // 导入Spring Security认证入口接口，用于处理未认证访问
import org.springframework.stereotype.Component; // 导入Spring组件注解，用于将该类注册为Spring管理的Bean

import java.io.IOException; // 导入IO异常类，用于处理输入输出异常

@Component // 标记该类为Spring组件，使其在SpringBoot启动时自动扫描并注册到Spring容器中
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint { // 定义未认证处理类，实现AuthenticationEntryPoint接口

    private final ObjectMapper objectMapper = new ObjectMapper(); // 创建ObjectMapper对象，用于将Java对象转换为JSON字符串返回给前端

    @Override // 重写AuthenticationEntryPoint接口中的commence方法
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException { // 当用户未登录或认证失败访问受保护接口时会执行该方法

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置HTTP响应状态码为401，表示未授权（用户未登录或认证失败）

        response.setContentType("application/json;charset=UTF-8"); // 设置响应数据类型为JSON格式，并指定字符编码为UTF-8，防止中文乱码

        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "未登录或登录已过期，请重新登录"))); // 将错误信息封装为Result对象并转换为JSON字符串，然后写入响应体返回给前端
    }
}