package com.ch.managementsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_employee_change_log")
public class EmployeeChangeLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long changeId;
    private String actionType;
    private Long operatorUserId;
    private String operatorName;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
