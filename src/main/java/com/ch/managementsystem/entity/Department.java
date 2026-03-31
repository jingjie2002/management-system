package com.ch.managementsystem.entity; // 声明当前类所在的包路径，表示该类属于实体类（Entity）包

import com.baomidou.mybatisplus.annotation.*; // 导入MyBatis-Plus的注解，支持数据库操作（如@TableName、@TableId、@TableField等）
import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter、toString等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可序列化
import java.time.LocalDateTime; // 导入LocalDateTime类，用于表示日期和时间
import java.util.List; // 导入List集合类，用于存储多个子部门对象

@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
@TableName("t_department") // 设置该实体类对应数据库中的表名为t_department
public class Department implements Serializable { // 定义部门实体类，表示公司中的部门

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @TableId(type = IdType.AUTO) // 设置主键ID策略为自动增长
    private Long id; // 定义部门ID属性，数据类型为Long

    private Long parentId; // 定义父部门ID属性，数据类型为Long，用于关联上级部门
    private String deptName; // 定义部门名称属性，数据类型为String
    private Integer orderNum; // 定义部门排序号属性，数据类型为Integer，用于部门显示的顺序
    private String leader; // 定义部门负责人属性，数据类型为String
    private String phone; // 定义部门联系电话属性，数据类型为String
    private String email; // 定义部门邮箱属性，数据类型为String
    private Integer status; // 定义部门状态属性，数据类型为Integer，表示部门状态（例如：1-启用，0-停用）

    @TableField(fill = FieldFill.INSERT) // 设置该字段在插入时自动填充
    private LocalDateTime createTime; // 定义部门创建时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableField(fill = FieldFill.INSERT_UPDATE) // 设置该字段在插入和更新时自动填充
    private LocalDateTime updateTime; // 定义部门更新时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableLogic // 设置该字段为逻辑删除标记字段
    private Integer deleted; // 定义逻辑删除标记字段，数据类型为Integer，表示该部门是否被删除（例如：0-未删除，1-已删除）

    @TableField(exist = false) // 设置该字段为非数据库字段，用于存储子部门列表
    private List<Department> children; // 定义子部门列表属性，存储该部门的下级部门对象
}