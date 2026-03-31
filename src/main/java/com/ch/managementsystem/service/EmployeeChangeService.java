package com.ch.managementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.managementsystem.entity.EmployeeChange;
import com.ch.managementsystem.entity.dto.EmployeeChangeApproveDTO;
import com.ch.managementsystem.entity.dto.EmployeeChangeCreateDTO;
import com.ch.managementsystem.entity.dto.EmployeeChangeExecuteDTO;
import com.ch.managementsystem.entity.vo.EmployeeChangeDetailVO;

import java.util.List;

public interface EmployeeChangeService extends IService<EmployeeChange> {
    EmployeeChangeDetailVO apply(EmployeeChangeCreateDTO dto);

    EmployeeChangeDetailVO directCreate(EmployeeChangeCreateDTO dto);

    EmployeeChangeDetailVO approve(Long changeId, EmployeeChangeApproveDTO dto);

    EmployeeChangeDetailVO execute(Long changeId, EmployeeChangeExecuteDTO dto);

    List<EmployeeChangeDetailVO> latestForEmployee(Long employeeId, int limit);

    List<EmployeeChangeDetailVO> listForCurrentUser();

    List<EmployeeChangeDetailVO> listAllChanges();
}
