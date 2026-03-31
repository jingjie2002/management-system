package com.ch.managementsystem.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ch.managementsystem.entity.Attendance;
import com.ch.managementsystem.entity.Department;
import com.ch.managementsystem.entity.Employee;
import com.ch.managementsystem.entity.Leave;
import com.ch.managementsystem.entity.Performance;
import com.ch.managementsystem.entity.Project;
import com.ch.managementsystem.entity.vo.DashboardVO;
import com.ch.managementsystem.service.AttendanceService;
import com.ch.managementsystem.service.DepartmentService;
import com.ch.managementsystem.service.EmployeeService;
import com.ch.managementsystem.service.LeaveService;
import com.ch.managementsystem.service.PerformanceService;
import com.ch.managementsystem.service.ProjectService;
import com.ch.managementsystem.service.StatisticsService;
import com.ch.managementsystem.utils.AlgorithmUtils;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private LeaveService leaveService;

    @Override
    public DashboardVO getDashboardData(LocalDate fromDate, LocalDate toDate) {
        DashboardVO vo = new DashboardVO();

        List<Employee> allEmployees = employeeService.list();
        vo.setTotalEmployees((long) allEmployees.size());
        vo.setActiveEmployees(allEmployees.stream().filter(e -> e.getStatus() == 1).count());
        vo.setResignedEmployees(allEmployees.stream().filter(e -> e.getStatus() == 2).count());

        List<Department> allDepts = departmentService.list();
        vo.setTotalDepartments((long) allDepts.size());

        List<Project> allProjects = projectService.list();
        vo.setTotalProjects((long) allProjects.size());
        vo.setOngoingProjects(allProjects.stream().filter(p -> p.getStatus() == 1).count());

        Map<Long, Long> empCountByDept = allEmployees.stream()
                .filter(e -> e.getDeptId() != null)
                .collect(Collectors.groupingBy(Employee::getDeptId, Collectors.counting()));

        List<Map<String, Object>> deptStats = new ArrayList<>();
        for (Department dept : allDepts) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", dept.getDeptName());
            map.put("value", empCountByDept.getOrDefault(dept.getId(), 0L));
            deptStats.add(map);
        }
        vo.setDeptEmployeeStats(deptStats);

        LocalDate end = toDate == null ? LocalDate.now() : toDate;
        LocalDate start = fromDate == null ? end.minusDays(29) : fromDate;
        List<Attendance> allAttendance = attendanceService.list().stream()
                .filter(item -> item.getDate() != null)
                .filter(item -> !item.getDate().isBefore(start) && !item.getDate().isAfter(end))
                .toList();
        Map<Integer, Long> attendanceCount = allAttendance.stream()
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));

        List<Map<String, Object>> attStats = new ArrayList<>();
        attStats.add(createStatMap("正常", attendanceCount.getOrDefault(1, 0L)));
        attStats.add(createStatMap("迟到", attendanceCount.getOrDefault(2, 0L)));
        attStats.add(createStatMap("早退", attendanceCount.getOrDefault(3, 0L)));
        attStats.add(createStatMap("缺勤", attendanceCount.getOrDefault(4, 0L)));
        vo.setAttendanceStats(attStats);

        Map<Integer, Long> projCount = allProjects.stream()
                .collect(Collectors.groupingBy(Project::getStatus, Collectors.counting()));
        List<Map<String, Object>> projStats = new ArrayList<>();
        projStats.add(createStatMap("待处理", projCount.getOrDefault(0, 0L)));
        projStats.add(createStatMap("进行中", projCount.getOrDefault(1, 0L)));
        projStats.add(createStatMap("已完成", projCount.getOrDefault(2, 0L)));
        projStats.add(createStatMap("已暂停", projCount.getOrDefault(3, 0L)));
        vo.setProjectStatusStats(projStats);

        Map<Integer, Long> leaveStatusCount = leaveService.list().stream()
                .collect(Collectors.groupingBy(Leave::getStatus, Collectors.counting()));
        List<Map<String, Object>> leaveStats = new ArrayList<>();
        leaveStats.add(createStatMap("待审批", leaveStatusCount.getOrDefault(0, 0L)));
        leaveStats.add(createStatMap("已通过", leaveStatusCount.getOrDefault(1, 0L)));
        leaveStats.add(createStatMap("已驳回", leaveStatusCount.getOrDefault(2, 0L)));
        vo.setLeaveStatusStats(leaveStats);

        List<Map<String, Object>> perfTrend = performanceService.list().stream()
                .filter(performance -> performance.getPerfMonth() != null)
                .map(performance -> Map.entry(performance.getPerfMonth(), calculatePerformanceScore(performance)))
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.averagingDouble(Map.Entry::getValue)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .limit(6)
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", item.getKey());
                    map.put("value", BigDecimal.valueOf(item.getValue())
                            .setScale(2, RoundingMode.HALF_UP));
                    return map;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        vo.setMonthlyPerformanceTrend(perfTrend);

        // 算法1应用：TOP-K 最小堆（部门出勤率排行）
        Map<Long, Long> attendanceByEmp = allAttendance.stream()
                .collect(Collectors.groupingBy(Attendance::getEmployeeId, Collectors.counting()));
        Map<String, Double> deptAttendanceRate = new HashMap<>();
        for (Department dept : allDepts) {
            List<Employee> deptEmployees = allEmployees.stream()
                    .filter(e -> dept.getId().equals(e.getDeptId()))
                    .toList();
            if (deptEmployees.isEmpty()) {
                deptAttendanceRate.put(dept.getDeptName(), 0.0);
                continue;
            }
            long hasRecord = deptEmployees.stream()
                    .filter(e -> attendanceByEmp.getOrDefault(e.getId(), 0L) > 0)
                    .count();
            double rate = (double) hasRecord / deptEmployees.size() * 100;
            deptAttendanceRate.put(dept.getDeptName(), rate);
        }
        List<Map<String, Object>> topAttendance = AlgorithmUtils.topKByMinHeap(deptAttendanceRate, 5)
                .stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", e.getKey());
                    m.put("value", BigDecimal.valueOf(e.getValue()).setScale(2, RoundingMode.HALF_UP));
                    return m;
                }).toList();
        vo.setTopAttendanceDepartments(topAttendance);

        // 算法2应用：加权评分（员工综合贡献排行）
        Map<Long, Double> performanceByEmp = performanceService.list().stream()
                .collect(Collectors.groupingBy(
                        Performance::getEmployeeId,
                        Collectors.averagingDouble(this::calculatePerformanceScore)
                ));
        Map<Long, Long> projectByManager = allProjects.stream()
                .filter(p -> p.getManagerId() != null)
                .collect(Collectors.groupingBy(Project::getManagerId, Collectors.counting()));
        List<Map<String, Object>> employeeTop = allEmployees.stream()
                .map(e -> {
                    double attendanceRate = attendanceByEmp.getOrDefault(e.getId(), 0L) > 0 ? 100.0 : 0.0;
                    double perfScore = performanceByEmp.getOrDefault(e.getId(), 0.0);
                    double projectProgress = Math.min(projectByManager.getOrDefault(e.getId(), 0L) * 20.0, 100.0);
                    double score = AlgorithmUtils.weightedScore(attendanceRate, perfScore, projectProgress);
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", e.getName());
                    m.put("value", BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP));
                    return m;
                })
                .sorted(Comparator.comparing((Map<String, Object> m) -> ((BigDecimal) m.get("value")).doubleValue()).reversed())
                .limit(5)
                .toList();
        vo.setEmployeeContributionTop(employeeTop);

        return vo;
    }

    private double calculatePerformanceScore(Performance performance) {
        if (performance == null) {
            return 0;
        }
        boolean hasMetric = performance.getAttendanceDays() != null || performance.getProjectBonus() != null;
        if (!hasMetric) {
            return 0;
        }
        double attendanceScore = performance.getAttendanceDays() == null ? 0 : performance.getAttendanceDays() * 3.0;
        double projectScore = performance.getProjectBonus() == null ? 0 : performance.getProjectBonus().doubleValue() / 10.0;
        return Math.min(attendanceScore + projectScore, 100.0);
    }
    private Map<String, Object> createStatMap(String name, Long value) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("value", value);
        return map;
    }

    private Map<String, Object> createStatMap(String name, BigDecimal value) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("value", value);
        return map;
    }
}
