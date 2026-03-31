package com.ch.managementsystem.entity.vo;

import com.ch.managementsystem.entity.Leave;
import com.ch.managementsystem.entity.Performance;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class ProfileVO {
    private Long employeeId;

    private String name;

    private String phone;

    private String jobNumber;

    private String deptName;

    private String position;

    private String jobLevelName;

    private LocalDate hireDate;

    private LocalDate leaveDate;

    private Integer status;

    private BigDecimal projectContributionScore;

    private Map<String, Long> attendanceSummary;

    private List<Performance> performanceHistory;

    private List<Leave> leaveRecords;

    private List<ProfileProjectVO> projectContributions;

    private List<EmployeeChangeDetailVO> recentChanges;
}
