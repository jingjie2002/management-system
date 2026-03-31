package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.entity.Attendance;
import com.ch.managementsystem.entity.Employee;
import com.ch.managementsystem.entity.Leave;
import com.ch.managementsystem.entity.Performance;
import com.ch.managementsystem.mapper.EmployeeMapper;
import com.ch.managementsystem.mapper.PerformanceMapper;
import com.ch.managementsystem.service.AttendanceService;
import com.ch.managementsystem.service.LeaveService;
import com.ch.managementsystem.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PerformanceServiceImpl extends ServiceImpl<PerformanceMapper, Performance> implements PerformanceService {

    private static final BigDecimal LATE_DEDUCTION_PER_TIME = BigDecimal.valueOf(50);
    private static final BigDecimal LEAVE_DEDUCTION_PER_DAY = BigDecimal.valueOf(100);

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LeaveService leaveService;

    @Override
    public void calculateAndUpsert(Long employeeId, String perfMonth) {
        YearMonth month = YearMonth.parse(perfMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);

        Employee employee = employeeMapper.selectById(employeeId);
        BigDecimal baseSalary = employee != null && employee.getBaseSalary() != null
                ? employee.getBaseSalary()
                : BigDecimal.valueOf(5000);

        List<Attendance> monthAttendance = attendanceService.list(new LambdaQueryWrapper<Attendance>()
                .eq(Attendance::getEmployeeId, employeeId)
                .between(Attendance::getDate, startDate, endDate));
        long attendanceDays = monthAttendance.stream().filter(item -> item.getStatus() != null && item.getStatus() != 4).count();
        long lateTimes = monthAttendance.stream().filter(item -> item.getStatus() != null && item.getStatus() == 2).count();
        long normalDays = monthAttendance.stream().filter(item -> item.getStatus() != null && item.getStatus() == 1).count();

        List<Leave> approvedLeaves = leaveService.list(new LambdaQueryWrapper<Leave>()
                .eq(Leave::getEmployeeId, employeeId)
                .eq(Leave::getStatus, 1)
                .ge(Leave::getEndTime, startTime)
                .le(Leave::getStartTime, endTime));
        long leaveDays = approvedLeaves.stream().mapToLong(item -> {
            if (item.getStartTime() == null || item.getEndTime() == null) {
                return 0L;
            }
            long hours = Math.max(1, Duration.between(item.getStartTime(), item.getEndTime()).toHours());
            return (hours + 23) / 24;
        }).sum();

        BigDecimal attendanceBonus = BigDecimal.valueOf(attendanceDays);
        BigDecimal lateDeduction = LATE_DEDUCTION_PER_TIME.multiply(BigDecimal.valueOf(lateTimes));
        BigDecimal leaveDeduction = LEAVE_DEDUCTION_PER_DAY.multiply(BigDecimal.valueOf(leaveDays));

        double attendanceRate = monthAttendance.isEmpty() ? 0.0 : (double) normalDays / monthAttendance.size();

        String comments = "基本工资+" + attendanceDays + "-迟到扣款" + lateDeduction + "-请假扣款" + leaveDeduction + "，全勤率"
                + BigDecimal.valueOf(attendanceRate * 100).setScale(2, RoundingMode.HALF_UP) + "%";

        Performance performance = this.getOne(new LambdaQueryWrapper<Performance>()
                .eq(Performance::getEmployeeId, employeeId)
                .eq(Performance::getPerfMonth, perfMonth)
                .last("limit 1"));
        if (performance == null) {
            performance = new Performance();
            performance.setEmployeeId(employeeId);
            performance.setPerfMonth(perfMonth);
            performance.setBaseSalary(baseSalary);
            performance.setAttendanceDays((int) attendanceDays);
            performance.setComments(comments);
            this.save(performance);
            return;
        }
        performance.setBaseSalary(baseSalary);
        performance.setAttendanceDays((int) attendanceDays);
        performance.setComments(comments);
        this.updateById(performance);
    }

    @Override
    public int recalculateByMonth(String perfMonth) {
        List<Employee> employees = employeeMapper.selectList(new LambdaQueryWrapper<Employee>().eq(Employee::getStatus, 1));
        int count = 0;
        for (Employee employee : employees) {
            calculateAndUpsert(employee.getId(), perfMonth);
            count++;
        }
        return count;
    }

    @Override
    public boolean saveOrUpdatePerformance(Performance performance) {
        if (performance.getId() != null) {
            return this.updateById(performance);
        }
        return this.save(performance);
    }
}
