package com.ch.managementsystem.controller; // 定义当前类所在的包路径，表示该类属于控制层(controller)

import com.alibaba.excel.EasyExcel; // 导入EasyExcel库，用于Excel文件导出
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis Plus的Lambda查询条件构造器
import com.baomidou.mybatisplus.core.metadata.IPage; // 导入分页结果接口IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis Plus分页对象Page
import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result
import com.ch.managementsystem.entity.Performance; // 导入绩效实体类Performance
import com.ch.managementsystem.service.PerformanceService; // 导入绩效业务服务接口PerformanceService
import org.springframework.beans.factory.annotation.Autowired; // 导入自动注入注解，用于从Spring容器中注入Bean
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils; // 导入StringUtils工具类，用于处理字符串操作
import org.springframework.web.bind.annotation.*; // 导入Spring Web相关注解（如RestController、PostMapping等）

import jakarta.servlet.http.HttpServletResponse; // HTTP响应对象，用于返回文件流
import java.io.IOException; // IO异常类
import java.net.URLEncoder; // URL编码工具类
import java.util.List; // List集合接口

@RestController // 标记该类为REST风格控制器，返回数据默认是JSON格式
@RequestMapping("/api/performance") // 定义该控制器的统一请求路径前缀为/api/performance
public class PerformanceController { // 定义绩效控制器类，负责绩效相关操作

    @Autowired // 自动注入PerformanceService对象
    private PerformanceService performanceService; // 绩效业务服务，用于处理数据库相关操作

    @GetMapping // 定义GET请求接口，用于分页查询绩效记录
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<IPage<Performance>> getPage( // 定义分页查询绩效记录的方法，并返回统一Result结果
            @RequestParam(defaultValue = "1") int page, // 获取请求参数page，如果没有传则默认值为1
            @RequestParam(defaultValue = "10") int size, // 获取请求参数size，如果没有传则默认值为10
            @RequestParam(required = false) Long employeeId, // 获取员工ID参数，可选参数
            @RequestParam(required = false) String perfMonth, // 获取绩效月份参数，可选参数
            @RequestParam(required = false) String month) { // 获取月份参数，可选参数
        
        Page<Performance> pageParam = new Page<>(page, size); // 创建分页对象，设置当前页码和每页条数
        LambdaQueryWrapper<Performance> wrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询条件构造器
        
        if (employeeId != null) { // 如果员工ID不为空
            wrapper.eq(Performance::getEmployeeId, employeeId); // 添加员工ID等值查询条件
        }
        
        String queryMonth = StringUtils.hasText(perfMonth) ? perfMonth : month; // 优先使用perfMonth，若为空则使用month
        if (StringUtils.hasText(queryMonth)) { // 如果查询月份不为空
            wrapper.eq(Performance::getPerfMonth, queryMonth); // 添加绩效月份等值查询条件
        }
        
        wrapper.orderByDesc(Performance::getPerfMonth); // 按绩效月份降序排序
        
        return Result.success(performanceService.page(pageParam, wrapper)); // 调用Service层分页查询方法并返回结果
    }

    @PostMapping // 定义POST请求接口，用于新增绩效记录
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> save(@RequestBody Performance performance) { // 定义保存绩效的方法，接收前端传来的绩效对象
        if (performance.getEmployeeId() == null || !StringUtils.hasText(performance.getPerfMonth())) { // 如果员工ID或绩效月份为空
            return Result.error(400, "员工ID和绩效月份不能为空"); // 返回错误提示，要求员工ID和绩效月份不能为空
        }
        return Result.success(performanceService.saveOrUpdatePerformance(performance));
    }

    @PutMapping // 定义PUT请求接口，用于更新绩效记录
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> update(@RequestBody Performance performance) { // 定义更新绩效的方法，接收前端传来的绩效对象
        if (performance.getId() == null) {
            return Result.error(400, "更新时ID不能为空");
        }
        return Result.success(performanceService.saveOrUpdatePerformance(performance));
    }

    @DeleteMapping("/{id}") // 定义DELETE请求接口，用于根据ID删除绩效记录
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> delete(@PathVariable Long id) { // 定义根据ID删除绩效的方法
        return Result.success(performanceService.removeById(id)); // 调用Service层删除绩效记录并返回结果
    }
    
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> deleteBatch(@RequestBody java.util.Map<String, java.util.List<Long>> request) {
        java.util.List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的绩效记录");
        }
        return Result.success(performanceService.removeBatchByIds(ids));
    }

    @PostMapping("/recalculate") // 定义POST请求接口，用于重新计算某个月份的所有绩效
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Integer> recalculateByMonth(@RequestParam String perfMonth) { // 定义重新计算绩效的方法，接收绩效月份作为参数
        if (!StringUtils.hasText(perfMonth)) { // 如果绩效月份为空
            return Result.error(400, "绩效月份不能为空"); // 返回错误提示，要求绩效月份不能为空
        }
        return Result.success(performanceService.recalculateByMonth(perfMonth)); // 调用Service层重新计算绩效并返回结果
    }

    @GetMapping("/export") // 定义绩效数据导出Excel接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public void export(HttpServletResponse response) throws IOException { // 向浏览器返回Excel文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // 设置响应类型为Excel文件
        response.setCharacterEncoding("utf-8"); // 设置字符编码为UTF-8
        String fileName = URLEncoder.encode("Performance_List", "UTF-8").replaceAll("\\+", "%20"); // 对文件名进行URL编码，防止中文乱码
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx"); // 设置浏览器下载文件的响应头
        
        List<Performance> list = performanceService.list(); // 调用service层获取所有绩效数据
        EasyExcel.write(response.getOutputStream(), Performance.class) // 使用EasyExcel写入输出流
                .sheet("Performance") // 创建Excel工作表名称为Performance
                .doWrite(list); // 将数据写入Excel文件
    }
}
