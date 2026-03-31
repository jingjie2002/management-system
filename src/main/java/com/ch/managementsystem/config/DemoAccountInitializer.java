package com.ch.managementsystem.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoAccountInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public DemoAccountInitializer(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Long adminRoleId = syncRole("Administrator", "admin", "System administrator");
        Long hrRoleId = syncRole("HR", "hr", "Human resources");
        Long employeeRoleId = syncRole("Employee", "employee", "Regular employee");

        syncUser("admin", "System Admin", "13800000000", adminRoleId);
        syncUser("hr", "HR Manager", "13800000001", hrRoleId);
        syncUser("employee", "Jane Smith", "13800000002", employeeRoleId);

        bindEmployeeAccount("employee", "Jane Smith", "IT002");
    }

    private Long syncRole(String roleName, String roleKey, String description) {
        Long roleId = queryForLong("select id from sys_role where role_key = ? and deleted = 0 limit 1", roleKey);
        if (roleId != null) {
            return roleId;
        }

        jdbcTemplate.update(
                "insert into sys_role(role_name, role_key, description, deleted) values(?,?,?,0)",
                roleName, roleKey, description
        );
        Long createdId = queryForLong("select id from sys_role where role_key = ? and deleted = 0 limit 1", roleKey);
        return createdId == null ? 0L : createdId;
    }

    private void syncUser(String username, String realName, String phone, Long roleId) {
        String encodedPassword = passwordEncoder.encode("123456");
        Long userId = queryForLong("select id from sys_user where username = ? and deleted = 0 limit 1", username);

        if (userId != null) {
            jdbcTemplate.update(
                    "update sys_user set password = ?, status = 1, real_name = ?, phone = ? where id = ?",
                    encodedPassword, realName, phone, userId
            );
        } else {
            jdbcTemplate.update(
                    "insert into sys_user(username,password,real_name,phone,status,deleted) values(?,?,?,?,?,0)",
                    username, encodedPassword, realName, phone, 1
            );
            userId = queryForLong("select id from sys_user where username = ? and deleted = 0 limit 1", username);
        }

        if (userId == null || roleId == null || roleId <= 0) {
            return;
        }

        Integer relationCount = jdbcTemplate.queryForObject(
                "select count(1) from sys_user_role where user_id = ? and role_id = ?",
                Integer.class,
                userId,
                roleId
        );
        if (relationCount == null || relationCount == 0) {
            jdbcTemplate.update(
                    "insert into sys_user_role(user_id, role_id) values(?, ?)",
                    userId,
                    roleId
            );
        }
    }

    private void bindEmployeeAccount(String username, String realName, String jobNumber) {
        Long userId = queryForLong("select id from sys_user where username = ? and deleted = 0 limit 1", username);
        if (userId == null) {
            return;
        }

        jdbcTemplate.update(
                "update sys_user set real_name = ?, status = 1 where id = ?",
                realName,
                userId
        );
        jdbcTemplate.update(
                "update t_employee set user_id = null where user_id = ? and job_number <> ? and deleted = 0",
                userId,
                jobNumber
        );
        jdbcTemplate.update(
                "update t_employee set user_id = ?, status = 1, leave_date = null where job_number = ? and deleted = 0",
                userId,
                jobNumber
        );
    }

    private Long queryForLong(String sql, String arg) {
        return jdbcTemplate.query(
                sql,
                rs -> rs.next() ? rs.getLong(1) : null,
                arg
        );
    }
}
