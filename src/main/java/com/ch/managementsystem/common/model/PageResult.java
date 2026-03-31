package com.ch.managementsystem.common.model; // 定义当前类所在的包路径，用于存放系统中的通用数据模型类

import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter、toString等方法

import java.util.Collections; // 导入Java集合工具类Collections，用于创建空集合
import java.util.List; // 导入List集合接口

@Data // Lombok注解，自动生成getter、setter、equals、hashCode和toString方法
public class PageResult<T> { // 定义一个泛型分页结果类，用于统一封装分页查询返回的数据

    private Long total = 0L; // 定义总记录数，表示数据库中符合条件的数据总数量，默认值为0

    private List<T> records = Collections.emptyList(); // 定义当前页的数据列表，默认返回一个空的不可变List
}