package com.ch.managementsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_employee_change")
public class EmployeeChange implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
