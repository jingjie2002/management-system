package com.ch.managementsystem.controller; // 声明当前类所在的包路径，表示该类属于系统的controller控制层

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的LambdaQueryWrapper，用于构建类型安全的查询条件
import com.baomidou.mybatisplus.core.metadata.IPage; // 导入分页结果接口IPage，用于封装分页查询返回的数据
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入Page类，用于创建分页对象
import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result，用于标准化接口返回格式
import com.ch.managementsystem.entity.Training; // 导入Training实体类，对应数据库中的培训信息表
import com.ch.managementsystem.service.TrainingService; // 导入培训业务服务接口TrainingService
import org.springframework.beans.factory.annotation.Autowired; // 导入Spring自动注入注解，用于自动注入Bean
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils; // 导入字符串工具类，用于判断字符串是否为空或有内容
import org.springframework.web.bind.annotation.*; // 导入Spring Web相关注解（RestController、GetMapping、PostMapping等）

@RestController // 标记该类为REST风格控制器，返回数据默认是JSON格式
@RequestMapping("/api/training") // 定义该控制器所有接口的统一访问路径前缀 /api/training
public class TrainingController { // 定义培训管理控制器类

    @Autowired // 自动注入TrainingService对象
    private TrainingService trainingService; // 声明培训服务层对象，用于处理培训相关业务逻辑

    @GetMapping // 定义GET请求接口，用于分页查询培训信息
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<IPage<Training>> getPage( // 定义分页查询方法，返回封装后的分页培训数据
            @RequestParam(defaultValue = "1") int page, // 接收请求参数page，表示当前页，默认值为1
            @RequestParam(defaultValue = "10") int size, // 接收请求参数size，表示每页条数，默认值为10
            @RequestParam(required = false) String title) { // 接收可选参数title，用于按培训标题进行模糊查询
        
        Page<Training> pageParam = new Page<>(page, size); // 创建分页对象，指定当前页和每页数量
        LambdaQueryWrapper<Training> wrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询条件构造器
        if (StringUtils.hasText(title)) { // 如果title参数不为空且有内容
            wrapper.like(Training::getTitle, title); // 添加模糊查询条件：培训标题包含title
        }
        wrapper.orderByDesc(Training::getStartTime); // 按培训开始时间降序排序（最新的培训排在前面）
        
        return Result.success(trainingService.page(pageParam, wrapper)); // 调用Service层分页查询方法，并返回统一成功结果
    }

    @PostMapping // 定义POST请求接口，用于新增培训信息
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> save(@RequestBody Training training) { // 接收请求体中的Training对象
        return Result.success(trainingService.save(training)); // 调用Service层保存培训信息，并返回操作结果
    }

    @PutMapping // 定义PUT请求接口，用于更新培训信息
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> update(@RequestBody Training training) { // 接收请求体中的Training对象（包含要更新的数据）
        return Result.success(trainingService.updateById(training)); // 调用Service层根据ID更新培训信息
    }

    @DeleteMapping("/{id}") // 定义DELETE请求接口，通过路径参数删除指定培训
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> delete(@PathVariable Long id) { // 从URL路径中获取培训ID
        return Result.success(trainingService.removeById(id)); // 调用Service层根据ID删除培训信息
    }
    
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> deleteBatch(@RequestBody java.util.Map<String, java.util.List<Long>> request) {
        java.util.List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的培训记录");
        }
        return Result.success(trainingService.removeBatchByIds(ids));
    }
}
