// package com.ch.managementsystem.entity;

// import com.baomidou.mybatisplus.annotation.*;
// import lombok.Data;
// import java.io.Serializable;
// import java.time.LocalDate;
// import java.time.LocalDateTime;

// @Data
// @TableName("t_attendance")
// public class Attendance implements Serializable {
//     private static final long serialVersionUID = 1L;

//     @TableId(type = IdType.AUTO)
//     private Long id;

//     private Long employeeId;
//     private LocalDateTime checkInTime;
//     private LocalDateTime checkOutTime;
//     private LocalDate date;
//     private Integer status; // 1-Normal, 2-Late, 3-Early Leave, 4-Absent

//     @TableField(fill = FieldFill.INSERT)
//     private LocalDateTime createTime;
// }


package com.ch.managementsystem.entity; // 声明当前类所在的包路径，表示该类属于实体类（Entity）包

import com.baomidou.mybatisplus.annotation.*; // 导入MyBatis-Plus的注解，支持数据库操作（如@TableName、@TableId、@TableField等）
import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter、toString等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可序列化
import java.time.LocalDate; // 导入LocalDate类，用于表示日期（不包含时间）
import java.time.LocalDateTime; // 导入LocalDateTime类，用于表示日期和时间

@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
@TableName("t_attendance") // 设置该实体类对应数据库中的表名为t_attendance
public class Attendance implements Serializable { // 定义考勤实体类，表示员工考勤记录

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @TableId(type = IdType.AUTO) // 设置主键ID策略为自动增长
    private Long id; // 定义考勤记录的ID属性，数据类型为Long

    private Long employeeId; // 定义员工ID属性，数据类型为Long，用于关联员工信息

    private LocalDateTime checkInTime; // 定义考勤签到时间属性，数据类型为LocalDateTime（包含日期和时间）

    private LocalDateTime checkOutTime; // 定义考勤签退时间属性，数据类型为LocalDateTime（包含日期和时间）

    private LocalDate date; // 定义考勤日期属性，数据类型为LocalDate（仅包含日期，不包含时间）

    private Integer status; // 定义考勤状态属性，数据类型为Integer，表示考勤的状态（1-正常，2-迟到，3-早退，4-缺席）

    @TableField(fill = FieldFill.INSERT) // 设置该字段在插入时自动填充
    private LocalDateTime createTime; // 定义记录创建时间属性，数据类型为LocalDateTime（包含日期和时间）
}