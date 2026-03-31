package com.ch.managementsystem.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ch.managementsystem.entity.Employee;
import com.ch.managementsystem.entity.Performance;
import com.ch.managementsystem.service.EmployeeService;
import com.ch.managementsystem.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class PerformanceDataGenerator {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PerformanceService performanceService;

    public void generateRecentPerformanceData() {
        // 延迟执行，确保数据库初始化完成
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            try {
                // 获取所有在职员工
                List<Employee> employees = employeeService.list();
                System.out.println("获取到 " + employees.size() + " 个员工");
                
                if (employees.isEmpty()) {
                    System.out.println("没有员工数据，跳过绩效数据生成");
                    return;
                }
                
                // 生成最近一周的绩效数据
                LocalDate today = LocalDate.now();
                String currentMonth = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                
                for (Employee employee : employees) {
                    try {
                        // 检查是否已有当月绩效数据
                        Performance existing = performanceService.getOne(
                            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Performance>()
                                .eq(Performance::getEmployeeId, employee.getId())
                                .eq(Performance::getPerfMonth, currentMonth)
                                .last("limit 1")
                        );
                        
                        if (existing == null) {
                            // 生成新的绩效数据
                            Performance performance = new Performance();
                            performance.setEmployeeId(employee.getId());
                            performance.setPerfMonth(currentMonth);
                            performance.setBaseSalary(employee.getBaseSalary());
                            performance.setAttendanceDays(22); // 假设全勤
                            performance.setComments("月度绩效");
                            performanceService.save(performance);
                            System.out.println("为员工 " + employee.getName() + " 生成了 " + currentMonth + " 的绩效数据");
                        }
                    } catch (Exception e) {
                        System.err.println("为员工 " + employee.getName() + " 生成绩效数据失败: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("生成绩效数据失败: " + e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
