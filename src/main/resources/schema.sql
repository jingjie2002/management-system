-- 企业员工管理系统数据库架构
-- 采用规范化设计，确保数据完整性和一致性

-- 1. System Users 系统用户表：存储登录账号信息
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
    password VARCHAR(100) NOT NULL COMMENT 'Password (Encrypted)',
    real_name VARCHAR(50) COMMENT 'Real Name',
    email VARCHAR(100) COMMENT 'Email',
    phone VARCHAR(20) COMMENT 'Phone Number',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-Enabled, 0-Disabled',
    avatar VARCHAR(255) COMMENT 'Avatar URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT 'Logical Delete: 1-Deleted, 0-Normal'
) COMMENT 'System User Table';

-- 2. Roles 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role Name',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role Key (e.g., admin)',
    description VARCHAR(255) COMMENT 'Description',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Role Table';

-- 3. User-Role Relation 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) COMMENT 'User-Role Relation Table';

-- 3.1 Menu 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT 'Parent Menu ID',
    menu_name VARCHAR(100) NOT NULL COMMENT 'Menu Name',
    path VARCHAR(200) COMMENT 'Route Path',
    perms VARCHAR(100) COMMENT 'Permission Key',
    icon VARCHAR(50) COMMENT 'Icon',
    order_num INT DEFAULT 0 COMMENT 'Display Order',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'System Menu Table';

-- 3.2 Role-Menu Relation 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
) COMMENT 'Role-Menu Relation Table';

-- 4. Departments 部门表
CREATE TABLE IF NOT EXISTS t_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT 'Parent Department ID',
    dept_name VARCHAR(100) NOT NULL COMMENT 'Department Name',
    order_num INT DEFAULT 0 COMMENT 'Display Order',
    leader VARCHAR(50) COMMENT 'Department Leader',
    phone VARCHAR(20) COMMENT 'Contact Phone',
    email VARCHAR(100) COMMENT 'Contact Email',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-Normal, 0-Disabled',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Department Table';

-- 5. Employees 员工表
CREATE TABLE IF NOT EXISTS t_employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT 'Associated System User ID (if any)',
    dept_id BIGINT COMMENT 'Department ID',
    job_level_id BIGINT COMMENT 'Job level ID',
    name VARCHAR(50) NOT NULL COMMENT 'Employee Name',
    gender TINYINT COMMENT 'Gender: 1-Male, 2-Female',
    job_number VARCHAR(20) NOT NULL UNIQUE COMMENT 'Job Number (统一使用大写字母+数字)',
    position VARCHAR(50) COMMENT 'Position/Job Title',
    base_salary DECIMAL(10,2) DEFAULT 5000.00 COMMENT 'Base Salary',
    hire_date DATE COMMENT 'Hire Date',
    leave_date DATE DEFAULT NULL COMMENT 'Leave Date',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-Active, 2-Resigned, 3-Leave',
    id_card VARCHAR(20) COMMENT 'ID Card Number',
    address VARCHAR(255) COMMENT 'Address',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Employee Table';

CREATE TABLE IF NOT EXISTS t_job_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level_code VARCHAR(50) NOT NULL UNIQUE,
    level_name VARCHAR(100) NOT NULL,
    level_rank INT NOT NULL,
    salary_min DECIMAL(10,2),
    salary_max DECIMAL(10,2),
    status TINYINT DEFAULT 1,
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Job level table';

