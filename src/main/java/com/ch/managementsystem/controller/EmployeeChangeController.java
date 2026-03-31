package com.ch.managementsystem.controller;

import com.ch.managementsystem.common.Result;
import com.ch.managementsystem.entity.dto.EmployeeChangeApproveDTO;
import com.ch.managementsystem.entity.dto.EmployeeChangeCreateDTO;
import com.ch.managementsystem.entity.dto.EmployeeChangeExecuteDTO;
import com.ch.managementsystem.entity.vo.EmployeeChangeDetailVO;
import com.ch.managementsystem.service.EmployeeChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee-changes")
public class EmployeeChangeController {

    @Autowired
    private EmployeeChangeService employeeChangeService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('employee')")
    public Result<List<EmployeeChangeDetailVO>> myChanges() {
        return Result.success(employeeChangeService.listForCurrentUser());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<List<EmployeeChangeDetailVO>> listAll() {
        return Result.success(employeeChangeService.listAllChanges());
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('employee')")
    public Result<EmployeeChangeDetailVO> apply(@RequestBody EmployeeChangeCreateDTO dto) {
        return Result.success(employeeChangeService.apply(dto));
    }

    @PostMapping("/direct")
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<EmployeeChangeDetailVO> directCreate(@RequestBody EmployeeChangeCreateDTO dto) {
        return Result.success(employeeChangeService.directCreate(dto));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<EmployeeChangeDetailVO> approve(@PathVariable("id") Long id, @RequestBody EmployeeChangeApproveDTO dto) {
        return Result.success(employeeChangeService.approve(id, dto));
    }

    @PostMapping("/{id}/execute")
    @PreAuthorize("hasRole('admin')")
    public Result<EmployeeChangeDetailVO> execute(@PathVariable("id") Long id, @RequestBody EmployeeChangeExecuteDTO dto) {
        return Result.success(employeeChangeService.execute(id, dto));
    }
}
