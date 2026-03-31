package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.common.exception.BizException;
import com.ch.managementsystem.entity.Employee;
import com.ch.managementsystem.entity.EmployeeChange;
import com.ch.managementsystem.entity.EmployeeChangeLog;
import com.ch.managementsystem.entity.SysUser;
import com.ch.managementsystem.entity.dto.EmployeeChangeApproveDTO;
import com.ch.managementsystem.entity.dto.EmployeeChangeCreateDTO;
import com.ch.managementsystem.entity.dto.EmployeeChangeExecuteDTO;
import com.ch.managementsystem.entity.vo.EmployeeChangeDetailVO;
import com.ch.managementsystem.entity.vo.EmployeeChangeLogVO;
import com.ch.managementsystem.mapper.EmployeeChangeLogMapper;
import com.ch.managementsystem.mapper.EmployeeChangeMapper;
import com.ch.managementsystem.service.EmployeeChangeService;
import com.ch.managementsystem.service.EmployeeService;
import com.ch.managementsystem.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeChangeServiceImpl extends ServiceImpl<EmployeeChangeMapper, EmployeeChange> implements EmployeeChangeService {

    private static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    private static final String STATUS_APPROVED_PENDING_EFFECTIVE = "APPROVED_PENDING_EFFECTIVE";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_EXECUTED = "EXECUTED";

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private EmployeeChangeLogMapper employeeChangeLogMapper;

    @Override
    @Transactional
    public EmployeeChangeDetailVO apply(EmployeeChangeCreateDTO dto) {
        SysUser currentUser = getCurrentUser();
        Employee employee = getActiveEmployeeByUserId(currentUser.getId());
        EmployeeChange change = buildChange(dto, employee, currentUser.getId(), "EMPLOYEE_APPLY", STATUS_PENDING_APPROVAL);
        save(change);
        saveLog(change.getId(), "CREATED", currentUser, buildCreatedContent(employee, dto));
        return toDetailVO(change.getId());
    }

    @Override
    @Transactional
    public EmployeeChangeDetailVO directCreate(EmployeeChangeCreateDTO dto) {
        if (dto.getEmployeeId() == null) {
            throw new BizException(400, "Employee id is required");
        }

        SysUser currentUser = getCurrentUser();
        Employee employee = getActiveEmployeeById(dto.getEmployeeId());
        EmployeeChange change = buildChange(dto, employee, currentUser.getId(), "HR_DIRECT", STATUS_APPROVED_PENDING_EFFECTIVE);
        change.setApproverUserId(currentUser.getId());
        change.setApproveRemark("Directly approved by HR");
        change.setApproveTime(LocalDateTime.now());
        save(change);
        saveLog(change.getId(), "CREATED", currentUser, buildCreatedContent(employee, dto));
        saveLog(change.getId(), "DIRECT_APPROVED", currentUser, "HR directly created and approved this change");
        return toDetailVO(change.getId());
    }

    @Override
    @Transactional
    public EmployeeChangeDetailVO approve(Long changeId, EmployeeChangeApproveDTO dto) {
        EmployeeChange change = getChangeOrThrow(changeId);
        if (!STATUS_PENDING_APPROVAL.equals(change.getStatus())) {
            throw new BizException(400, "Current status does not allow approval");
        }

        SysUser currentUser = getCurrentUser();
        boolean approved = Boolean.TRUE.equals(dto.getApproved());
        change.setApproverUserId(currentUser.getId());
        change.setApproveRemark(dto.getRemark());
        change.setApproveTime(LocalDateTime.now());
        change.setStatus(approved ? STATUS_APPROVED_PENDING_EFFECTIVE : STATUS_REJECTED);
        updateById(change);

        saveLog(
                change.getId(),
                approved ? "APPROVED" : "REJECTED",
                currentUser,
                StringUtils.hasText(dto.getRemark()) ? dto.getRemark() : (approved ? "HR approved this change" : "HR rejected this change")
        );
        return toDetailVO(changeId);
    }

    @Override
    @Transactional
    public EmployeeChangeDetailVO execute(Long changeId, EmployeeChangeExecuteDTO dto) {
        EmployeeChange change = getChangeOrThrow(changeId);
        if (!STATUS_APPROVED_PENDING_EFFECTIVE.equals(change.getStatus())) {
            throw new BizException(400, "Current status does not allow execution");
        }

        SysUser currentUser = getCurrentUser();
        Employee employee = getActiveEmployeeById(change.getEmployeeId());
        Employee updateRef = new Employee();
        updateRef.setId(employee.getId());
        updateRef.setDeptId(change.getAfterDeptId());
        updateRef.setPosition(change.getAfterPosition());
        updateRef.setJobLevelId(change.getAfterJobLevelId());
        updateRef.setBaseSalary(change.getAfterSalary());
        employeeService.updateById(updateRef);

        change.setStatus(STATUS_EXECUTED);
        change.setExecuteUserId(currentUser.getId());
        change.setExecuteTime(LocalDateTime.now());
        updateById(change);

        saveLog(
                change.getId(),
                "EXECUTED",
                currentUser,
                StringUtils.hasText(dto.getRemark()) ? dto.getRemark() : "Admin executed this approved change"
        );
        return toDetailVO(changeId);
    }

    @Override
    public List<EmployeeChangeDetailVO> latestForEmployee(Long employeeId, int limit) {
        if (employeeId == null || limit <= 0) {
            return List.of();
        }

        int safeLimit = Math.min(limit, 10);
        return list(
                new LambdaQueryWrapper<EmployeeChange>()
                        .eq(EmployeeChange::getEmployeeId, employeeId)
                        .orderByDesc(EmployeeChange::getCreateTime)
                        .orderByDesc(EmployeeChange::getId)
                        .last("limit " + safeLimit)
        ).stream()
                .map(this::toDetailVO)
                .toList();
    }

    @Override
    public List<EmployeeChangeDetailVO> listForCurrentUser() {
        SysUser currentUser = getCurrentUser();
        Employee employee = getActiveEmployeeByUserId(currentUser.getId());
        return listChanges(
                new LambdaQueryWrapper<EmployeeChange>()
                        .eq(EmployeeChange::getEmployeeId, employee.getId())
                        .orderByDesc(EmployeeChange::getCreateTime)
                        .orderByDesc(EmployeeChange::getId)
        );
    }

    @Override
    public List<EmployeeChangeDetailVO> listAllChanges() {
        return listChanges(
                new LambdaQueryWrapper<EmployeeChange>()
                        .orderByDesc(EmployeeChange::getCreateTime)
                        .orderByDesc(EmployeeChange::getId)
        );
    }

    private EmployeeChange buildChange(EmployeeChangeCreateDTO dto, Employee employee, Long applicantUserId, String applyMode, String status) {
        EmployeeChange change = new EmployeeChange();
        change.setEmployeeId(employee.getId());
        change.setChangeType(dto.getChangeType());
        change.setApplyMode(applyMode);
        change.setStatus(status);
        change.setReason(dto.getReason());
        change.setEffectiveDate(dto.getEffectiveDate());
        change.setBeforeDeptId(employee.getDeptId());
        change.setAfterDeptId(dto.getAfterDeptId());
        change.setBeforePosition(employee.getPosition());
        change.setAfterPosition(dto.getAfterPosition());
        change.setBeforeJobLevelId(employee.getJobLevelId());
        change.setAfterJobLevelId(dto.getAfterJobLevelId());
        change.setBeforeSalary(employee.getBaseSalary());
        change.setAfterSalary(dto.getAfterSalary());
        change.setApplicantUserId(applicantUserId);
        return change;
    }

    private void saveLog(Long changeId, String actionType, SysUser user, String content) {
        EmployeeChangeLog log = new EmployeeChangeLog();
        log.setChangeId(changeId);
        log.setActionType(actionType);
        log.setOperatorUserId(user.getId());
        log.setOperatorName(resolveOperatorName(user));
        log.setContent(content);
        employeeChangeLogMapper.insert(log);
    }

    private String buildCreatedContent(Employee employee, EmployeeChangeCreateDTO dto) {
        String target = StringUtils.hasText(dto.getAfterPosition()) ? dto.getAfterPosition() : "new position";
        return employee.getName() + " submitted " + dto.getChangeType() + " request for " + target;
    }

    private EmployeeChange getChangeOrThrow(Long changeId) {
        EmployeeChange change = getById(changeId);
        if (change == null) {
            throw new BizException(404, "Employee change record does not exist");
        }
        return change;
    }

    private SysUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BizException(401, "Current user is not authenticated");
        }

        SysUser user = sysUserService.getByUsername(authentication.getName());
        if (user == null) {
            throw new BizException(404, "Current user does not exist");
        }
        return user;
    }

    private Employee getActiveEmployeeByUserId(Long userId) {
        Employee employee = employeeService.getOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUserId, userId)
                        .eq(Employee::getStatus, 1)
                        .isNull(Employee::getLeaveDate)
                        .last("limit 1")
        );
        if (employee == null) {
            throw new BizException(400, "Current account is not bound to an active employee");
        }
        return employee;
    }

    private Employee getActiveEmployeeById(Long employeeId) {
        Employee employee = employeeService.getOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getId, employeeId)
                        .eq(Employee::getStatus, 1)
                        .isNull(Employee::getLeaveDate)
                        .last("limit 1")
        );
        if (employee == null) {
            throw new BizException(404, "Employee does not exist or is not active");
        }
        return employee;
    }

    private String resolveOperatorName(SysUser user) {
        return StringUtils.hasText(user.getRealName()) ? user.getRealName() : user.getUsername();
    }

    private EmployeeChangeDetailVO toDetailVO(Long changeId) {
        return toDetailVO(getChangeOrThrow(changeId));
    }

    private List<EmployeeChangeDetailVO> listChanges(LambdaQueryWrapper<EmployeeChange> queryWrapper) {
        return list(queryWrapper).stream()
                .map(this::toDetailVO)
                .toList();
    }

    private EmployeeChangeDetailVO toDetailVO(EmployeeChange change) {
        EmployeeChangeDetailVO detailVO = new EmployeeChangeDetailVO();
        BeanUtils.copyProperties(change, detailVO);
        List<EmployeeChangeLogVO> logs = employeeChangeLogMapper.selectList(
                        new LambdaQueryWrapper<EmployeeChangeLog>()
                                .eq(EmployeeChangeLog::getChangeId, change.getId())
                                .orderByAsc(EmployeeChangeLog::getCreateTime)
                                .orderByAsc(EmployeeChangeLog::getId)
                ).stream()
                .map(log -> {
                    EmployeeChangeLogVO logVO = new EmployeeChangeLogVO();
                    BeanUtils.copyProperties(log, logVO);
                    return logVO;
                })
                .toList();
        detailVO.setLogs(logs);
        return detailVO;
    }
}
