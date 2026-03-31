package com.ch.managementsystem.entity; // 声明当前类所在的包路径，表示该类属于实体类（Entity）包

import com.baomidou.mybatisplus.annotation.*; // 导入MyBatis-Plus的注解，支持数据库操作（如@TableName、@TableId、@TableField等）
import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可序列化
import java.time.LocalDateTime; // 导入LocalDateTime类，用于表示日期和时间

@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
@TableName("sys_role") // 设置该实体类对应数据库中的表名为sys_role
public class SysRole implements Serializable { // 定义系统角色实体类，表示系统中的角色信息

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @TableId(type = IdType.AUTO) // 设置主键ID策略为自动增长
    private Long id; // 定义角色ID属性，数据类型为Long

    private String roleName; // 定义角色名称属性，数据类型为String
    private String roleKey; // 定义角色键属性，数据类型为String，通常用于标识角色的权限标识符
    private String description; // 定义角色描述属性，数据类型为String，用于描述角色的功能或权限

    @TableField(fill = FieldFill.INSERT) // 设置该字段在插入时自动填充
    private LocalDateTime createTime; // 定义记录创建时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableField(fill = FieldFill.INSERT_UPDATE) // 设置该字段在插入和更新时自动填充
    private LocalDateTime updateTime; // 定义记录更新时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableLogic // 设置该字段为逻辑删除标记字段
    private Integer deleted; // 定义逻辑删除标记字段，数据类型为Integer，表示该角色是否已被删除（例如：0-未删除，1-已删除）
}