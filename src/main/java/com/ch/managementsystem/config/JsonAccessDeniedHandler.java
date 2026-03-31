package com.ch.managementsystem.config; // 定义当前类所在的包路径，表示该类属于系统配置(config)模块

import com.ch.managementsystem.common.Result; // 导入项目中统一的接口返回结果封装类Result
import com.fasterxml.jackson.databind.ObjectMapper; // 导入Jackson的ObjectMapper，用于将Java对象转换为JSON字符串
import jakarta.servlet.ServletException; // 导入Servlet异常类，用于处理Servlet相关异常
import jakarta.servlet.http.HttpServletRequest; // 导入HttpServletRequest，用于获取客户端请求信息
import jakarta.servlet.http.HttpServletResponse; // 导入HttpServletResponse，用于向客户端返回响应数据
import org.springframework.security.access.AccessDeniedException; // 导入Spring Security的访问拒绝异常类
import org.springframework.security.web.access.AccessDeniedHandler; // 导入Spring Security访问拒绝处理接口
import org.springframework.stereotype.Component; // 导入Spring组件注解，用于将该类注册为Spring容器中的Bean

import java.io.IOException; // 导入IO异常类，用于处理输入输出异常

@Component // 标记该类为Spring组件，让SpringBoot在启动时自动扫描并注入到容器中
public class JsonAccessDeniedHandler implements AccessDeniedHandler { // 定义访问拒绝处理类，实现Spring Security的AccessDeniedHandler接口

    private final ObjectMapper objectMapper = new ObjectMapper(); // 创建ObjectMapper对象，用于将Java对象转换为JSON格式字符串返回给前端

    @Override // 重写AccessDeniedHandler接口中的handle方法
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException { // 当用户访问没有权限的接口时会执行该方法

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 设置HTTP响应状态码为403，表示禁止访问（没有权限）

        response.setContentType("application/json;charset=UTF-8"); // 设置响应数据类型为JSON，并指定字符编码为UTF-8，防止中文乱码

        response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "无权限访问该功能"))); // 将错误信息封装为Result对象并转换为JSON字符串写入响应体返回给前端
    }
}