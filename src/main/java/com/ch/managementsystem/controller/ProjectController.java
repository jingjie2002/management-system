package com.ch.managementsystem.controller; // 声明当前类所在的包路径，属于项目中的 controller 控制层

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入 MyBatis-Plus 的 LambdaQueryWrapper，用于构建类型安全的查询条件
import com.baomidou.mybatisplus.core.metadata.IPage; // 导入 IPage 接口，用于表示分页查询的结果
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入 Page 类，用于创建分页对象
import com.ch.managementsystem.common.Result; // 导入项目中统一的返回结果封装类 Result
import com.ch.managementsystem.entity.Project; // 导入 Project 实体类，对应数据库中的项目表
import com.ch.managementsystem.service.ProjectService; // 导入 ProjectService 服务层接口，用于处理项目相关业务逻辑
import org.springframework.beans.factory.annotation.Autowired; // 导入 Spring 的自动注入注解，用于自动装配 Bean
import org.springframework.security.access.prepost.PreAuthorize; // 导入 Spring Security 的权限控制注解，用于方法访问控制
import org.springframework.util.StringUtils; // 导入字符串工具类，用于判断字符串是否有内容
import org.springframework.web.bind.annotation.*; // 导入 Spring Web 的注解（如 GetMapping、PostMapping 等）

/**
 * Controller for Project Management
 * Provides RESTful APIs for managing projects.
 */ // 类的说明注释：项目管理控制器，提供项目管理相关的 RESTful 接口
@RestController // 标记当前类为 REST 控制器，返回的数据默认是 JSON 格式
@RequestMapping("/api/projects") // 定义该控制器所有接口的统一访问路径前缀 /api/projects
@PreAuthorize("hasAnyRole('admin','employee')") // 设置访问权限：只有 admin 或 employee 角色才能访问该控制器的接口
public class ProjectController { // 定义 ProjectController 类

    @Autowired // 自动注入 Spring 容器中的 ProjectService 对象
    private ProjectService projectService; // 声明项目服务层对象，用于调用业务逻辑

    /**
     * Get a paginated list of projects.
     * @param page Current page number (default 1)
     * @param size Page size (default 10)
     * @param name Optional project name filter
     * @param status Optional project status filter
     * @return Result containing IPage of Project
     */ // 方法说明：获取项目分页列表，可按名称和状态筛选
    @GetMapping // 处理 HTTP GET 请求
    public Result<IPage<Project>> getPage( // 定义方法，返回 Result 包装的分页项目数据
            @RequestParam(defaultValue = "1") int page, // 接收请求参数 page，默认值为1（当前页）
            @RequestParam(defaultValue = "10") int size, // 接收请求参数 size，默认值为10（每页数量）
            @RequestParam(required = false) String name, // 接收可选参数 name，用于按项目名称筛选
            @RequestParam(required = false) Integer status) { // 接收可选参数 status，用于按项目状态筛选
        
        Page<Project> pageParam = new Page<>(page, size); // 创建分页对象，指定当前页和每页大小
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>(); // 创建查询条件构造器对象
        
        // Dynamic filtering
        if (StringUtils.hasText(name)) { // 如果 name 参数不为空且有内容
            wrapper.like(Project::getName, name); // 添加模糊查询条件：项目名称包含 name
        }
        if (status != null) { // 如果 status 参数不为空
            wrapper.eq(Project::getStatus, status); // 添加精确查询条件：项目状态等于 status
        }
        
        // Sort by creation time descending
        wrapper.orderByDesc(Project::getCreateTime); // 按项目创建时间倒序排序（最新的在前）
        
        return Result.success(projectService.page(pageParam, wrapper)); // 调用 service 层分页查询方法，并返回统一成功结果
    }

    /**
     * Create a new project.
     * @param project Project entity to save
     * @return Result<Boolean>
     */ // 方法说明：创建一个新的项目
    @PostMapping // 处理 HTTP POST 请求（通常用于新增数据）
    public Result<Boolean> save(@RequestBody Project project) { // 接收请求体中的 Project 对象
        return Result.success(projectService.save(project)); // 调用 service 层保存项目，并返回操作结果
    }

    /**
     * Update an existing project.
     * @param project Project entity with ID and updated fields
     * @return Result<Boolean>
     */ // 方法说明：更新已有的项目
    @PutMapping // 处理 HTTP PUT 请求（通常用于更新数据）
    public Result<Boolean> update(@RequestBody Project project) { // 接收请求体中的 Project 对象（包含要更新的字段）
        return Result.success(projectService.updateById(project)); // 调用 service 层根据 ID 更新项目
    }

    /**
     * Delete a project by ID.
     * @param id Project ID
     * @return Result<Boolean>
     */ // 方法说明：根据项目 ID 删除项目
    @DeleteMapping("/{id}") // 处理 HTTP DELETE 请求，路径中包含 id 参数
    public Result<Boolean> delete(@PathVariable Long id) { // 使用 @PathVariable 获取 URL 路径中的 id
        return Result.success(projectService.removeById(id)); // 调用 service 层根据 ID 删除项目
    }
    
    /**
     * Delete multiple projects by IDs.
     * @param request Map containing list of project IDs
     * @return Result<Boolean>
     */
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody java.util.Map<String, java.util.List<Long>> request) {
        java.util.List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的项目");
        }
        return Result.success(projectService.removeBatchByIds(ids));
    }
}