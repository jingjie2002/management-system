package com.ch.managementsystem.entity.dto;

import lombok.Data;

@Data
public class EmployeeChangeApproveDTO {
    private Boolean approved;
    private String remark;
}
