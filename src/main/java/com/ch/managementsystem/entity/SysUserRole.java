package com.ch.managementsystem.entity; // 声明当前类所在的包路径，表示该类属于实体类（Entity）包

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName; // 导入MyBatis-Plus的@TableName注解，用于标记数据库表名
import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可序列化

@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
@TableName("sys_user_role") // 设置该实体类对应数据库中的表名为sys_user_role
public class SysUserRole implements Serializable { // 定义用户角色关系实体类，用于表示用户与角色的多对多关系

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId; // 定义用户ID属性，数据类型为Long，用于表示用户的唯一标识符

    @TableField("role_id")
    private Long roleId; // 定义角色ID属性，数据类型为Long，用于表示角色的唯一标识符
}
