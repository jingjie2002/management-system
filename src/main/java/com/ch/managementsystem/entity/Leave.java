// package com.ch.managementsystem.entity;

// import com.baomidou.mybatisplus.annotation.*;
// import com.fasterxml.jackson.annotation.JsonFormat;
// import lombok.Data;
// import java.io.Serializable;
// import java.time.LocalDateTime;

// @Data
// @TableName("t_leave")
// public class Leave implements Serializable {
//     private static final long serialVersionUID = 1L;

//     @TableId(type = IdType.AUTO)
//     private Long id;

//     private Long employeeId;
//     private Integer type; // 1-Sick, 2-Casual, 3-Annual
//     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//     private LocalDateTime startTime;
//     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//     private LocalDateTime endTime;
//     private String reason;
//     private Integer status; // 0-Pending, 1-Approved, 2-Rejected
//     private String approver;

//     @TableField(fill = FieldFill.INSERT)
//     private LocalDateTime createTime;
// }


package com.ch.managementsystem.entity; // 声明当前类所在的包路径，表示该类属于实体类（Entity）包

import com.baomidou.mybatisplus.annotation.*; // 导入MyBatis-Plus的注解，支持数据库操作（如@TableName、@TableId、@TableField等）
import com.fasterxml.jackson.annotation.JsonFormat; // 导入Jackson的JsonFormat注解，用于自定义日期时间的格式化
import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可序列化
import java.time.LocalDateTime; // 导入LocalDateTime类，用于表示日期和时间

@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
@TableName("t_leave") // 设置该实体类对应数据库中的表名为t_leave
public class Leave implements Serializable { // 定义请假实体类，表示员工的请假记录

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @TableId(type = IdType.AUTO) // 设置主键ID策略为自动增长
    private Long id; // 定义请假记录的ID属性，数据类型为Long

    private Long employeeId; // 定义员工ID属性，数据类型为Long，用于关联员工表
    private Integer type; // 定义请假类型属性，数据类型为Integer，表示请假类型（1-病假，2-事假，3-年假）
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 设置JSON序列化时的日期格式，使用特定的格式化模式
    private LocalDateTime startTime; // 定义请假开始时间属性，数据类型为LocalDateTime（包含日期和时间）
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 设置JSON序列化时的日期格式，使用特定的格式化模式
    private LocalDateTime endTime; // 定义请假结束时间属性，数据类型为LocalDateTime（包含日期和时间）

    private String reason; // 定义请假理由属性，数据类型为String，描述请假原因

    private Integer status; // 定义请假状态属性，数据类型为Integer，表示请假状态（0-待审批，1-已批准，2-已拒绝）

    private String approver; // 定义审批人属性，数据类型为String，表示批准该请假的人

    @TableField(fill = FieldFill.INSERT) // 设置该字段在插入时自动填充
    private LocalDateTime createTime; // 定义记录创建时间属性，数据类型为LocalDateTime（包含日期和时间）
}