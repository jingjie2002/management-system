package com.ch.managementsystem.entity; // 声明当前类所在的包路径，表示该类属于实体类（Entity）包

import java.io.Serializable; // 导入Serializable接口，表示该类可序列化
import java.math.BigDecimal; // 导入BigDecimal类，用于表示高精度的货币或数值
import java.time.LocalDateTime; // 导入LocalDateTime类，用于表示日期和时间

import com.baomidou.mybatisplus.annotation.FieldFill; // 导入MyBatis-Plus的FieldFill注解，用于自动填充字段
import com.baomidou.mybatisplus.annotation.IdType; // 导入IdType枚举，用于设置主键生成策略
import com.baomidou.mybatisplus.annotation.TableField; // 导入TableField注解，用于标记表字段
import com.baomidou.mybatisplus.annotation.TableId; // 导入TableId注解，用于标记主键
import com.baomidou.mybatisplus.annotation.TableLogic; // 导入TableLogic注解，用于标记逻辑删除字段
import com.baomidou.mybatisplus.annotation.TableName; // 导入TableName注解，用于标记表名

import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter等方法

@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
@TableName("t_performance") // 设置该实体类对应数据库中的表名为t_performance
public class Performance implements Serializable { // 定义员工绩效实体类，用于记录员工的绩效信息

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @TableId(type = IdType.AUTO) // 设置主键ID策略为自动增长
    private Long id; // 定义绩效记录ID属性，数据类型为Long

    private Long employeeId; // 定义员工ID属性，数据类型为Long，用于关联员工信息

    private String perfMonth; // 定义绩效月份属性，数据类型为String，用于表示绩效的月份（如：2023-03）

    private String comments; // 定义绩效评语属性，数据类型为String，表示对员工绩效的评价

    private BigDecimal baseSalary; // 基本工资

    private Integer attendanceDays; // 出勤天数

    private BigDecimal projectBonus; // 项目加分

    @TableField(fill = FieldFill.INSERT) // 设置该字段在插入时自动填充
    private LocalDateTime createTime; // 定义记录创建时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableField(fill = FieldFill.INSERT_UPDATE) // 设置该字段在插入和更新时自动填充
    private LocalDateTime updateTime; // 定义记录更新时间属性，数据类型为LocalDateTime（包含日期和时间）

    @TableLogic // 设置该字段为逻辑删除标记字段
    private Integer deleted; // 定义逻辑删除标记字段，数据类型为Integer，表示该记录是否已被删除（0-未删除，1-已删除）
}