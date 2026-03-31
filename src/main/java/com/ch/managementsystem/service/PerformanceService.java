package com.ch.managementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.managementsystem.entity.Performance;

public interface PerformanceService extends IService<Performance> {
    void calculateAndUpsert(Long employeeId, String perfMonth);

    int recalculateByMonth(String perfMonth);

    boolean saveOrUpdatePerformance(Performance performance);
}
