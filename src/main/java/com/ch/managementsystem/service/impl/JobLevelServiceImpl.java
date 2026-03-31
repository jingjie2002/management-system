package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.entity.JobLevel;
import com.ch.managementsystem.mapper.JobLevelMapper;
import com.ch.managementsystem.service.JobLevelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobLevelServiceImpl extends ServiceImpl<JobLevelMapper, JobLevel> implements JobLevelService {

    @Override
    public List<JobLevel> listOrderedByRank() {
        return list(new LambdaQueryWrapper<JobLevel>().orderByAsc(JobLevel::getLevelRank));
    }
}
