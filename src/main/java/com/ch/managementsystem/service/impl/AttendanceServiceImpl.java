package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.entity.Attendance;
import com.ch.managementsystem.mapper.AttendanceMapper;
import com.ch.managementsystem.service.AttendanceService;
import org.springframework.stereotype.Service;

@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {
}
