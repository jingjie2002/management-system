package com.ch.managementsystem.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmployeeChangeDetailVO {
    private Long id;
    private Long employeeId;
    private String changeType;
    private String applyMode;
    private String status;
    private String reason;
    private LocalDate effectiveDate;
    private Long beforeDeptId;
    private Long afterDeptId;
    private String beforePosition;
    private String afterPosition;
    private Long beforeJobLevelId;
    private Long afterJobLevelId;
    private BigDecimal beforeSalary;
    private BigDecimal afterSalary;
    private Long applicantUserId;
    private Long approverUserId;
    private String approveRemark;
    private LocalDateTime approveTime;
    private Long executeUserId;
    private LocalDateTime executeTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<EmployeeChangeLogVO> logs;
}
