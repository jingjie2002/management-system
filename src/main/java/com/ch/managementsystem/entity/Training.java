package com.ch.managementsystem.entity;                 //实体类所在的包路径

import java.io.Serializable;                             //序列化接口
import java.time.LocalDateTime;                          //Java8 时间类型

import com.baomidou.mybatisplus.annotation.FieldFill;  //MyBatis-Plus 注解包
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;    //Jackson时间格式化注解

import lombok.Data;                                     //ombok注解，用于自动生成getter/setter

@Data                                                   // lombok 注解，用于自动生成 getter/setter
@TableName("t_training")                                // 指定该实体类对应的数据库表名为 t_training
public class Training implements Serializable {         // 培训实体类，实现序列化接口
    private static final long serialVersionUID = 1L;    // 序列化版本号，用于保证序列化兼容

    @TableId(type = IdType.AUTO)                          // 序列化版本号，用于保证序列化兼容
    private Long id;                                      // 培训ID，数据库主键

    private String title;                                 // 培训ID，数据库主键
    private String content;                                //培训内容或培训描述
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")               //指定JSON返回时时间格式（培训开始时间)
    private LocalDateTime startTime;                           // 培训开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")               //指定JSON返回时时间格式(培训结束时间)
    private LocalDateTime endTime;                                //培训结束时间
    private String location;                                    // 培训地点
    private String trainer;                                    // 培训讲师或培训负责人
    private Integer status;                                     //培训状态：0-Planned, 1-Ongoing, 2-Completed
    
    @TableField(fill = FieldFill.INSERT)                         // 插入数据时自动填充该字段               
    private LocalDateTime createTime;                           //插入数据时自动填充该字段          

    @TableField(fill = FieldFill.INSERT_UPDATE)                                
    private LocalDateTime updateTime;                           // 记录最后更新时间          
    
    @TableLogic                                                   // 逻辑删除字段（0表示未删除，1表示已删除）                   
    private Integer deleted;                                      // 逻辑删除标识                       
}
