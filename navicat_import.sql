-- Database Initialization Script for Navicat
-- 1. Copy all content from this file
-- 2. Open Navicat -> Connect to Localhost
-- 3. Click 'New Query' -> Paste -> Run

CREATE DATABASE IF NOT EXISTS management_system;
USE management_system;

-- 1. System Users
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

-- 2. Roles
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role Name',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role Key (e.g., admin)',
    description VARCHAR(255) COMMENT 'Description',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Role Table';

-- 3. User-Role Relation
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) COMMENT 'User-Role Relation Table';

-- 3.1 Menu
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

-- 3.2 Role-Menu Relation
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
) COMMENT 'Role-Menu Relation Table';

-- 4. Departments
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

-- 5. Employees
CREATE TABLE IF NOT EXISTS t_employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT 'Associated System User ID (if any)',
    dept_id BIGINT COMMENT 'Department ID',
    name VARCHAR(50) NOT NULL COMMENT 'Employee Name',
    gender TINYINT COMMENT 'Gender: 1-Male, 2-Female',
    job_number VARCHAR(20) NOT NULL UNIQUE COMMENT 'Job Number',
    position VARCHAR(50) COMMENT 'Position/Job Title',
    base_salary DECIMAL(10,2) DEFAULT 5000.00 COMMENT 'Base Salary',
    hire_date DATE COMMENT 'Hire Date',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-Active, 2-Resigned, 3-Leave',
    id_card VARCHAR(20) COMMENT 'ID Card Number',
    address VARCHAR(255) COMMENT 'Address',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Employee Table';

-- 6. Notifications
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

-- 7. Attendance
CREATE TABLE IF NOT EXISTS t_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    check_in_time DATETIME COMMENT 'Check-in Time',
    check_out_time DATETIME COMMENT 'Check-out Time',
    date DATE NOT NULL COMMENT 'Attendance Date',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-Normal, 2-Late, 3-Early Leave, 4-Absent',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'Attendance Table';

-- 8. Leave Applications
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

-- 9. Performance
CREATE TABLE IF NOT EXISTS t_performance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    perf_month VARCHAR(20) NOT NULL COMMENT 'Month (YYYY-MM)',
    score DECIMAL(5,2) COMMENT 'Score',
    level VARCHAR(10) COMMENT 'Level (A/B/C/D)',
    comments TEXT COMMENT 'Comments',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'Performance Table';

-- 10. Training
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

-- 11. Projects
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

-- Initial Data

-- Roles
INSERT INTO sys_role (role_name, role_key, description) VALUES ('Administrator', 'admin', 'System Administrator');
INSERT INTO sys_role (role_name, role_key, description) VALUES ('HR', 'hr', 'Human Resources');
INSERT INTO sys_role (role_name, role_key, description) VALUES ('Employee', 'employee', 'Regular Employee');

-- Users
-- admin / 123456
INSERT INTO sys_user (username, password, real_name, status) VALUES ('admin', '$2a$10$7JB720yubVSZv5W8vNGkarOu7zy8.W8.W8.W8.W8.W8.W8.W8', 'System Admin', 1);
-- hr / 123456
INSERT INTO sys_user (username, password, real_name, status) VALUES ('hr', '$2a$10$7JB720yubVSZv5W8vNGkarOu7zy8.W8.W8.W8.W8.W8.W8.W8', 'HR Manager', 1);
-- employee / 123456
INSERT INTO sys_user (username, password, real_name, status) VALUES ('employee', '$2a$10$7JB720yubVSZv5W8vNGkarOu7zy8.W8.W8.W8.W8.W8.W8.W8', 'John Doe', 1);

-- User Roles
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO sys_user_role (user_id, role_id) VALUES (3, 3);

-- Menus
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Dashboard', '/dashboard', 'dashboard:view', 'Odometer', 1);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Department', '/department', 'dept:view', 'OfficeBuilding', 2);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Employee', '/employee', 'employee:view', 'User', 3);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Attendance', '/attendance', 'attendance:view', 'Calendar', 4);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Leave', '/leave', 'leave:view', 'Memo', 5);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'Project', '/project', 'project:view', 'List', 6);
INSERT INTO sys_menu (parent_id, menu_name, path, perms, icon, order_num) VALUES (0, 'User', '/user', 'user:view', 'UserFilled', 7);

