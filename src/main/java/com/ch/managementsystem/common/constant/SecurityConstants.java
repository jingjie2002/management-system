package com.ch.managementsystem.common.constant; // 定义当前类所在的包路径，用于存放系统中的常量类

public final class SecurityConstants { // 定义一个最终类(final)，表示该类不能被继承，用于存放安全相关常量

    public static final String AUTH_HEADER = "Authorization"; // 定义HTTP请求头名称，用于存储认证Token的Header字段

    public static final String TOKEN_PREFIX = "Bearer "; // 定义Token前缀，一般JWT认证时Authorization格式为 "Bearer token"

    public static final String ROLE_PREFIX = "ROLE_"; // 定义角色前缀，Spring Security中角色默认需要以ROLE_开头

    private SecurityConstants() { // 私有构造方法，防止该常量类被实例化
    }
}