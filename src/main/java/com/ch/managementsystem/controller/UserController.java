package com.ch.managementsystem.controller; // 声明当前类所在的包路径，表示该类属于系统的controller控制层

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的LambdaQueryWrapper，用于构建类型安全的查询条件
import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result，用于标准化接口返回格式
import com.ch.managementsystem.entity.SysUser; // 导入系统用户实体类SysUser，对应数据库中的用户表
import com.ch.managementsystem.entity.SysUserRole; // 导入用户角色关系实体类SysUserRole，对应用户与角色的关系表
import com.ch.managementsystem.entity.dto.UserRoleAssignDTO; // 导入用户角色分配DTO对象，用于接收前端提交的用户角色分配数据
import com.ch.managementsystem.mapper.SysUserRoleMapper; // 导入用户角色关系Mapper接口，用于操作数据库中的用户角色关系表
import com.ch.managementsystem.service.SysUserService; // 导入用户业务服务接口SysUserService
import org.springframework.beans.factory.annotation.Autowired; // 导入Spring自动注入注解，用于自动装配Bean对象
import org.springframework.security.crypto.password.PasswordEncoder; // 导入密码加密接口PasswordEncoder，用于对用户密码进行加密
import org.springframework.security.access.prepost.PreAuthorize; // 导入权限控制注解，用于方法级别的权限验证
import org.springframework.web.bind.annotation.*; // 导入Spring Web相关注解（RestController、GetMapping、PostMapping等）

import java.util.List; // 导入List集合类，用于存储多个对象

@RestController // 标记该类为REST控制器，返回数据默认以JSON格式输出
@RequestMapping("/api/users") // 定义当前控制器所有接口的统一访问路径前缀 /api/users
public class UserController { // 定义用户管理控制器类

    @Autowired // 自动注入SysUserService对象
    private SysUserService sysUserService; // 声明用户服务对象，用于处理用户相关业务逻辑

    @Autowired // 自动注入SysUserRoleMapper对象
    private SysUserRoleMapper userRoleMapper; // 声明用户角色关系Mapper，用于操作用户与角色关系表

    @Autowired // 自动注入PasswordEncoder对象
    private PasswordEncoder passwordEncoder; // 声明密码加密器，用于对用户密码进行加密处理

    @GetMapping // 定义GET请求接口，用于获取所有用户
    @PreAuthorize("hasRole('admin')") // 设置访问权限：只有admin角色才能访问该接口
    public Result<List<SysUser>> getAllUsers() { // 定义获取所有用户的方法
        return Result.success(sysUserService.list()); // 调用service层查询所有用户，并返回成功结果
    }

    @GetMapping("/{id}") // 定义GET请求接口，通过用户ID获取用户信息
    @PreAuthorize("hasRole('admin')")
    public Result<SysUser> getUserById(@PathVariable Long id) { // 从URL路径中获取用户ID参数
        return Result.success(sysUserService.getById(id)); // 调用service层根据ID查询用户并返回结果
    }
    
    @GetMapping("/info") // 定义GET请求接口，用于根据用户名查询用户信息
    @PreAuthorize("hasRole('admin')")
    public Result<SysUser> getUserInfo(@RequestParam String username) { // 从请求参数中获取username
        return Result.success(sysUserService.getByUsername(username)); // 调用service层根据用户名查询用户并返回结果
    }

    @PostMapping // 定义POST请求接口，用于新增用户
    @PreAuthorize("hasRole('admin')") // 设置访问权限：只有admin角色才能创建用户
    public Result<Boolean> save(@RequestBody SysUser user) { // 接收请求体中的SysUser对象
        if (user.getPassword() != null && !user.getPassword().isEmpty()) { // 判断用户密码是否不为空
            user.setPassword(passwordEncoder.encode(user.getPassword())); // 使用PasswordEncoder对密码进行加密
        }
        return Result.success(sysUserService.save(user)); // 调用service层保存用户并返回结果
    }

    @PutMapping // 定义PUT请求接口，用于更新用户信息
    @PreAuthorize("hasRole('admin')") // 设置访问权限：只有admin角色才能更新用户
    public Result<Boolean> update(@RequestBody SysUser user) { // 接收请求体中的SysUser对象
        if (user.getPassword() != null && !user.getPassword().isEmpty()) { // 判断是否需要更新密码
            user.setPassword(passwordEncoder.encode(user.getPassword())); // 如果有密码则进行加密
        } else { // 如果密码为空
            user.setPassword(null); // 设置为null，表示不更新密码字段
        }
        return Result.success(sysUserService.updateById(user)); // 调用service层根据ID更新用户信息
    }

    @DeleteMapping("/{id}") // 定义DELETE请求接口，用于删除指定ID的用户
    @PreAuthorize("hasRole('admin')") // 设置访问权限：只有admin角色才能删除用户
    public Result<Boolean> delete(@PathVariable Long id) { // 从URL路径中获取用户ID
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id)); // 先删除用户与角色之间的关系记录
        return Result.success(sysUserService.removeById(id)); // 再调用service层删除用户
    }

    @PostMapping("/assign-roles") // 定义POST请求接口，用于给用户分配角色
    @PreAuthorize("hasRole('admin')") // 设置访问权限：只有admin角色才能分配角色
    public Result<Boolean> assignRoles(@RequestBody UserRoleAssignDTO dto) { // 接收前端提交的用户角色分配数据
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, dto.getUserId())); // 先删除该用户已有的角色关系
        if (dto.getRoleIds() != null) { // 判断是否存在需要分配的角色ID列表
            for (Long roleId : dto.getRoleIds()) { // 遍历所有角色ID
                SysUserRole rel = new SysUserRole(); // 创建用户角色关系对象
                rel.setUserId(dto.getUserId()); // 设置用户ID
                rel.setRoleId(roleId); // 设置角色ID
                userRoleMapper.insert(rel); // 将用户与角色的关系插入数据库
            }
        }
        return Result.success(true); // 返回操作成功结果
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('admin')")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> request) {
        Integer status = request.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值无效，必须为0（禁用）或1（启用）");
        }
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        return Result.success(sysUserService.updateById(user));
    }
}
