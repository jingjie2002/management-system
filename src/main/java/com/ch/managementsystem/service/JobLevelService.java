package com.ch.managementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.managementsystem.entity.JobLevel;

import java.util.List;

public interface JobLevelService extends IService<JobLevel> {
    List<JobLevel> listOrderedByRank();
}
