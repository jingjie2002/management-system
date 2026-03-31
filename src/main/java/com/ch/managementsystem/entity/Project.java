// package com.ch.managementsystem.entity;

// import java.io.Serializable;
// import java.time.LocalDate;
// import java.time.LocalDateTime;

// import com.baomidou.mybatisplus.annotation.FieldFill;
// import com.baomidou.mybatisplus.annotation.IdType;
// import com.baomidou.mybatisplus.annotation.TableField;
// import com.baomidou.mybatisplus.annotation.TableId;
// import com.baomidou.mybatisplus.annotation.TableLogic;
// import com.baomidou.mybatisplus.annotation.TableName;

// import lombok.Data;

// @Data
// @TableName("t_project")
// public class Project implements Serializable {
//     private static final long serialVersionUID = 1L;

//     @TableId(type = IdType.AUTO)
//     private Long id;

//     private String name;
//     private String description;
//     private LocalDate startDate;
//     private LocalDate endDate;
//     private Integer status; // 0-Pending未开始, 1-Ongoing进行中 2-Completed已完成, 3-Suspended已暂停
//     private Long managerId;

//     @TableField(fill = FieldFill.INSERT)
//     private LocalDateTime createTime;

//     @TableField(fill = FieldFill.INSERT_UPDATE)
//     private LocalDateTime updateTime;

//     @TableLogic
//     private Integer deleted;
// }


package com.ch.managementsystem.entity; // 声明当前类所在的包路径，表示该类属于实体类（Entity）包

import java.io.Serializable; // 导入Serializable接口，表示该类可序列化
import java.time.LocalDate; // 导入LocalDate类，用于表示日期（不包含时间）
import java.time.LocalDateTime; // 导入LocalDateTime类，用于表示日期和时间

import com.baomidou.mybatisplus.annotation.FieldFill; // 导入MyBatis-Plus的FieldFill注解，用于自动填充字段
import com.baomidou.mybatisplus.annotation.IdType; // 导入IdType枚举，用于设置主键生成策略
import com.baomidou.mybatisplus.annotation.TableField; // 导入TableField注解，用于标记表字段
import com.baomidou.mybatisplus.annotation.TableId; // 导入TableId注解，用于标记主键
import com.baomidou.mybatisplus.annotation.TableLogic; // 导入TableLogic注解，用于标记逻辑删除字段
import com.baomidou.mybatisplus.annotation.TableName; // 导入TableName注解，用于标记表名

import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter等方法

@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
@TableName("t_project") // 设置该实体类对应数据库中的表名为t_project
public class Project implements Serializable { // 定义项目实体类，表示公司中的项目

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @TableId(type = IdType.AUTO) // 设置主键ID策略为自动增长
    private Long id; // 定义项目ID属性，数据类型为Long

    private String name; // 定义项目名称属性，数据类型为String
    private String description; // 定义项目描述属性，数据类型为String
    private LocalDate startDate; // 定义项目开始日期属性，数据类型为LocalDate（仅包含日期，不包含时间）
    private LocalDate endDate; // 定义项目结束日期属性，数据类型为LocalDate（仅包含日期，不包含时间）
    private Integer status; // 定义项目状态属性，数据类型为Integer，表示项目的当前状态（0-未开始，1-进行中，2-已完成，3-已暂停）
    private Long managerId; // 定义项目经理ID属性，数据类型为Long，用于关联项目经理的ID

    @TableField(fill = FieldFill.INSERT) // 设置该字段在插入时自动填充
    private LocalDateTime createTime; // 定义记录创建时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableField(fill = FieldFill.INSERT_UPDATE) // 设置该字段在插入和更新时自动填充
    private LocalDateTime updateTime; // 定义记录更新时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableLogic // 设置该字段为逻辑删除标记字段
    private Integer deleted; // 定义逻辑删除标记字段，数据类型为Integer，表示该项目是否已被删除（例如：0-未删除，1-已删除）
}