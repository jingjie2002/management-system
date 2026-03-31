package com.ch.managementsystem.controller; // 定义当前类所在的包路径，表示该类属于控制层(controller)

import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result
import com.ch.managementsystem.entity.SysUser; // 导入系统用户实体类SysUser
import com.ch.managementsystem.entity.dto.LoginDto; // 导入登录数据传输对象LoginDto，用于接收前端登录数据
import com.ch.managementsystem.service.SysUserService; // 导入系统用户业务服务接口SysUserService
import com.ch.managementsystem.utils.CaptchaUtils; // 导入验证码工具类
import org.springframework.beans.factory.annotation.Autowired; // 导入自动注入注解，用于从Spring容器中注入Bean
import org.springframework.security.core.Authentication; // 导入Spring Security认证对象接口
import org.springframework.security.core.context.SecurityContextHolder; // 导入安全上下文对象，用于获取当前登录用户信息
import org.springframework.web.bind.annotation.*; // 导入Spring Web相关注解（如RestController、PostMapping等）

import jakarta.servlet.http.HttpSession; // 导入HttpSession，用于存储验证码
import java.util.HashMap; // 导入HashMap集合类
import java.util.Map; // 导入Map集合接口
import java.util.stream.Collectors; // 导入流处理工具类，用于集合数据转换

/**
 * 认证控制器
 * 负责处理用户登录、注册、验证码等认证相关操作
 */
@RestController // 标记该类为REST风格控制器，返回数据默认是JSON格式
@RequestMapping("/api/auth") // 定义该控制器的统一请求路径前缀
public class AuthController { // 定义认证控制器类，负责登录、注册等认证相关操作

    @Autowired // 自动注入SysUserService对象
    private SysUserService sysUserService; // 用户业务服务，用于处理用户登录、注册等逻辑

    /**
     * 获取验证码
     * @param session HttpSession对象，用于存储验证码
     * @return 包含验证码图片的Result对象
     */
    @GetMapping("/captcha") // 定义GET请求接口，路径为/api/auth/captcha
    public Result<Map<String, String>> getCaptcha(HttpSession session) { // 定义获取验证码接口
        CaptchaUtils.Captcha captcha = CaptchaUtils.generateCaptcha(); // 生成验证码
        session.setAttribute("captcha", captcha.getCode()); // 将验证码存储到session中
        Map<String, String> result = new HashMap<>(); // 创建Map对象用于返回结果
        result.put("image", "data:image/jpeg;base64," + captcha.getBase64Image()); // 将验证码图片转换为Base64格式并放入Map中
        return Result.success(result); // 返回成功结果，并携带验证码图片数据
    }

    /**
     * 用户登录
     * @param loginDto 登录数据传输对象，包含用户名、密码和验证码
     * @param session HttpSession对象，用于获取存储的验证码
     * @return 包含JWT token的Result对象
     */
    @PostMapping("/login") // 定义POST请求接口，路径为/api/auth/login
    public Result<Map<String, String>> login(@RequestBody LoginDto loginDto, HttpSession session) { // 定义登录接口，接收前端传来的登录DTO对象和session
        // 验证验证码
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (!CaptchaUtils.validateCaptcha(loginDto.getCaptcha(), sessionCaptcha)) {
            return Result.error("验证码错误");
        }
        
        String token = sysUserService.login(loginDto.getUsername(), loginDto.getPassword()); // 调用Service层登录方法，并生成JWT Token
        Map<String, String> result = new HashMap<>(); // 创建Map对象用于返回结果
        result.put("token", token); // 将生成的Token放入Map中
        return Result.success(result); // 返回成功结果，并携带Token数据
    }

    /**
     * 用户注册
     * @param user 用户对象，包含注册信息
     * @return 注册结果的Result对象
     */
    @PostMapping("/register") // 定义POST请求接口，路径为/api/auth/register
    public Result<String> register(@RequestBody SysUser user) { // 定义用户注册接口，接收前端传来的用户对象
        sysUserService.register(user); // 调用Service层注册方法，将用户信息保存到数据库
        return Result.success("注册成功"); // 返回注册成功提示信息
    }

    /**
     * 获取当前登录用户信息
     * @return 包含用户信息的Result对象
     */
    @GetMapping("/profile") // 定义GET请求接口，路径为/api/auth/profile
    public Result<Map<String, Object>> profile() { // 定义获取当前登录用户信息接口
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 从Spring Security上下文中获取当前用户认证信息
        Map<String, Object> result = new HashMap<>(); // 创建Map对象用于存储返回数据
        result.put("username", authentication.getName()); // 获取当前登录用户名并放入返回结果
        result.put("roles", authentication.getAuthorities().stream() // 获取当前用户的权限集合并转换为流
                .map(item -> item.getAuthority().replace("ROLE_", "")) // 去掉权限字符串中的ROLE_前缀
                .collect(Collectors.toList())); // 将处理后的权限集合转换为List集合
        return Result.success(result); // 返回成功结果，并返回当前用户信息
    }
}