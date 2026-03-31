// package com.ch.managementsystem.mapper;

// import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// import com.ch.managementsystem.entity.SysRole;
// import org.apache.ibatis.annotations.Mapper;
// import org.apache.ibatis.annotations.Select;
// import java.util.List;

// @Mapper
// public interface SysRoleMapper extends BaseMapper<SysRole> {
    
//     @Select("SELECT r.role_key FROM sys_role r LEFT JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
//     List<String> selectRoleKeysByUserId(Long userId);
// }


package com.ch.managementsystem.mapper; // 声明当前接口所在的包路径，表示该接口属于数据库映射包

import com.baomidou.mybatisplus.core.mapper.BaseMapper; // 导入MyBatis-Plus的BaseMapper接口，提供了常用的数据库操作方法
import com.ch.managementsystem.entity.SysRole; // 导入SysRole实体类，表示数据库中的角色表
import org.apache.ibatis.annotations.Mapper; // 导入@Mapper注解，标记为MyBatis的Mapper接口
import org.apache.ibatis.annotations.Select; // 导入@Select注解，用于执行自定义的SQL查询语句
import java.util.List; // 导入List类，用于返回多个角色信息

@Mapper // MyBatis的注解，表示该接口是MyBatis的Mapper，负责与数据库进行交互
public interface SysRoleMapper extends BaseMapper<SysRole> { // 定义SysRoleMapper接口，继承BaseMapper接口

    /**
     * 根据用户ID查询角色的role_key
     * @param userId 用户ID
     * @return 返回该用户的所有角色key列表
     */
    @Select("SELECT r.role_key FROM sys_role r LEFT JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}") 
    // 使用@Select注解执行SQL查询，LEFT JOIN sys_role 和 sys_user_role 表，通过用户ID查找该用户的所有角色key
    List<String> selectRoleKeysByUserId(Long userId); 
    // 返回一个包含角色key的列表，用于表示用户的所有角色
}