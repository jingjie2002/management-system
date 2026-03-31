package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.entity.SysRole;
import com.ch.managementsystem.mapper.SysRoleMapper;
import com.ch.managementsystem.service.SysRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}
