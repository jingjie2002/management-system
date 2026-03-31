package com.ch.managementsystem.controller; // 声明当前类所在的包路径，表示该类属于控制层(controller)

import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result，用于标准化接口返回数据
import com.ch.managementsystem.entity.vo.DashboardVO; // 导入DashboardVO视图对象，用于封装仪表盘展示的数据
import com.ch.managementsystem.service.StatisticsService; // 导入统计业务服务接口StatisticsService
import org.springframework.beans.factory.annotation.Autowired; // 导入Spring自动注入注解，用于自动注入Bean对象
import org.springframework.format.annotation.DateTimeFormat; // 导入日期格式化注解，用于将请求参数字符串转换为日期对象
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping; // 导入GetMapping注解，用于映射HTTP GET请求
import org.springframework.web.bind.annotation.RequestParam; // 导入RequestParam注解，用于获取请求参数
import org.springframework.web.bind.annotation.RequestMapping; // 导入RequestMapping注解，用于定义统一请求路径
import org.springframework.web.bind.annotation.RestController; // 导入RestController注解，表示该类是REST风格控制器

import java.time.LocalDate; // 导入LocalDate类，用于表示日期（不包含时间）

@RestController // 标记该类为REST控制器，返回数据默认以JSON格式输出
@RequestMapping("/api/statistics") // 定义当前控制器所有接口的统一访问路径前缀 /api/statistics
public class StatisticsController { // 定义统计控制器类，负责系统统计相关接口

    @Autowired // 自动注入StatisticsService对象
    private StatisticsService statisticsService; // 声明统计服务对象，用于处理统计业务逻辑

    @GetMapping("/dashboard") // 定义GET请求接口，访问路径为 /api/statistics/dashboard
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<DashboardVO> getDashboardData( // 定义获取仪表盘统计数据的方法，返回DashboardVO类型数据
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate, // 接收请求参数fromDate，格式为yyyy-MM-dd，可为空，用于统计开始日期
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) { // 接收请求参数toDate，格式为yyyy-MM-dd，可为空，用于统计结束日期
        return Result.success(statisticsService.getDashboardData(fromDate, toDate)); // 调用service层获取仪表盘统计数据，并封装为成功结果返回
    }
}