CREATE TABLE IF NOT EXISTS t_employee_change (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    apply_mode VARCHAR(30) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason VARCHAR(500),
    effective_date DATE,
    before_dept_id BIGINT,
    after_dept_id BIGINT,
    before_position VARCHAR(100),
    after_position VARCHAR(100),
    before_job_level_id BIGINT,
    after_job_level_id BIGINT,
    before_salary DECIMAL(10,2),
    after_salary DECIMAL(10,2),
    applicant_user_id BIGINT,
    approver_user_id BIGINT,
    approve_remark VARCHAR(500),
    approve_time DATETIME,
    execute_user_id BIGINT,
    execute_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Employee change table';

CREATE TABLE IF NOT EXISTS t_employee_change_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    change_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    operator_user_id BIGINT,
    operator_name VARCHAR(100),
    content VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'Employee change log table';

-- 6. Notifications 通知公告表
CREATE TABLE IF NOT EXISTS t_notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT 'Notice Title',
    content TEXT COMMENT 'Notice Content',
    type TINYINT DEFAULT 1 COMMENT 'Type: 1-Notice, 2-Announcement',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-Normal, 0-Closed',
    is_top TINYINT DEFAULT 0 COMMENT 'Top Flag: 1-Top, 0-Normal',
    top_time DATETIME COMMENT 'Top Time',
    create_by VARCHAR(50) COMMENT 'Creator',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Notification Table';

-- 7. Attendance 考勤表
CREATE TABLE IF NOT EXISTS t_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    check_in_time DATETIME COMMENT 'Check-in Time',
    check_out_time DATETIME COMMENT 'Check-out Time',
    date DATE NOT NULL COMMENT 'Attendance Date',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-Normal, 2-Late, 3-Early Leave, 4-Absent',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'Attendance Table';

-- 8. Leave Applications 请假申请表
CREATE TABLE IF NOT EXISTS t_leave (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    type TINYINT NOT NULL COMMENT 'Leave Type: 1-Sick, 2-Casual, 3-Annual, etc.',
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    reason VARCHAR(500) COMMENT 'Reason',
    status TINYINT DEFAULT 0 COMMENT 'Status: 0-Pending, 1-Approved, 2-Rejected',
    approver VARCHAR(50) COMMENT 'Approver Name',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'Leave Application Table';

-- 9. Performance 绩效表
CREATE TABLE IF NOT EXISTS t_performance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    perf_month VARCHAR(20) NOT NULL COMMENT 'Month (YYYY-MM)',
    comments VARCHAR(255) COMMENT '绩效评语',
    base_salary DECIMAL(10, 2) COMMENT '基本工资',
    attendance_days INT COMMENT '出勤天数',
    project_bonus DECIMAL(10, 2) COMMENT '项目加分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Performance Table';

-- 10. Training 培训表
CREATE TABLE IF NOT EXISTS t_training (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL COMMENT 'Training Title',
    content TEXT COMMENT 'Content',
    start_time DATETIME COMMENT 'Start Time',
    end_time DATETIME COMMENT 'End Time',
    location VARCHAR(100) COMMENT 'Location',
    trainer VARCHAR(50) COMMENT 'Trainer',
    status TINYINT DEFAULT 0 COMMENT 'Status: 0-Planned, 1-Ongoing, 2-Completed',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Training Table';

-- 11. Projects 项目表
CREATE TABLE IF NOT EXISTS t_project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT 'Project Name',
    description TEXT COMMENT 'Description',
    start_date DATE COMMENT 'Start Date',
    end_date DATE COMMENT 'End Date',
    status TINYINT DEFAULT 0 COMMENT 'Status: 0-Pending, 1-Ongoing, 2-Completed, 3-Suspended',
    manager_id BIGINT COMMENT 'Project Manager ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Project Table';

-- 12. Project Participation 项目参与表
CREATE TABLE IF NOT EXISTS t_project_participation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT 'Employee ID',
    project_id BIGINT NOT NULL COMMENT 'Project ID',
    role VARCHAR(100) COMMENT 'Role in project',
    contribution_score DECIMAL(5,2) DEFAULT 0 COMMENT 'Contribution score',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (employee_id) REFERENCES t_employee(id),
    FOREIGN KEY (project_id) REFERENCES t_project(id)
) COMMENT 'Project Participation Table';

-- 初始化数据

-- Roles 角色
INSERT INTO sys_role (role_name, role_key, description) VALUES ('Administrator', 'admin', 'System Administrator');
INSERT INTO sys_role (role_name, role_key, description) VALUES ('HR', 'hr', 'Human Resources');
INSERT INTO sys_role (role_name, role_key, description) VALUES ('Employee', 'employee', 'Regular Employee');

-- Users 用户
-- admin / 123456
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES ('admin', '$2a$10$7JB720yubVSZv5W8vNGkarOu7zy8.W8.W8.W8.W8.W8.W8.W8', 'System Admin', '13800000000', 1);
-- hr / 123456
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES ('hr', '$2a$10$7JB720yubVSZv5W8vNGkarOu7zy8.W8.W8.W8.W8.W8.W8.W8', 'HR Manager', '13800000001', 1);
-- employee / 123456
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES ('employee', '$2a$10$7JB720yubVSZv5W8vNGkarOu7zy8.W8.W8.W8.W8.W8.W8.W8', 'John Doe', '13800000002', 1);

-- User Roles 用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO sys_user_role (user_id, role_id) VALUES (3, 3);

INSERT INTO t_job_level (level_code, level_name, level_rank, salary_min, salary_max, status, remark)
SELECT 'P1', 'Junior Specialist', 1, 5000.00, 8000.00, 1, 'Entry-level individual contributor'
WHERE NOT EXISTS (SELECT 1 FROM t_job_level WHERE level_code = 'P1');
INSERT INTO t_job_level (level_code, level_name, level_rank, salary_min, salary_max, status, remark)
SELECT 'P2', 'Specialist', 2, 8000.00, 12000.00, 1, 'Core delivery level'
WHERE NOT EXISTS (SELECT 1 FROM t_job_level WHERE level_code = 'P2');
INSERT INTO t_job_level (level_code, level_name, level_rank, salary_min, salary_max, status, remark)
SELECT 'P3', 'Senior Specialist', 3, 12000.00, 18000.00, 1, 'Senior individual contributor'
WHERE NOT EXISTS (SELECT 1 FROM t_job_level WHERE level_code = 'P3');

INSERT INTO t_employee_change (
    employee_id, change_type, apply_mode, status, reason, effective_date,
    before_dept_id, after_dept_id, before_position, after_position,
    before_job_level_id, after_job_level_id, before_salary, after_salary,
    applicant_user_id, approver_user_id, approve_remark, approve_time,
    execute_user_id, execute_time
)
SELECT e.id, 'JOB_LEVEL_ADJUST', 'EMPLOYEE_APPLY', 'PENDING_APPROVAL', 'Seeded demo job level adjustment', DATE '2026-04-10',
       e.dept_id, e.dept_id, 'Tester', 'Senior Tester',
       before_level.id, after_level.id, 7000.00, 8200.00,
       applicant.id, NULL, NULL, NULL,
       NULL, NULL
FROM t_employee e
JOIN t_job_level before_level ON before_level.level_code = 'P2'
JOIN t_job_level after_level ON after_level.level_code = 'P3'
JOIN sys_user applicant ON applicant.username = 'employee'
WHERE e.job_number = 'IT002'
  AND NOT EXISTS (
    SELECT 1 FROM t_employee_change c
    WHERE c.employee_id = e.id AND c.change_type = 'JOB_LEVEL_ADJUST' AND c.effective_date = DATE '2026-04-10'
  );

INSERT INTO t_employee_change_log (change_id, action_type, operator_user_id, operator_name, content)
SELECT c.id, 'CREATED', 3, 'John Doe', 'Seeded demo workflow record'
FROM t_employee_change c
JOIN t_employee e ON e.id = c.employee_id
WHERE e.job_number = 'IT002'
  AND c.change_type = 'JOB_LEVEL_ADJUST'
  AND c.effective_date = DATE '2026-04-10'
  AND NOT EXISTS (
      SELECT 1 FROM t_employee_change_log l
      WHERE l.change_id = c.id AND l.action_type = 'CREATED'
  );

-- Menus 菜单
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Dashboard', '/dashboard', 'dashboard:view', 'Odometer', 1);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Department', '/department', 'dept:view', 'OfficeBuilding', 2);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Employee', '/employee', 'employee:view', 'User', 3);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Attendance', '/attendance', 'attendance:view', 'Calendar', 4);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Leave', '/leave', 'leave:view', 'Memo', 5);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Project', '/project', 'project:view', 'List', 6);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'User', '/user', 'user:view', 'UserFilled', 7);

