package com.ch.managementsystem.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeChangeCreateDTO {
    private Long employeeId;
    private String changeType;
    private String reason;
    private LocalDate effectiveDate;
    private Long afterDeptId;
    private String afterPosition;
    private Long afterJobLevelId;
    private BigDecimal afterSalary;
}
