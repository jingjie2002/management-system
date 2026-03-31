package com.ch.managementsystem.config; // 定义当前类所在的包路径，属于系统的配置层(config)

import org.springframework.context.annotation.Configuration; // 导入Spring的配置注解，用于标记该类为配置类
import org.springframework.web.servlet.config.annotation.CorsRegistry; // 导入跨域配置注册类，用于定义跨域规则
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 导入SpringMVC配置接口，可以自定义MVC相关配置

@Configuration // 标记该类为Spring配置类，SpringBoot启动时会自动加载该配置
public class CorsConfig implements WebMvcConfigurer { // 定义跨域配置类，并实现WebMvcConfigurer接口以自定义MVC配置

    @Override // 重写父接口中的方法
    public void addCorsMappings(CorsRegistry registry) { // 定义跨域规则的方法，registry用于注册跨域配置

        registry.addMapping("/**") // 允许所有路径的接口都可以进行跨域访问
                .allowedOriginPatterns("*") // 允许所有来源的域名访问（例如localhost、127.0.0.1等）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP请求方法类型
                .allowedHeaders("*") // 允许所有请求头（如Authorization、Content-Type等）
                .allowCredentials(true) // 允许跨域请求携带Cookie或认证信息
                .maxAge(3600); // 浏览器缓存预检请求的时间，单位为秒，这里表示1小时
    }
}