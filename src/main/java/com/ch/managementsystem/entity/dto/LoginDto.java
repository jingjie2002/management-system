package com.ch.managementsystem.entity.dto; // 声明当前类所在的包路径，表示该类属于数据传输对象（DTO）包

import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter、toString等方法

@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
public class LoginDto { // 定义一个登录数据传输对象类，用于接收前端传来的登录信息

    private String username; // 定义用户名属性，数据类型为String，用于存储用户登录时输入的用户名

    private String password; // 定义密码属性，数据类型为String，用于存储用户登录时输入的密码

    private String captcha; // 验证码字段，用于存储用户输入的验证码
}