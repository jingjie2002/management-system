package com.ch.managementsystem.controller; // 定义当前类所在的包路径，表示该类属于控制层(controller)

import org.springframework.web.bind.annotation.GetMapping; // 导入GetMapping注解，用于定义GET请求接口
import org.springframework.web.bind.annotation.RequestMapping; // 导入RequestMapping注解，用于定义请求路径前缀
import org.springframework.web.bind.annotation.RestController; // 导入RestController注解，表示该类是REST风格控制器

import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result

@RestController // 标记该类为REST控制器，返回数据默认是JSON格式
@RequestMapping("/api") // 定义控制器的统一请求路径前缀为/api
public class HealthController { // 定义健康检查控制器类
    @GetMapping("/health") // 定义GET请求接口，完整路径为/api/health
    public Result<String> health() { // 定义健康检查方法，返回Result类型字符串
        return Result.success("ok"); // 返回成功结果，并返回字符串"ok"
    }
}