-- Role Menus (admin has all) 管理员拥有所有菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 1);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 5);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 6);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 7);

-- Departments 部门
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (0, 'Headquarters', 1, 'CEO', '13800138000', 'ceo@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, 'HR Department', 1, 'HR Manager', '13800138001', 'hr@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, 'IT Department', 2, 'CTO', '13800138002', 'it@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, 'Sales Department', 3, 'Sales Director', '13800138003', 'sales@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, '研发部', 4, '张三', '13900139004', 'rd@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, '产品部', 5, '李四', '13900139005', 'pm@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, '测试部', 6, '王五', '13900139006', 'qa@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, '售后部', 7, '赵六', '13900139007', 'support@company.com', 1);

-- Employees 员工 - 使用统一的工号格式（大写字母+数字）
-- HR部门（HR前缀）
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (2, 'HR Manager', 2, 'HR001', 'Manager', 8000.00, '2020-01-01', NULL, 1, '110101199001011234', 'Beijing');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (2, 'HR专员1', 1, 'HR002', 'HR Specialist', 5500.00, '2021-03-15', NULL, 1, '110101199102021234', 'Beijing');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (2, 'HR专员2', 2, 'HR003', 'HR Specialist', 5500.00, '2021-06-01', NULL, 1, '110101199203031234', 'Beijing');

-- IT部门（IT前缀）
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (3, 'John Doe', 1, 'IT001', 'Developer', 7500.00, '2021-06-15', '2025-01-01', 2, '110101199505051234', 'Shanghai');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (3, 'Jane Smith', 2, 'IT002', 'Tester', 7000.00, '2021-07-01', NULL, 1, '110101199606061234', 'Shenzhen');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (3, 'Mike Johnson', 1, 'IT003', 'Developer', 8000.00, '2022-01-10', NULL, 1, '110101199707071234', 'Guangzhou');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (3, 'Sarah Wilson', 2, 'IT004', 'UI Designer', 6500.00, '2022-04-20', NULL, 1, '110101199808081234', 'Hangzhou');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (3, 'Tom Brown', 1, 'IT005', 'DevOps', 7500.00, '2022-07-15', NULL, 1, '110101199909091234', 'Chengdu');

-- 销售部门（SALE前缀）
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (4, 'Alice Brown', 2, 'SALE001', 'Salesman', 6000.00, '2022-03-10', NULL, 1, '110101199808081234', 'Guangzhou');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (4, 'Bob White', 1, 'SALE002', 'Sales Manager', 8500.00, '2021-09-05', NULL, 1, '110101199404041234', 'Shenzhen');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (4, 'Cindy Green', 2, 'SALE003', 'Sales Representative', 5500.00, '2023-01-20', NULL, 1, '110101200010101234', 'Wuhan');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, leave_date, status, id_card, address) VALUES (4, 'David Lee', 1, 'SALE004', 'Sales Representative', 5800.00, '2023-04-15', NULL, 1, '110101200111111234', 'Nanjing');

