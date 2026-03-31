package com.ch.managementsystem.config; // 定义当前类所在的包路径，表示该类属于系统配置(config)模块

import java.util.Arrays; // 导入Arrays工具类，用于快速创建集合
import java.util.List; // 导入List集合接口

import org.springframework.beans.factory.annotation.Autowired; // 导入自动注入注解，用于从Spring容器中自动注入Bean
import org.springframework.context.annotation.Bean; // 导入Bean注解，用于将方法返回对象注册为Spring容器中的Bean
import org.springframework.context.annotation.Configuration; // 导入配置类注解，标识该类是Spring配置类
import org.springframework.security.authentication.AuthenticationManager; // 导入认证管理器接口，用于处理认证逻辑
import org.springframework.security.authentication.AuthenticationProvider; // 导入认证提供者接口，用于自定义认证方式
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // 导入基于数据库的认证提供者实现类
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // 导入认证配置类，用于获取AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // 开启方法级权限控制注解（如@PreAuthorize）
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // 导入HttpSecurity，用于配置Spring Security安全规则
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // 开启Spring Security Web安全功能
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // 导入Http配置工具类，用于禁用默认配置
import org.springframework.security.config.http.SessionCreationPolicy; // 导入Session创建策略枚举
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 导入BCrypt密码加密器
import org.springframework.security.crypto.password.PasswordEncoder; // 导入密码加密接口
import org.springframework.security.web.SecurityFilterChain; // 导入Security过滤器链
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 导入用户名密码认证过滤器
import org.springframework.web.cors.CorsConfiguration; // 导入跨域配置类
import org.springframework.web.cors.CorsConfigurationSource; // 导入跨域配置源接口
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // 导入基于URL的跨域配置源

import com.ch.managementsystem.service.impl.UserDetailsServiceImpl; // 导入自定义用户详情服务实现类，用于加载用户信息

@Configuration // 标记该类为Spring配置类
@EnableWebSecurity // 开启Spring Security安全框架
@EnableMethodSecurity // 开启方法级权限控制（例如@PreAuthorize）
public class SecurityConfig { // 定义Spring Security核心配置类

    @Autowired // 自动注入JWT认证过滤器
    private JwtAuthenticationFilter jwtAuthenticationFilter; // 自定义JWT过滤器，用于验证Token

    @Autowired // 自动注入用户详情服务
    private UserDetailsServiceImpl userDetailsService; // 用于从数据库加载用户信息

    @Autowired // 自动注入未认证处理器
    private JsonAuthenticationEntryPoint authenticationEntryPoint; // 当用户未登录时返回401错误

    @Autowired // 自动注入权限不足处理器
    private JsonAccessDeniedHandler accessDeniedHandler; // 当用户无权限时返回403错误

    @Bean // 将SecurityFilterChain注册为Spring Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // 定义Spring Security的核心安全过滤链
        http // 开始配置HttpSecurity
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 启用CORS跨域配置
            .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF防护（前后端分离项目通常关闭）
            .authorizeHttpRequests(auth -> auth // 配置接口访问权限
                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/captcha", "/api/health", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll() // 这些接口允许所有用户访问（无需登录）
                .anyRequest().authenticated() // 其他所有请求都必须认证（必须登录）
            )
            .sessionManagement(session -> session // 配置Session管理策略
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 设置为无状态Session（使用JWT，不使用Session）
            )
            .exceptionHandling(ex -> ex // 配置异常处理
                .authenticationEntryPoint(authenticationEntryPoint) // 设置未认证时的处理逻辑
                .accessDeniedHandler(accessDeniedHandler) // 设置权限不足时的处理逻辑
            )
            .authenticationProvider(authenticationProvider()) // 设置认证提供者
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 在用户名密码过滤器之前添加JWT过滤器

        return http.build(); // 构建并返回安全过滤器链
    }

    @Bean // 注册跨域配置Bean
    public CorsConfigurationSource corsConfigurationSource() { // 定义跨域配置方法
        CorsConfiguration configuration = new CorsConfiguration(); // 创建跨域配置对象
        configuration.setAllowedOriginPatterns(List.of("*")); // 允许所有来源访问
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); // 允许的HTTP请求方法
        configuration.setAllowedHeaders(List.of("*")); // 允许所有请求头
        configuration.setAllowCredentials(true); // 允许携带Cookie

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // 创建基于URL的跨域配置源
        source.registerCorsConfiguration("/**", configuration); // 对所有路径应用跨域配置
        return source; // 返回跨域配置源
    }

    @Bean // 注册认证提供者Bean
    public AuthenticationProvider authenticationProvider() { // 定义认证提供者方法
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // 创建基于数据库的认证提供者
        authProvider.setUserDetailsService(userDetailsService); // 设置用户详情服务（用于查询用户）
        authProvider.setPasswordEncoder(passwordEncoder()); // 设置密码加密器
        return authProvider; // 返回认证提供者
    }

    @Bean // 注册AuthenticationManager Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { // 定义认证管理器方法
        return config.getAuthenticationManager(); // 从配置中获取AuthenticationManager
    }

    @Bean // 注册密码加密器Bean
    public PasswordEncoder passwordEncoder() { // 定义密码加密器方法
        return new BCryptPasswordEncoder(); // 使用BCrypt算法对密码进行加密
    }
}
