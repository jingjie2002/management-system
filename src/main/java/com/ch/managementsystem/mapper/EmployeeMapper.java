package com.ch.managementsystem.mapper; // 声明当前接口所在的包路径，表示该接口属于数据库映射包

import com.baomidou.mybatisplus.core.mapper.BaseMapper; // 导入MyBatis-Plus的BaseMapper接口，提供了常用的数据库操作方法
import com.baomidou.mybatisplus.core.metadata.IPage; // 导入IPage接口，用于分页查询返回结果
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入Page类，用于支持分页操作
import com.ch.managementsystem.entity.Employee; // 导入Employee实体类，表示数据库中的员工表
import org.apache.ibatis.annotations.Mapper; // 导入@Mapper注解，标记为MyBatis的Mapper接口
import org.apache.ibatis.annotations.Param; // 导入@Param注解，用于标记SQL查询中的参数
import org.apache.ibatis.annotations.Select; // 导入@Select注解，用于执行自定义的SQL查询语句

@Mapper // MyBatis的注解，表示该接口是MyBatis的Mapper，负责与数据库进行交互
public interface EmployeeMapper extends BaseMapper<Employee> { // 定义EmployeeMapper接口，继承BaseMapper接口

    /**
     * 自定义分页查询方法，查询员工信息并关联部门名称
     * @param page 分页参数，包含分页信息
     * @param wrapper 查询条件包装器，允许动态添加查询条件
     * @return 返回分页结果，包含员工信息和部门名称
     */
    @Select("SELECT e.*, d.dept_name FROM t_employee e LEFT JOIN t_department d ON e.dept_id = d.id ${ew.customSqlSegment}") 
    // 使用自定义的SQL查询，查询员工信息（包括部门名称），并通过LEFT JOIN关联t_department表
    IPage<Employee> selectPageWithDept(Page<Employee> page, @Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<Employee> wrapper); 
    // 使用@Param("ew")将Wrapper参数传递到SQL语句中，实现动态查询条件
}