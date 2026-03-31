package com.ch.managementsystem.controller; // 定义当前类所在的包路径，表示该类属于控制层(controller)

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis Plus的Lambda查询条件构造器
import com.baomidou.mybatisplus.core.metadata.IPage; // 导入分页结果接口IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis Plus分页对象Page
import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result
import com.ch.managementsystem.entity.Leave; // 导入请假实体类Leave
import com.ch.managementsystem.service.LeaveService; // 导入请假业务层接口LeaveService
import org.springframework.beans.factory.annotation.Autowired; // 导入自动注入注解，用于从Spring容器中注入Bean
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*; // 导入Spring Web相关注解（如RestController、PostMapping等）

@RestController // 标记该类为REST风格控制器，返回数据默认是JSON格式
@RequestMapping("/api/leave") // 定义该控制器的统一请求路径前缀为/api/leave
public class LeaveController { // 定义请假控制器类

    @Autowired // 自动注入LeaveService对象
    private LeaveService leaveService; // 请假业务服务，用于处理数据库相关操作

    @GetMapping // 定义GET请求接口
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<IPage<Leave>> getPage( // 定义分页查询请假记录的方法，并返回统一Result结果
            @RequestParam(defaultValue = "1") int page, // 获取请求参数page，如果没有传则默认值为1
            @RequestParam(defaultValue = "10") int size, // 获取请求参数size，如果没有传则默认值为10
            @RequestParam(required = false) Long employeeId, // 获取员工ID参数，可选参数
            @RequestParam(required = false) Integer status) { // 获取请假状态参数，可选参数
        
        Page<Leave> p = new Page<>(page, size); // 创建分页对象，设置当前页码和每页条数
        LambdaQueryWrapper<Leave> wrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询条件构造器
        
        if (employeeId != null) { // 如果员工ID不为空
            wrapper.eq(Leave::getEmployeeId, employeeId); // 添加员工ID等值查询条件
        }
        if (status != null) { // 如果状态不为空
            wrapper.eq(Leave::getStatus, status); // 添加状态等值查询条件
        }
        
        wrapper.orderByDesc(Leave::getCreateTime); // 按创建时间进行降序排序
        
        return Result.success(leaveService.page(p, wrapper)); // 调用Service层分页查询方法并返回结果
    }

    @PostMapping("/apply") // 定义POST请求接口，路径为/api/leave/apply
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<Boolean> apply(@RequestBody Leave leave) { // 定义请假申请接口，并接收前端传来的请假对象
        leave.setStatus(0); // 设置请假状态为0（默认状态：待审批）
        leave.setApprover(null); // 设置审批人为空，待审批
        return Result.success(leaveService.save(leave)); // 调用Service保存请假记录并返回结果
    }

    @PutMapping("/approve/{id}") // 定义PUT请求接口，路径为/api/leave/approve/{id}
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> approve( // 定义审批接口，根据请假记录ID进行审批操作
            @PathVariable Long id, // 从URL路径中获取请假记录的ID
            @RequestParam String approver, // 获取审批人名称参数
            @RequestParam boolean approved) { // 获取是否批准的参数（true/false）
        return Result.success(leaveService.approve(id, approver, approved)); // 调用Service层审批方法并返回结果
    }
    
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> deleteBatch(@RequestBody java.util.Map<String, java.util.List<Long>> request) {
        java.util.List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        return Result.success(leaveService.removeBatchByIds(ids));
    }
}
