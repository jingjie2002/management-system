package com.ch.managementsystem.mapper; // 声明当前接口所在的包路径，表示该接口属于数据库映射包

import com.baomidou.mybatisplus.core.mapper.BaseMapper; // 导入MyBatis-Plus的BaseMapper接口，提供了常用的数据库操作方法
import com.ch.managementsystem.entity.Department; // 导入Department实体类，表示数据库中的部门表
import org.apache.ibatis.annotations.Mapper; // 导入@Mapper注解，标记为MyBatis的Mapper接口

@Mapper // MyBatis的注解，表示该接口是MyBatis的Mapper，负责与数据库进行交互
public interface DepartmentMapper extends BaseMapper<Department> { // 定义DepartmentMapper接口，继承BaseMapper接口

    // 继承自BaseMapper的通用方法：
    // - insert(T entity) 插入一条记录
    // - deleteById(Serializable id) 删除指定ID的记录
    // - updateById(T entity) 根据ID更新记录
    // - selectById(Serializable id) 根据ID查询记录
    // - selectList(Wrapper<T> queryWrapper) 根据条件查询多条记录
    // - selectOne(Wrapper<T> queryWrapper) 根据条件查询一条记录
    // 其他常用方法可参考MyBatis-Plus的文档

}