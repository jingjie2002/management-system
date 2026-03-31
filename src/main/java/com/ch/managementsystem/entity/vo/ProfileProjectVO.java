package com.ch.managementsystem.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProfileProjectVO {

    private Long participationId;

    private Long projectId;

    private String projectName;

    private String role;

    private Integer projectStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal contributionScore;
}
