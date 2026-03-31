// package com.ch.managementsystem.entity;

// import com.baomidou.mybatisplus.annotation.*;
// import lombok.Data;
// import java.io.Serializable;
// import java.time.LocalDateTime;

// @Data
// @TableName("t_notice")
// public class Notice implements Serializable {
//     private static final long serialVersionUID = 1L;

//     @TableId(type = IdType.AUTO)
//     private Long id;

//     private String title;
//     private String content;
//     private Integer type; // 1-Notice, 2-Announcement
//     private Integer status; // 1-Normal, 0-Closed
//     private String createBy;
//     private Integer isTop;
//     private LocalDateTime topTime;

//     @TableField(fill = FieldFill.INSERT)
//     private LocalDateTime createTime;

//     @TableField(fill = FieldFill.INSERT_UPDATE)
//     private LocalDateTime updateTime;

//     @TableLogic
//     private Integer deleted;
// }


package com.ch.managementsystem.entity; // 声明当前类所在的包路径，表示该类属于实体类（Entity）包

import com.baomidou.mybatisplus.annotation.*; // 导入MyBatis-Plus的注解，支持数据库操作（如@TableName、@TableId、@TableField等）
import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter、toString等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可序列化
import java.time.LocalDateTime; // 导入LocalDateTime类，用于表示日期和时间

@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
@TableName("t_notice") // 设置该实体类对应数据库中的表名为t_notice
public class Notice implements Serializable { // 定义通知实体类，表示公告和通知的内容

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @TableId(type = IdType.AUTO) // 设置主键ID策略为自动增长
    private Long id; // 定义通知ID属性，数据类型为Long

    private String title; // 定义通知标题属性，数据类型为String
    private String content; // 定义通知内容属性，数据类型为String
    private Integer type; // 定义通知类型属性，数据类型为Integer，表示通知类型（1-公告，2-通知）
    private Integer status; // 定义通知状态属性，数据类型为Integer，表示通知状态（1-正常，0-关闭）
    private String createBy; // 定义创建人属性，数据类型为String，表示该通知的创建人
    private Integer isTop; // 定义是否置顶属性，数据类型为Integer，表示是否置顶（1-是，0-否）
    private LocalDateTime topTime; // 定义置顶时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableField(fill = FieldFill.INSERT) // 设置该字段在插入时自动填充
    private LocalDateTime createTime; // 定义记录创建时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableField(fill = FieldFill.INSERT_UPDATE) // 设置该字段在插入和更新时自动填充
    private LocalDateTime updateTime; // 定义记录更新时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableLogic // 设置该字段为逻辑删除标记字段
    private Integer deleted; // 定义逻辑删除标记字段，数据类型为Integer，表示该通知是否已被删除（例如：0-未删除，1-已删除）
}

