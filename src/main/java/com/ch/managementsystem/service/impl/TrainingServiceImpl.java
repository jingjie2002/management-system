package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.entity.Training;
import com.ch.managementsystem.mapper.TrainingMapper;
import com.ch.managementsystem.service.TrainingService;
import org.springframework.stereotype.Service;

@Service
public class TrainingServiceImpl extends ServiceImpl<TrainingMapper, Training> implements TrainingService {
}