-- 研发部（RD前缀）
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (5, '张三', 1, 'RD001', 'Java开发', 9000.00, '2022-01-01', 1);
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (5, '李四', 2, 'RD002', '前端开发', 8500.00, '2022-02-01', 1);
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (5, '王五', 1, 'RD003', '后端开发', 8800.00, '2022-03-01', 1);
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (5, '赵六', 2, 'RD004', '测试开发', 8000.00, '2022-04-01', 1);
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (5, '钱七', 1, 'RD005', '架构师', 12000.00, '2021-01-01', 1);

-- 产品部（PM前缀）
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (6, '孙八', 1, 'PM001', '产品经理', 8500.00, '2022-02-01', 1);
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (6, '周九', 2, 'PM002', '产品专员', 6000.00, '2022-05-01', 1);
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (6, '吴十', 1, 'PM003', 'UI设计师', 7000.00, '2022-06-01', 1);

-- 测试部（QA前缀）
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (7, '郑十一', 2, 'QA001', '测试工程师', 7500.00, '2022-03-01', 1);
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (7, '王十二', 1, 'QA002', '自动化测试', 8000.00, '2022-07-01', 1);

-- 售后部（SUPPORT前缀）
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (8, '冯十三', 2, 'SUPPORT001', '客服专员', 5500.00, '2022-04-01', 1);
INSERT INTO t_employee (dept_id, name, gender, job_number, position, base_salary, hire_date, status) VALUES (8, '陈十四', 1, 'SUPPORT002', '技术支持', 6500.00, '2022-08-01', 1);

