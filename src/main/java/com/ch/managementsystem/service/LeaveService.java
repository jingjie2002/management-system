package com.ch.managementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.managementsystem.entity.Leave;

public interface LeaveService extends IService<Leave> {
    boolean approve(Long id, String approver, boolean approved);
}