-- Role Menus (admin has all)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 1);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 5);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 6);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 7);

-- Departments
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (0, 'Headquarters', 1, 'CEO', '13800138000', 'ceo@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, 'HR Department', 1, 'HR Manager', '13800138001', 'hr@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, 'IT Department', 2, 'CTO', '13800138002', 'it@company.com', 1);
INSERT INTO t_department (parent_id, dept_name, order_num, leader, phone, email, status) VALUES (1, 'Sales Department', 3, 'Sales Director', '13800138003', 'sales@company.com', 1);

-- Employees
INSERT INTO t_employee (dept_id, name, gender, job_number, position, hire_date, status, id_card, address) VALUES (2, 'HR Manager', 2, 'HR001', 'Manager', '2020-01-01', 1, '110101199001011234', 'Beijing');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, hire_date, status, id_card, address) VALUES (3, 'John Doe', 1, 'IT001', 'Developer', '2021-06-15', 1, '110101199505051234', 'Shanghai');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, hire_date, status, id_card, address) VALUES (3, 'Jane Smith', 2, 'IT002', 'Tester', '2021-07-01', 1, '110101199606061234', 'Shenzhen');
INSERT INTO t_employee (dept_id, name, gender, job_number, position, hire_date, status, id_card, address) VALUES (4, 'Alice Brown', 2, 'SALE001', 'Salesman', '2022-03-10', 1, '110101199808081234', 'Guangzhou');

-- Notices
INSERT INTO t_notice (title, content, type, status, is_top, top_time, create_by) VALUES ('Welcome to the System', 'Welcome to the Enterprise Employee Management System!', 1, 1, 1, CURRENT_TIMESTAMP, 'admin');
INSERT INTO t_notice (title, content, type, status, is_top, top_time, create_by) VALUES ('Holiday Notice', 'The office will be closed for the upcoming holiday.', 2, 1, 0, NULL, 'admin');
INSERT INTO t_notice (title, content, type, status, is_top, top_time, create_by) VALUES ('System Maintenance', 'System will be under maintenance this weekend.', 1, 0, 0, NULL, 'admin');

-- Attendance
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (2, CURRENT_DATE, CURRENT_TIMESTAMP, NULL, 1);
INSERT INTO t_attendance (employee_id, date, check_in_time, check_out_time, status) VALUES (3, CURRENT_DATE, CURRENT_TIMESTAMP, NULL, 1);

-- Performance
INSERT INTO t_performance (employee_id, perf_month, score, level, comments) VALUES (2, '2023-01', 95.5, 'A', 'Excellent work');
INSERT INTO t_performance (employee_id, perf_month, score, level, comments) VALUES (3, '2023-01', 88.0, 'B', 'Good job, but needs improvement');

-- Training
INSERT INTO t_training (title, content, start_time, end_time, location, trainer, status) VALUES ('New Employee Orientation', 'Introduction to company culture and policies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Meeting Room A', 'HR Manager', 2);
INSERT INTO t_training (title, content, start_time, end_time, location, trainer, status) VALUES ('Java Advanced Training', 'Deep dive into Java concurrency', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Online', 'Senior Architect', 1);

-- Projects
INSERT INTO t_project (name, description, start_date, end_date, status, manager_id) VALUES ('Employee Management System', 'Developing a new HR system', '2023-01-01', '2023-06-30', 1, 1);
INSERT INTO t_project (name, description, start_date, end_date, status, manager_id) VALUES ('Website Redesign', 'Redesigning the corporate website', '2023-02-15', '2023-05-20', 0, 1);

-- Leave
INSERT INTO t_leave (employee_id, type, start_time, end_time, reason, status, approver) VALUES (2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Fever', 0, NULL);
INSERT INTO t_leave (employee_id, type, start_time, end_time, reason, status, approver) VALUES (3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Family travel', 1, 'admin');
