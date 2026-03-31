package com.ch.managementsystem.entity; // 声明当前类所在的包路径，表示该类属于实体类（Entity）包

import com.baomidou.mybatisplus.annotation.*; // 导入MyBatis-Plus的注解，支持数据库操作（如@TableName、@TableId、@TableField等）
import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可序列化
import java.time.LocalDateTime; // 导入LocalDateTime类，用于表示日期和时间

/**
 * System User Entity
 * Corresponds to the 'sys_user' table in the database.
 * Stores login credentials and basic user information.
 */
@Data // Lombok注解，自动为类生成getter、setter、toString、equals、hashCode等常用方法
@TableName("sys_user") // 设置该实体类对应数据库中的表名为sys_user
public class SysUser implements Serializable { // 定义系统用户实体类，表示系统用户的基本信息

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    /**
     * Primary Key ID (Auto-increment)
     */
    @TableId(type = IdType.AUTO) // 设置主键ID策略为自动增长
    private Long id; // 定义系统用户ID属性，数据类型为Long

    /**
     * Username (Unique login identifier)
     */
    private String username; // 定义用户名属性，数据类型为String，用于用户登录时的唯一标识符

    /**
     * Password (Encrypted using BCrypt)
     */
    private String password; // 定义密码属性，数据类型为String，用于存储加密后的用户密码

    /**
     * Real Name of the user
     */
    private String realName; // 定义用户真实姓名属性，数据类型为String

    /**
     * Email address
     */
    private String email; // 定义用户电子邮件属性，数据类型为String

    /**
     * Phone number
     */
    private String phone; // 定义用户电话号码属性，数据类型为String

    /**
     * User Status: 1 for Enabled, 0 for Disabled
     */
    private Integer status; // 定义用户状态属性，数据类型为Integer，表示用户是否启用（1-启用，0-禁用）

    /**
     * Avatar URL
     */
    private String avatar; // 定义用户头像URL属性，数据类型为String，表示用户的头像图片链接

    /**
     * Record creation time (Automatically filled on insert)
     */
    @TableField(fill = FieldFill.INSERT) // 设置该字段在插入时自动填充
    private LocalDateTime createTime; // 定义记录创建时间属性，数据类型为LocalDateTime（包含日期和时间）

    /**
     * Record update time (Automatically filled on insert and update)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE) // 设置该字段在插入和更新时自动填充
    private LocalDateTime updateTime; // 定义记录更新时间属性，数据类型为LocalDateTime（包含日期和时间）

    /**
     * Logical deletion flag: 0 for Normal, 1 for Deleted
     */
    @TableLogic // 设置该字段为逻辑删除标记字段
    private Integer deleted; // 定义逻辑删除标记字段，数据类型为Integer，表示该记录是否已被删除（0-正常，1-已删除）
}