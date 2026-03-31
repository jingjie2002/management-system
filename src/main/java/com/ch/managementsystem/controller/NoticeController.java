package com.ch.managementsystem.controller; // 定义当前类所在的包路径，表示该类属于控制层(controller)

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis Plus的Lambda查询条件构造器
import com.baomidou.mybatisplus.core.metadata.IPage; // 导入分页结果接口IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis Plus分页对象Page
import com.ch.managementsystem.common.Result; // 导入统一返回结果封装类Result
import com.ch.managementsystem.entity.Notice; // 导入公告实体类Notice
import com.ch.managementsystem.service.NoticeService; // 导入公告业务服务接口NoticeService
import org.springframework.beans.factory.annotation.Autowired; // 导入自动注入注解，用于从Spring容器中注入Bean
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils; // 导入StringUtils工具类，用于处理字符串
import org.springframework.web.bind.annotation.*; // 导入Spring Web相关注解（如RestController、PostMapping等）

import java.time.LocalDateTime; // 导入LocalDateTime类，用于获取当前时间
import java.util.List; // 导入List集合接口，用于存储多个对象

@RestController // 标记该类为REST风格控制器，返回数据默认是JSON格式
@RequestMapping("/api/notices") // 定义该控制器的统一请求路径前缀为/api/notices
public class NoticeController { // 定义公告控制器类，负责公告相关操作

    @Autowired // 自动注入NoticeService对象
    private NoticeService noticeService; // 公告业务服务，用于处理数据库相关操作

    @GetMapping // 定义GET请求接口，用于分页查询公告
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<IPage<Notice>> getPage( // 定义分页查询公告记录的方法，并返回统一Result结果
            @RequestParam(defaultValue = "1") int page, // 获取请求参数page，如果没有传则默认值为1
            @RequestParam(defaultValue = "10") int size, // 获取请求参数size，如果没有传则默认值为10
            @RequestParam(required = false) String title) { // 获取公告标题参数，非必传

        Page<Notice> pageParam = new Page<>(page, size); // 创建分页对象，设置当前页码和每页条数
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询条件构造器
        if (StringUtils.hasText(title)) { // 如果标题不为空
            wrapper.like(Notice::getTitle, title); // 添加标题模糊查询条件
        }
        wrapper.orderByDesc(Notice::getIsTop) // 按照是否置顶字段降序排序
                .orderByDesc(Notice::getTopTime) // 按照置顶时间降序排序
                .orderByDesc(Notice::getCreateTime); // 按照创建时间降序排序
        
        return Result.success(noticeService.page(pageParam, wrapper)); // 调用Service层分页查询方法并返回结果
    }

    @GetMapping("/{id}") // 定义GET请求接口，用于根据ID获取公告详情
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<Notice> getById(@PathVariable Long id) { // 定义根据公告ID查询的方法
        return Result.success(noticeService.getById(id)); // 调用Service层根据ID查询公告并返回结果
    }

    @PostMapping // 定义POST请求接口，用于新增公告
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> save(@RequestBody Notice notice) { // 定义保存公告的方法，接收前端传来的公告对象
        return Result.success(noticeService.save(notice)); // 调用Service层保存公告并返回结果
    }

    @PutMapping // 定义PUT请求接口，用于更新公告
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> update(@RequestBody Notice notice) { // 定义更新公告的方法，接收前端传来的公告对象
        return Result.success(noticeService.updateById(notice)); // 调用Service层更新公告并返回结果
    }

    @DeleteMapping("/{id}") // 定义DELETE请求接口，用于根据ID删除公告
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> delete(@PathVariable Long id) { // 定义根据ID删除公告的方法
        return Result.success(noticeService.removeById(id)); // 调用Service层删除公告并返回结果
    }

    @DeleteMapping("/batch") // 定义DELETE请求接口，用于批量删除公告
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) { // 定义批量删除公告的方法，接收公告ID列表
        return Result.success(noticeService.removeByIds(ids)); // 调用Service层批量删除公告并返回结果
    }

    @PutMapping("/top/{id}") // 定义PUT请求接口，用于设置公告置顶
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> setTop(@PathVariable Long id, @RequestParam boolean top) { // 定义设置公告置顶的方法
        Notice notice = new Notice(); // 创建公告对象
        notice.setId(id); // 设置公告ID
        notice.setIsTop(top ? 1 : 0); // 设置公告是否置顶（1为置顶，0为不置顶）
        notice.setTopTime(top ? LocalDateTime.now() : null); // 设置置顶时间，如果置顶则为当前时间，否则为空
        return Result.success(noticeService.updateById(notice)); // 调用Service层更新公告并返回结果
    }
}
