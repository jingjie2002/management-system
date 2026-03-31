package com.ch.managementsystem.service;

import com.ch.managementsystem.entity.vo.DashboardVO;

import java.time.LocalDate;

public interface StatisticsService {
    DashboardVO getDashboardData(LocalDate fromDate, LocalDate toDate);
}
