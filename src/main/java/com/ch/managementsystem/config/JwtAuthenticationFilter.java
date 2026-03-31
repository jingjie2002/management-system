package com.ch.managementsystem.config; // 定义当前类所在的包路径，表示该类属于系统配置(config)模块

import com.ch.managementsystem.service.impl.UserDetailsServiceImpl; // 导入自定义的UserDetailsService实现类，用于根据用户名加载用户信息
import com.ch.managementsystem.utils.JwtUtils; // 导入JWT工具类，用于解析和校验JWT令牌
import jakarta.servlet.FilterChain; // 导入过滤器链对象，用于将请求继续传递给下一个过滤器
import jakarta.servlet.ServletException; // 导入Servlet异常类，用于处理Servlet相关异常
import jakarta.servlet.http.HttpServletRequest; // 导入HttpServletRequest对象，用于获取客户端HTTP请求信息
import jakarta.servlet.http.HttpServletResponse; // 导入HttpServletResponse对象，用于向客户端返回HTTP响应
import org.springframework.beans.factory.annotation.Autowired; // 导入自动注入注解，用于自动注入Spring容器中的Bean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 导入用户名密码认证Token对象，用于封装用户认证信息
import org.springframework.security.core.context.SecurityContextHolder; // 导入Spring Security上下文对象，用于存储当前线程的认证信息
import org.springframework.security.core.userdetails.UserDetails; // 导入Spring Security用户详情接口，用于表示用户信息
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // 导入认证详情构建类，用于构建认证相关的请求信息
import org.springframework.stereotype.Component; // 导入Spring组件注解，用于将该类注册为Spring容器中的Bean
import org.springframework.web.filter.OncePerRequestFilter; // 导入Spring过滤器类，确保每个请求只执行一次该过滤器

import java.io.IOException; // 导入IO异常类，用于处理输入输出异常

@Component // 标记该类为Spring组件，使其在SpringBoot启动时自动扫描并注册到Spring容器中
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 定义JWT认证过滤器类，继承OncePerRequestFilter确保每个请求只执行一次

    @Autowired // 自动注入JwtUtils对象
    private JwtUtils jwtUtils; // JWT工具类，用于解析token和验证token是否合法

    @Autowired // 自动注入UserDetailsService实现类
    private UserDetailsServiceImpl userDetailsService; // 自定义用户服务，用于根据用户名加载用户详细信息

    @Override // 重写OncePerRequestFilter中的核心过滤方法
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException { // 过滤器执行的核心方法，每个请求都会进入这里

        final String authorizationHeader = request.getHeader("Authorization"); // 从请求头中获取Authorization字段（通常用于存放JWT Token）

        String username = null; // 定义用户名变量，用于保存解析出来的用户名
        String jwt = null; // 定义JWT字符串变量，用于保存Token

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) { // 判断请求头是否存在并且是否以"Bearer "开头
            jwt = authorizationHeader.substring(7); // 截取Token字符串，去掉前面的"Bearer "前缀
            try { // 尝试解析Token
                username = jwtUtils.extractUsername(jwt); // 从JWT中解析出用户名
            } catch (Exception e) { // 如果解析Token过程中出现异常
                SecurityContextHolder.clearContext(); // 清空当前安全上下文，表示认证失败
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // 如果成功获取用户名，并且当前安全上下文中还没有认证信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // 根据用户名从数据库中加载用户详细信息

            if (jwtUtils.validateToken(jwt, userDetails.getUsername())) { // 校验JWT Token是否合法以及是否与用户名匹配
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // 创建认证Token对象，并设置用户信息和权限信息
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 将请求相关的认证详情信息设置到Token中
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // 将认证信息存入Spring Security上下文中，表示用户已登录
            }
        }
        chain.doFilter(request, response); // 继续执行过滤器链，将请求传递给下一个过滤器或最终的Controller
    }
}