UPDATE t_employee
SET job_level_id = (SELECT id FROM t_job_level WHERE level_code = 'P2')
WHERE job_number IN ('HR001', 'IT002', 'SALE002') AND (job_level_id IS NULL OR job_level_id = 0);
UPDATE t_employee
SET job_level_id = (SELECT id FROM t_job_level WHERE level_code = 'P3')
WHERE job_number IN ('IT003', 'RD005') AND (job_level_id IS NULL OR job_level_id = 0);

-- Notices 通知公告
INSERT INTO t_notice (title, content, type, status, is_top, top_time, create_by) VALUES ('Welcome to the System', 'Welcome to the Enterprise Employee Management System! This system provides comprehensive HR management capabilities including employee management, attendance tracking, performance evaluation, and more.', 1, 1, 1, CURRENT_TIMESTAMP, 'admin');
INSERT INTO t_notice (title, content, type, status, is_top, top_time, create_by) VALUES ('Holiday Notice', 'The office will be closed for the upcoming holiday. Please plan your work accordingly and ensure all pending tasks are completed before the holiday.', 2, 1, 0, NULL, 'admin');
INSERT INTO t_notice (title, content, type, status, is_top, top_time, create_by) VALUES ('System Maintenance', 'System will be under maintenance this weekend. During this period, the system may be unavailable. We apologize for any inconvenience caused.', 1, 0, 0, NULL, 'admin');
INSERT INTO t_notice (title, content, type, status, is_top, top_time, create_by) VALUES ('Training Schedule', 'New employee orientation training will be held next Monday. All new employees are required to attend.', 2, 1, 0, NULL, 'hr');
INSERT INTO t_notice (title, content, type, status, is_top, top_time, create_by) VALUES ('Performance Review', 'Quarterly performance review is coming up. Please prepare your work summary and self-evaluation.', 1, 0, 0, NULL, 'hr');

