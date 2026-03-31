package com.ch.managementsystem.controller; // 定义当前类所在的包路径，表示该类属于控制层(controller)

import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result
import com.ch.managementsystem.entity.SysRole; // 导入系统角色实体类SysRole
import com.ch.managementsystem.service.SysRoleService; // 导入角色业务服务接口SysRoleService
import org.springframework.beans.factory.annotation.Autowired; // 导入自动注入注解，用于从Spring容器中注入Bean
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*; // 导入Spring Web相关注解（如RestController、GetMapping、PostMapping等）

import java.util.List;

@RestController // 标记该类为REST风格控制器，返回的数据默认是JSON格式
@RequestMapping("/api/roles") // 定义该控制器所有接口的统一请求路径前缀 /api/roles
public class RoleController { // 定义角色控制器类，负责角色相关操作

    @Autowired // 自动注入SysRoleService对象
    private SysRoleService roleService; // 角色业务服务，用于处理数据库相关操作

    @GetMapping // 定义GET请求接口，用于获取角色列表
    @PreAuthorize("hasRole('admin')")
    public Result<List<SysRole>> list() { // 返回所有角色列表的方法
        return Result.success(roleService.list()); // 调用Service层的list方法，获取所有角色并返回成功结果
    }

    @PostMapping // 定义POST请求接口，用于新增角色
    @PreAuthorize("hasRole('admin')")
    public Result<Boolean> save(@RequestBody SysRole role) { // 接收请求体中的SysRole对象，并保存到数据库
        return Result.success(roleService.save(role)); // 调用Service层保存角色并返回操作结果
    }

    @PutMapping // 定义PUT请求接口，用于更新角色信息
    @PreAuthorize("hasRole('admin')")
    public Result<Boolean> update(@RequestBody SysRole role) { // 接收请求体中的SysRole对象，并更新角色信息
        return Result.success(roleService.updateById(role)); // 调用Service层根据角色ID更新角色信息
    }

    @DeleteMapping("/{id}") // 定义DELETE请求接口，用于删除指定ID的角色
    @PreAuthorize("hasRole('admin')")
    public Result<Boolean> delete(@PathVariable Long id) { // 从URL路径中获取角色ID，并删除该角色
        return Result.success(roleService.removeById(id)); // 调用Service层删除角色并返回操作结果
    }
}
