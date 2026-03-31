package com.ch.managementsystem.entity.dto; // 声明当前类所在的包路径，表示该类属于数据传输对象（DTO）包

import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter、toString等方法

import java.util.List; // 导入List集合类，用于存储多个角色ID

@Data // Lombok注解，自动生成getter、setter、toString、equals、hashCode等常用方法
public class UserRoleAssignDTO { // 定义用户角色分配的数据传输对象类，用于用户与角色之间的分配操作

    private Long userId; // 定义用户ID属性，数据类型为Long，用于表示需要分配角色的用户

    private List<Long> roleIds; // 定义角色ID集合属性，数据类型为List<Long>，用于存储该用户被分配的多个角色ID
}