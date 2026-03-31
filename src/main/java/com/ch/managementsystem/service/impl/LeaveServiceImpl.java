package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.entity.Leave;
import com.ch.managementsystem.mapper.LeaveMapper;
import com.ch.managementsystem.service.LeaveService;
import org.springframework.stereotype.Service;

@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveMapper, Leave> implements LeaveService {
    @Override
    public boolean approve(Long id, String approver, boolean approved) {
        Leave leave = this.getById(id);
        if (leave == null) {
            return false;
        }
        leave.setStatus(approved ? 1 : 2);
        leave.setApprover(approver);
        return this.updateById(leave);
    }
}
