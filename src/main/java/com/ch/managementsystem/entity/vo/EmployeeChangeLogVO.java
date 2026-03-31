package com.ch.managementsystem.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeChangeLogVO {
    private Long id;
    private Long changeId;
    private String actionType;
    private Long operatorUserId;
    private String operatorName;
    private String content;
    private LocalDateTime createTime;
}
