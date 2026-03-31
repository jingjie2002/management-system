package com.ch.managementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.managementsystem.entity.SysUser;

/**
 * 系统用户服务接口
 * 继承自MyBatis-Plus的IService，提供基础的CRUD操作
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户对象
     */
    SysUser getByUsername(String username);
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return JWT token
     */
    String login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户对象
     */
    void register(SysUser user);
}
