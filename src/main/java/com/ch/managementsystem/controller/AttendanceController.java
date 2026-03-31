package com.ch.managementsystem.controller; // 定义当前类所在的包路径，表示该类属于控制层(controller)

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis Plus的Lambda查询条件构造器
import com.baomidou.mybatisplus.core.metadata.IPage; // 导入分页结果接口IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis Plus分页对象Page
import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result
import com.ch.managementsystem.entity.Attendance; // 导入考勤实体类Attendance
import com.ch.managementsystem.service.AttendanceService; // 导入考勤业务层接口AttendanceService
import org.springframework.beans.factory.annotation.Autowired; // 导入自动注入注解，用于从Spring容器中注入Bean
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*; // 导入Spring Web注解（如RestController、GetMapping等）

import java.time.LocalDate; // 导入LocalDate类，用于表示日期

@RestController // 标记该类为REST风格控制器，返回的数据默认是JSON格式
@RequestMapping("/api/attendance") // 定义该控制器的统一请求路径前缀
public class AttendanceController { // 定义考勤控制器类

    @Autowired // 自动注入AttendanceService对象
    private AttendanceService attendanceService; // 考勤业务服务，用于处理数据库相关操作

    @GetMapping // 定义GET请求接口
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<IPage<Attendance>> getPage( // 定义分页查询考勤记录的方法，并返回统一Result结果
            @RequestParam(defaultValue = "1") int page, // 获取请求参数page，如果没有传则默认值为1
            @RequestParam(defaultValue = "10") int size, // 获取请求参数size，如果没有传则默认值为10
            @RequestParam(required = false) Long employeeId, // 获取员工ID参数，可选参数
            @RequestParam(required = false) LocalDate date) { // 获取日期参数，可选参数
        
        Page<Attendance> pageParam = new Page<>(page, size); // 创建分页对象，设置当前页码和每页条数
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询条件构造器
        
        if (employeeId != null) { // 如果员工ID不为空
            wrapper.eq(Attendance::getEmployeeId, employeeId); // 添加员工ID等值查询条件
        }
        
        if (date != null) { // 如果日期不为空
            wrapper.eq(Attendance::getDate, date); // 添加日期等值查询条件
        }
        
        wrapper.orderByDesc(Attendance::getCheckInTime); // 按签到时间字段进行降序排序
        
        return Result.success(attendanceService.page(pageParam, wrapper)); // 调用Service层分页查询方法并返回成功结果
    }

    @PostMapping("/checkin") // 定义POST请求接口，路径为/api/attendance/checkin
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<Boolean> checkIn(@RequestBody Attendance attendance) { // 定义员工签到接口，并接收前端传来的考勤对象
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        attendance.setCheckInTime(now); // 设置签到时间为当前时间
        attendance.setDate(LocalDate.now()); // 设置签到日期为当前日期
        
        // 检查上班打卡时间是否在正常范围内（早上8点到晚上6点）
        int hour = now.getHour();
        if (hour < 8 || hour >= 18) {
            attendance.setStatus(2); // 迟到
        } else {
            attendance.setStatus(1); // 正常
        }
        
        return Result.success(attendanceService.save(attendance)); // 调用Service保存考勤记录并返回结果
    }
    
    @PutMapping("/checkout") // 定义PUT请求接口，路径为/api/attendance/checkout
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<Boolean> checkOut(@RequestBody Attendance attendance) { // 定义员工签退接口，并接收前端传来的考勤对象
        Attendance existingAttendance = attendanceService.getById(attendance.getId());
        if (existingAttendance == null) {
            return Result.error("考勤记录不存在");
        }
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        attendance.setCheckOutTime(now); // 设置签退时间为当前时间
        
        // 检查下班打卡时间是否在正常范围内（早上8点到晚上6点）
        int hour = now.getHour();
        if (hour < 8 || hour >= 18) {
            attendance.setStatus(3); // 早退
        } else {
            // 检查上班打卡和下班打卡的时间间隔是否至少60秒
            if (existingAttendance.getCheckInTime() != null) {
                java.time.Duration duration = java.time.Duration.between(existingAttendance.getCheckInTime(), now);
                if (duration.getSeconds() < 60) {
                    attendance.setStatus(3); // 早退
                } else {
                    attendance.setStatus(1); // 正常
                }
            } else {
                attendance.setStatus(1); // 正常
            }
        }
        
        return Result.success(attendanceService.updateById(attendance)); // 调用Service根据ID更新考勤记录并返回结果
    }
    
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> deleteBatch(@RequestBody java.util.Map<String, java.util.List<Long>> request) {
        java.util.List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        return Result.success(attendanceService.removeBatchByIds(ids));
    }
}
