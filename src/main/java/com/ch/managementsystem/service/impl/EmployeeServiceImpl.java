package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.entity.Attendance;
import com.ch.managementsystem.entity.Department;
import com.ch.managementsystem.entity.Employee;
import com.ch.managementsystem.entity.JobLevel;
import com.ch.managementsystem.entity.Leave;
import com.ch.managementsystem.entity.Performance;
import com.ch.managementsystem.entity.Project;
import com.ch.managementsystem.entity.ProjectParticipation;
import com.ch.managementsystem.entity.SysUser;
import com.ch.managementsystem.entity.dto.EmployeeExportDTO;
import com.ch.managementsystem.entity.vo.ProfileVO;
import com.ch.managementsystem.entity.vo.ProfileProjectVO;
import com.ch.managementsystem.mapper.EmployeeMapper;
import com.ch.managementsystem.mapper.ProjectParticipationMapper;
import com.ch.managementsystem.service.AttendanceService;
import com.ch.managementsystem.service.DepartmentService;
import com.ch.managementsystem.service.EmployeeChangeService;
import com.ch.managementsystem.service.EmployeeService;
import com.ch.managementsystem.service.JobLevelService;
import com.ch.managementsystem.service.LeaveService;
import com.ch.managementsystem.service.PerformanceService;
import com.ch.managementsystem.service.ProjectService;
import com.ch.managementsystem.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private PerformanceService performanceService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ProjectParticipationMapper projectParticipationMapper;
    @Autowired
    private JobLevelService jobLevelService;
    @Autowired
    @Lazy
    private EmployeeChangeService employeeChangeService;

    @Override
    public ProfileVO getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        SysUser sysUser = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .last("limit 1")
        );
        if (sysUser == null) {
            return null;
        }

        Employee employee = this.getOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUserId, sysUser.getId())
                        .eq(Employee::getStatus, 1)
                        .isNull(Employee::getLeaveDate)
                        .last("limit 1")
        );
        if (employee == null) {
            employee = this.getOne(
                    new LambdaQueryWrapper<Employee>()
                            .eq(Employee::getUserId, sysUser.getId())
                            .last("limit 1")
            );
        }
        if (employee == null && StringUtils.hasText(sysUser.getRealName())) {
            employee = this.getOne(
                    new LambdaQueryWrapper<Employee>()
                            .eq(Employee::getName, sysUser.getRealName())
                            .eq(Employee::getStatus, 1)
                            .isNull(Employee::getLeaveDate)
                            .last("limit 1")
            );
            if (employee == null) {
                employee = this.getOne(
                        new LambdaQueryWrapper<Employee>()
                                .eq(Employee::getName, sysUser.getRealName())
                                .last("limit 1")
                );
            }
            if (employee != null && employee.getUserId() == null) {
                Employee updateRef = new Employee();
                updateRef.setId(employee.getId());
                updateRef.setUserId(sysUser.getId());
                this.updateById(updateRef);
                employee.setUserId(sysUser.getId());
            }
        }

        ProfileVO profile = buildBaseProfile(employee, sysUser);

        if (employee == null || employee.getId() == null) {
            profile.setAttendanceSummary(defaultAttendanceSummary());
            profile.setPerformanceHistory(List.of());
            profile.setLeaveRecords(List.of());
            profile.setProjectContributions(List.of());
            profile.setRecentChanges(List.of());
            profile.setProjectContributionScore(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            return profile;
        }

        Map<String, Long> attendanceSummary = defaultAttendanceSummary();
        List<Attendance> attendanceList = attendanceService.list(
                new LambdaQueryWrapper<Attendance>().eq(Attendance::getEmployeeId, employee.getId())
        );
        attendanceList.forEach(item -> {
            String label = attendanceLabel(item.getStatus());
            attendanceSummary.put(label, attendanceSummary.getOrDefault(label, 0L) + 1L);
        });
        profile.setAttendanceSummary(attendanceSummary);

        List<Performance> perfHistory = performanceService.list(
                new LambdaQueryWrapper<Performance>()
                        .eq(Performance::getEmployeeId, employee.getId())
                        .orderByDesc(Performance::getPerfMonth)
                        .last("limit 6")
        );
        profile.setPerformanceHistory(perfHistory);

        List<Leave> leaveList = leaveService.list(
                new LambdaQueryWrapper<Leave>()
                        .eq(Leave::getEmployeeId, employee.getId())
                        .orderByDesc(Leave::getCreateTime)
                        .last("limit 10")
        );
        profile.setLeaveRecords(leaveList);
        profile.setRecentChanges(employeeChangeService.latestForEmployee(employee.getId(), 5));

        List<ProjectParticipation> participations = projectParticipationMapper.selectList(
                new LambdaQueryWrapper<ProjectParticipation>()
                        .eq(ProjectParticipation::getEmployeeId, employee.getId())
                        .orderByDesc(ProjectParticipation::getUpdateTime)
                        .orderByDesc(ProjectParticipation::getCreateTime)
        );
        if (participations.isEmpty()) {
            profile.setProjectContributions(List.of());
            profile.setProjectContributionScore(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            return profile;
        }

        Map<Long, Project> projectMap = projectService.listByIds(
                        participations.stream()
                                .map(ProjectParticipation::getProjectId)
                                .filter(Objects::nonNull)
                                .distinct()
                                .toList()
                )
                .stream()
                .collect(Collectors.toMap(Project::getId, item -> item, (left, right) -> left));

        List<ProfileProjectVO> projectVOList = participations.stream()
                .map(participation -> {
                    Project project = projectMap.get(participation.getProjectId());
                    BigDecimal score = participation.getContributionScore();
                    if (score == null) {
                        score = estimateContributionScore(project, participation.getRole());
                    }
                    ProfileProjectVO item = new ProfileProjectVO();
                    item.setParticipationId(participation.getId());
                    item.setProjectId(participation.getProjectId());
                    item.setProjectName(project == null ? "未知项目" : project.getName());
                    item.setRole(participation.getRole());
                    item.setProjectStatus(project == null ? null : project.getStatus());
                    item.setStartDate(project == null ? null : project.getStartDate());
                    item.setEndDate(project == null ? null : project.getEndDate());
                    item.setContributionScore(score);
                    return item;
                })
                .toList();

        BigDecimal totalContribution = projectVOList.stream()
                .map(ProfileProjectVO::getContributionScore)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        profile.setProjectContributions(projectVOList);
        profile.setProjectContributionScore(totalContribution);
        return profile;
    }

    private ProfileVO buildBaseProfile(Employee employee, SysUser sysUser) {
        ProfileVO profile = new ProfileVO();
        profile.setPhone(sysUser.getPhone());
        if (employee == null) {
            profile.setName(StringUtils.hasText(sysUser.getRealName()) ? sysUser.getRealName() : sysUser.getUsername());
            return profile;
        }
        profile.setName(StringUtils.hasText(employee.getName()) ? employee.getName()
                : (StringUtils.hasText(sysUser.getRealName()) ? sysUser.getRealName() : sysUser.getUsername()));
        profile.setEmployeeId(employee.getId());
        profile.setJobNumber(employee.getJobNumber());
        profile.setPosition(employee.getPosition());
        profile.setJobLevelName(resolveJobLevelName(employee));
        profile.setHireDate(employee.getHireDate());
        profile.setLeaveDate(employee.getLeaveDate());
        profile.setStatus(employee.getStatus());
        if (employee.getDeptId() != null) {
            Department department = departmentService.getById(employee.getDeptId());
            if (department != null) {
                profile.setDeptName(department.getDeptName());
            }
        }
        return profile;
    }

    private String resolveJobLevelName(Employee employee) {
        if (employee == null) {
            return null;
        }
        if (StringUtils.hasText(employee.getJobLevelName())) {
            return employee.getJobLevelName();
        }
        if (employee.getJobLevelId() == null) {
            return null;
        }
        JobLevel jobLevel = jobLevelService.getById(employee.getJobLevelId());
        return jobLevel == null ? null : jobLevel.getLevelName();
    }

    private Map<String, Long> defaultAttendanceSummary() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("正常", 0L);
        result.put("迟到", 0L);
        result.put("早退", 0L);
        result.put("缺勤", 0L);
        return result;
    }

    private String attendanceLabel(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 1 -> "正常";
            case 2 -> "迟到";
            case 3 -> "早退";
            case 4 -> "缺勤";
            default -> "未知";
        };
    }

    private BigDecimal estimateContributionScore(Project project, String role) {
        double baseScore = switch (project == null || project.getStatus() == null ? -1 : project.getStatus()) {
            case 2 -> 100D;
            case 1 -> 80D;
            case 0 -> 60D;
            case 3 -> 40D;
            default -> 50D;
        };
        double roleFactor = 1D;
        if (StringUtils.hasText(role)) {
            String roleText = role.toLowerCase();
            if (roleText.contains("manager") || role.contains("经理") || role.contains("负责人")) {
                roleFactor = 1.2D;
            } else if (roleText.contains("leader") || role.contains("主程") || role.contains("架构")) {
                roleFactor = 1.1D;
            } else if (roleText.contains("test") || roleText.contains("qa") || role.contains("测试")) {
                roleFactor = 0.9D;
            }
        }
        return BigDecimal.valueOf(Math.min(baseScore * roleFactor, 120D)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public IPage<Employee> getPageWithDept(int page, int size, String name, Long deptId) {
        Page<Employee> pageParam = new Page<>(page, size);
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like("e.name", name);
        }
        if (deptId != null) {
            wrapper.eq("e.dept_id", deptId);
        }
        wrapper.orderByDesc("e.create_time");
        
        return baseMapper.selectPageWithDept(pageParam, wrapper);
    }

    @Override
    public List<EmployeeExportDTO> getExportList() {
        // Fetch all employees with department name
        Page<Employee> pageParam = new Page<>(1, -1); // No limit
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("e.create_time");
        IPage<Employee> page = baseMapper.selectPageWithDept(pageParam, wrapper);
        
        List<Employee> records = page.getRecords();
        List<EmployeeExportDTO> dtos = new ArrayList<>();
        
        for (Employee e : records) {
            EmployeeExportDTO dto = new EmployeeExportDTO();
            dto.setId(e.getId());
            dto.setName(e.getName());
            dto.setGenderStr(e.getGender() != null && e.getGender() == 1 ? "Male" : "Female");
            dto.setJobNumber(e.getJobNumber());
            dto.setPosition(e.getPosition());
            dto.setBaseSalary(e.getBaseSalary());
            dto.setHireDate(e.getHireDate());
            
            String status = "Unknown";
            if (e.getStatus() != null) {
                if (e.getStatus() == 1) status = "Active";
                else if (e.getStatus() == 2) status = "Resigned";
                else if (e.getStatus() == 3) status = "Leave";
            }
            dto.setStatusStr(status);
            
            dto.setIdCard(e.getIdCard());
            dto.setAddress(e.getAddress());
            dto.setDeptName(e.getDeptName());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public boolean importExcel(List<Employee> employees) {
        try {
            for (Employee employee : employees) {
                // 检查员工是否已存在
                Employee existing = this.getOne(new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getJobNumber, employee.getJobNumber())
                        .last("limit 1"));
                if (existing != null) {
                    // 更新现有员工
                    employee.setId(existing.getId());
                    this.updateById(employee);
                } else {
                    // 新增员工
                    this.save(employee);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Employee> getImportTemplate() {
        List<Employee> templateList = new ArrayList<>();
        Employee template = new Employee();
        template.setName("张三");
        template.setGender(1);
        template.setJobNumber("EMP001");
        template.setPosition("工程师");
        template.setBaseSalary(new BigDecimal(5000));
        template.setStatus(1);
        template.setDeptId(1L);
        templateList.add(template);
        return templateList;
    }

    @Override
    public boolean importExcel(MultipartFile file) throws IOException {
        try {
            List<Employee> employees = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), Employee.class, new AnalysisEventListener<Employee>() {
                @Override
                public void invoke(Employee employee, AnalysisContext context) {
                    employees.add(employee);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 所有数据解析完成后的操作
                }
            }).sheet().doRead();
            
            return importExcel(employees);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
