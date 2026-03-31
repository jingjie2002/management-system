package com.ch.managementsystem.common.model; // 定义当前类所在的包路径，用于存放系统中的通用数据模型类

import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter、toString等方法

@Data // Lombok注解，自动生成getter、setter、equals、hashCode和toString方法
public class PageQuery { // 定义分页查询参数模型类，用于接收前端传递的分页参数

    private Integer page = 1; // 当前页码，默认值为1，表示从第一页开始查询

    private Integer size = 10; // 每页显示的数据条数，默认值为10条
}