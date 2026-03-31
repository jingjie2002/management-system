package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.entity.SysRole;
import com.ch.managementsystem.entity.SysUser;
import com.ch.managementsystem.entity.SysUserRole;
import com.ch.managementsystem.mapper.SysRoleMapper;
import com.ch.managementsystem.mapper.SysUserMapper;
import com.ch.managementsystem.mapper.SysUserRoleMapper;
import com.ch.managementsystem.service.SysUserService;
import com.ch.managementsystem.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of System User Service
 * Handles business logic for user management, including authentication.
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * Retrieve user by username.
     * @param username The username to search for.
     * @return SysUser object if found, null otherwise.
     */
    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    /**
     * Authenticate user and generate JWT token.
     * @param username Username provided by user.
     * @param password Raw password provided by user.
     * @return JWT Token string if authentication is successful.
     */
    @Override
    public String login(String username, String password) {
        // Authenticate using Spring Security's AuthenticationManager
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        // If successful, generate and return JWT token
        return jwtUtils.generateToken(username);
    }

    /**
     * Register a new user.
     * Checks if username exists and encodes the password before saving.
     * @param user The user object containing registration details.
     */
    @Override
    public void register(SysUser user) {
        if (getByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        // Encode password using BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save user to database
        this.save(user);
        assignDefaultEmployeeRole(user.getId());
    }

    private void assignDefaultEmployeeRole(Long userId) {
        SysRole employeeRole = sysRoleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getRoleKey, "employee")
                        .last("limit 1")
        );
        if (employeeRole == null) {
            throw new RuntimeException("Default employee role is not configured");
        }
        SysUserRole relation = new SysUserRole();
        relation.setUserId(userId);
        relation.setRoleId(employeeRole.getId());
        sysUserRoleMapper.insert(relation);
    }
}