-- Attendance 考勤 - 为所有员工生成近30天的考勤数据
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (1, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (2, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (3, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (4, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 2);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (5, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 8, CURRENT_TIMESTAMP), 3);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (6, CURRENT_DATE, NULL, NULL, 4);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (7, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (8, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (9, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (10, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (11, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 2);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (12, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (13, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 3);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (14, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (15, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (16, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 4);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (17, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (18, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (19, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (20, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 2);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (21, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (22, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 3);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (23, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (24, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (25, CURRENT_DATE, CURRENT_TIMESTAMP, DATEADD('HOUR', 9, CURRENT_TIMESTAMP), 4);

-- Performance 绩效 - 为所有员工生成近6个月的绩效数据
-- 2025年10月
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (1, '2025-10', 8000.00, 22, 500.00, '表现优秀，HR工作完成出色');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (2, '2025-10', 7500.00, 20, 300.00, '表现良好');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (3, '2025-10', 8500.00, 22, 600.00, '项目贡献突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (4, '2025-10', 7000.00, 21, 400.00, '稳定输出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (5, '2025-10', 8000.00, 22, 550.00, '技术能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (6, '2025-10', 6500.00, 20, 200.00, '需要提升');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (7, '2025-10', 7500.00, 22, 350.00, '工作认真负责');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (8, '2025-10', 8500.00, 22, 650.00, '销售业绩优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (9, '2025-10', 5500.00, 21, 150.00, '新员工，需要学习');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (10, '2025-10', 5800.00, 22, 250.00, '积极主动');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (11, '2025-10', 9000.00, 22, 700.00, '研发能力突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (12, '2025-10', 8500.00, 21, 450.00, '前端技术优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (13, '2025-10', 8800.00, 22, 500.00, '后端开发经验丰富');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (14, '2025-10', 8000.00, 20, 300.00, '测试工作细致');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (15, '2025-10', 12000.00, 22, 800.00, '架构设计优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (16, '2025-10', 8500.00, 22, 550.00, '产品规划能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (17, '2025-10', 6000.00, 21, 200.00, '产品运营认真');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (18, '2025-10', 7000.00, 22, 350.00, 'UI设计美观');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (19, '2025-10', 7500.00, 22, 400.00, '测试工作全面');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (20, '2025-10', 8000.00, 21, 450.00, '自动化测试能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (21, '2025-10', 5500.00, 22, 200.00, '客服态度好');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (22, '2025-10', 6500.00, 21, 250.00, '技术支持到位');

-- 2025年11月
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (1, '2025-11', 8000.00, 21, 450.00, '持续优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (2, '2025-11', 7500.00, 21, 320.00, '有进步');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (3, '2025-11', 8500.00, 22, 620.00, '项目贡献突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (4, '2025-11', 7000.00, 22, 420.00, '稳定输出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (5, '2025-11', 8000.00, 21, 520.00, '技术能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (6, '2025-11', 6500.00, 19, 180.00, '需要提升');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (7, '2025-11', 7500.00, 22, 370.00, '工作认真负责');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (8, '2025-11', 8500.00, 22, 670.00, '销售业绩优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (9, '2025-11', 5500.00, 20, 160.00, '新员工，需要学习');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (10, '2025-11', 5800.00, 22, 270.00, '积极主动');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (11, '2025-11', 9000.00, 21, 720.00, '研发能力突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (12, '2025-11', 8500.00, 22, 470.00, '前端技术优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (13, '2025-11', 8800.00, 22, 520.00, '后端开发经验丰富');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (14, '2025-11', 8000.00, 21, 320.00, '测试工作细致');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (15, '2025-11', 12000.00, 22, 820.00, '架构设计优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (16, '2025-11', 8500.00, 22, 570.00, '产品规划能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (17, '2025-11', 6000.00, 21, 220.00, '产品运营认真');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (18, '2025-11', 7000.00, 22, 370.00, 'UI设计美观');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (19, '2025-11', 7500.00, 22, 420.00, '测试工作全面');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (20, '2025-11', 8000.00, 21, 470.00, '自动化测试能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (21, '2025-11', 5500.00, 22, 220.00, '客服态度好');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (22, '2025-11', 6500.00, 21, 270.00, '技术支持到位');

-- 2025年12月
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (1, '2025-12', 8000.00, 22, 500.00, '年底冲刺表现佳');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (2, '2025-12', 7500.00, 22, 350.00, '良好');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (3, '2025-12', 8500.00, 22, 650.00, '项目贡献突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (4, '2025-12', 7000.00, 21, 400.00, '稳定输出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (5, '2025-12', 8000.00, 22, 550.00, '技术能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (6, '2025-12', 6500.00, 20, 200.00, '需要提升');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (7, '2025-12', 7500.00, 22, 350.00, '工作认真负责');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (8, '2025-12', 8500.00, 22, 700.00, '销售业绩优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (9, '2025-12', 5500.00, 21, 150.00, '新员工，需要学习');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (10, '2025-12', 5800.00, 22, 250.00, '积极主动');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (11, '2025-12', 9000.00, 22, 750.00, '研发能力突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (12, '2025-12', 8500.00, 22, 500.00, '前端技术优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (13, '2025-12', 8800.00, 22, 550.00, '后端开发经验丰富');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (14, '2025-12', 8000.00, 21, 350.00, '测试工作细致');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (15, '2025-12', 12000.00, 22, 850.00, '架构设计优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (16, '2025-12', 8500.00, 22, 600.00, '产品规划能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (17, '2025-12', 6000.00, 22, 250.00, '产品运营认真');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (18, '2025-12', 7000.00, 22, 400.00, 'UI设计美观');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (19, '2025-12', 7500.00, 22, 450.00, '测试工作全面');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (20, '2025-12', 8000.00, 22, 500.00, '自动化测试能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (21, '2025-12', 5500.00, 22, 250.00, '客服态度好');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (22, '2025-12', 6500.00, 21, 300.00, '技术支持到位');

-- 2026年1月
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (1, '2026-01', 8000.00, 22, 520.00, '开年表现好');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (2, '2026-01', 7500.00, 20, 300.00, '需要提升');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (3, '2026-01', 8500.00, 22, 630.00, '项目贡献突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (4, '2026-01', 7000.00, 21, 380.00, '稳定输出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (5, '2026-01', 8000.00, 22, 530.00, '技术能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (6, '2026-01', 6500.00, 19, 170.00, '需要提升');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (7, '2026-01', 7500.00, 22, 360.00, '工作认真负责');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (8, '2026-01', 8500.00, 22, 680.00, '销售业绩优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (9, '2026-01', 5500.00, 20, 140.00, '新员工，需要学习');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (10, '2026-01', 5800.00, 22, 260.00, '积极主动');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (11, '2026-01', 9000.00, 22, 730.00, '研发能力突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (12, '2026-01', 8500.00, 21, 480.00, '前端技术优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (13, '2026-01', 8800.00, 22, 530.00, '后端开发经验丰富');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (14, '2026-01', 8000.00, 20, 310.00, '测试工作细致');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (15, '2026-01', 12000.00, 22, 830.00, '架构设计优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (16, '2026-01', 8500.00, 22, 580.00, '产品规划能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (17, '2026-01', 6000.00, 21, 210.00, '产品运营认真');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (18, '2026-01', 7000.00, 22, 360.00, 'UI设计美观');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (19, '2026-01', 7500.00, 22, 410.00, '测试工作全面');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (20, '2026-01', 8000.00, 21, 460.00, '自动化测试能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (21, '2026-01', 5500.00, 22, 210.00, '客服态度好');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (22, '2026-01', 6500.00, 21, 260.00, '技术支持到位');

-- 2026年2月
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (1, '2026-02', 8000.00, 21, 480.00, '持续优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (2, '2026-02', 7500.00, 22, 330.00, '状态回升');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (3, '2026-02', 8500.00, 22, 600.00, '项目贡献突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (4, '2026-02', 7000.00, 22, 430.00, '稳定输出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (5, '2026-02', 8000.00, 21, 500.00, '技术能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (6, '2026-02', 6500.00, 21, 190.00, '有进步');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (7, '2026-02', 7500.00, 22, 340.00, '工作认真负责');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (8, '2026-02', 8500.00, 21, 640.00, '销售业绩优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (9, '2026-02', 5500.00, 21, 150.00, '新员工，需要学习');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (10, '2026-02', 5800.00, 22, 240.00, '积极主动');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (11, '2026-02', 9000.00, 22, 700.00, '研发能力突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (12, '2026-02', 8500.00, 22, 460.00, '前端技术优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (13, '2026-02', 8800.00, 21, 500.00, '后端开发经验丰富');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (14, '2026-02', 8000.00, 22, 330.00, '测试工作细致');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (15, '2026-02', 12000.00, 22, 800.00, '架构设计优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (16, '2026-02', 8500.00, 21, 540.00, '产品规划能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (17, '2026-02', 6000.00, 22, 230.00, '产品运营认真');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (18, '2026-02', 7000.00, 21, 340.00, 'UI设计美观');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (19, '2026-02', 7500.00, 22, 390.00, '测试工作全面');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (20, '2026-02', 8000.00, 22, 440.00, '自动化测试能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (21, '2026-02', 5500.00, 22, 230.00, '客服态度好');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (22, '2026-02', 6500.00, 21, 280.00, '技术支持到位');

-- 2026年3月
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (1, '2026-03', 8000.00, 22, 550.00, '本月最佳');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (2, '2026-03', 7500.00, 22, 380.00, '表现出色');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (3, '2026-03', 8500.00, 22, 680.00, '项目贡献突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (4, '2026-03', 7000.00, 22, 450.00, '稳定输出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (5, '2026-03', 8000.00, 22, 580.00, '技术能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (6, '2026-03', 6500.00, 21, 220.00, '持续进步');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (7, '2026-03', 7500.00, 22, 390.00, '工作认真负责');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (8, '2026-03', 8500.00, 22, 730.00, '销售业绩优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (9, '2026-03', 5500.00, 22, 180.00, '新员工，需要学习');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (10, '2026-03', 5800.00, 22, 290.00, '积极主动');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (11, '2026-03', 9000.00, 22, 780.00, '研发能力突出');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (12, '2026-03', 8500.00, 22, 500.00, '前端技术优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (13, '2026-03', 8800.00, 22, 550.00, '后端开发经验丰富');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (14, '2026-03', 8000.00, 21, 350.00, '测试工作细致');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (15, '2026-03', 12000.00, 22, 850.00, '架构设计优秀');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (16, '2026-03', 8500.00, 22, 600.00, '产品规划能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (17, '2026-03', 6000.00, 22, 250.00, '产品运营认真');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (18, '2026-03', 7000.00, 22, 400.00, 'UI设计美观');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (19, '2026-03', 7500.00, 22, 450.00, '测试工作全面');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (20, '2026-03', 8000.00, 22, 500.00, '自动化测试能力强');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (21, '2026-03', 5500.00, 22, 250.00, '客服态度好');
INSERT INTO t_performance (employee_id, perf_month, base_salary, attendance_days, project_bonus, comments) VALUES (22, '2026-03', 6500.00, 21, 300.00, '技术支持到位');

-- Training 培训
INSERT INTO t_training (title, content, start_time, end_time, location, trainer, status) VALUES ('New Employee Orientation', 'Introduction to company culture and policies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Meeting Room A', 'HR Manager', 2);
INSERT INTO t_training (title, content, start_time, end_time, location, trainer, status) VALUES ('Java Advanced Training', 'Deep dive into Java concurrency', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Online', 'Senior Architect', 1);
INSERT INTO t_training (title, content, start_time, end_time, location, trainer, status) VALUES ('团队协作培训', '提升团队协作能力和沟通技巧', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Conference Room B', '培训师', 0);
INSERT INTO t_training (title, content, start_time, end_time, location, trainer, status) VALUES ('项目管理培训', '学习项目管理最佳实践', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Online', '项目总监', 1);

-- Projects 项目
INSERT INTO t_project (name, description, start_date, end_date, status, manager_id) VALUES ('Employee Management System', 'Developing a new HR system', '2023-01-01', '2023-06-30', 1, 1);
INSERT INTO t_project (name, description, start_date, end_date, status, manager_id) VALUES ('Website Redesign', 'Redesigning the corporate website', '2023-02-15', '2023-05-20', 0, 1);
INSERT INTO t_project (name, description, start_date, end_date, status, manager_id) VALUES ('客户关系管理系统', '开发企业级CRM系统', '2025-10-01', '2026-03-31', 1, 11);
INSERT INTO t_project (name, description, start_date, end_date, status, manager_id) VALUES ('内部培训平台', '搭建企业内部培训平台', '2026-01-01', '2026-06-30', 0, 16);
INSERT INTO t_project (name, description, start_date, end_date, status, manager_id) VALUES ('数据分析平台', '建设企业数据分析和可视化平台', '2025-08-01', '2026-02-28', 2, 11);
-- 添加一个已暂停的项目
INSERT INTO t_project (name, description, start_date, end_date, status, manager_id) VALUES ('移动应用开发', '开发企业移动办公应用', '2025-09-01', '2026-04-30', 3, 11);

-- Project participation 项目参与
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (1, 1, '后端开发', 89.50);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (2, 1, '前端开发', 86.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (3, 2, 'UI设计', 78.50);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (11, 3, '项目经理', 92.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (12, 3, '前端开发', 88.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (13, 3, '后端开发', 90.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (14, 3, '测试工程师', 85.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (19, 4, '测试工程师', 80.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (16, 4, '产品经理', 87.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (18, 4, 'UI设计', 82.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (15, 5, '架构师', 95.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (11, 5, '项目经理', 89.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (13, 5, '后端开发', 91.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (20, 5, '测试工程师', 84.00);
-- 为已暂停的项目添加参与数据
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (11, 6, '项目经理', 88.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (12, 6, '前端开发', 85.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (13, 6, '后端开发', 87.00);
INSERT INTO t_project_participation (employee_id, project_id, role, contribution_score) VALUES (14, 6, '测试工程师', 83.00);

-- Leave 请假
INSERT INTO t_leave (employee_id, type, start_time, end_time, reason, status, approver) VALUES (2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Fever', 0, NULL);
INSERT INTO t_leave (employee_id, type, start_time, end_time, reason, status, approver) VALUES (3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Family travel', 1, 'admin');
INSERT INTO t_leave (employee_id, type, start_time, end_time, reason, status, approver) VALUES (11, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '年假休息', 1, 'admin');
INSERT INTO t_leave (employee_id, type, start_time, end_time, reason, status, approver) VALUES (12, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '感冒发烧', 0, NULL);
INSERT INTO t_leave (employee_id, type, start_time, end_time, reason, status, approver) VALUES (16, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '参加培训', 1, 'admin');
