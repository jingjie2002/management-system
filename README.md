# 基于SpringBoot的企业员工精细化人事管理系统

## 1. 项目定位
本项目对应毕设题目：**基于SpringBoot的企业员工精细化人事管理系统设计与实现**。  
技术路线：**Spring Boot + MyBatis-Plus + Vue3 + Element Plus + ECharts + MySQL + navicat +ruoyi**。使用IDEA自动配置

## 2. 已实现模块
- 通知公告管理（增删改查）
- 部门组织管理（树形展示、CRUD）
- 员工信息管理（检索、CRUD、Excel导出）
- 考勤管理（签到/签退、状态统计）
- 请假审批（申请、审批、驳回）
- 绩效管理（月度绩效CRUD）
- 培训管理（培训计划CRUD）
- 项目记录管理（项目CRUD）
- 用户与角色管理（用户CRUD、角色CRUD、分配角色）
- 仪表盘可视化（ECharts图表 + 排行）
- 个人信息看板（员工ID、电话、入离职、绩效、考勤、请假、项目贡献分）
- OpenAPI 文档（Swagger UI）

## 3. 毕设算法说明
系统内已集成2个可写入论文的算法，并在代码中有注释：
- **TOP-K最小堆算法**：用于“部门出勤率Top5”排行。
- **加权评分算法**：用于“员工综合贡献分”计算。

算法代码位置：
- `src/main/java/com/ch/managementsystem/utils/AlgorithmUtils.java`
- 调用位置：`src/main/java/com/ch/managementsystem/service/impl/StatisticsServiceImpl.java`

## 4. 本地运行（推荐）
### 4.1 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.x（你可用 Navicat 管理）

### 4.2 一键启动（H2模式，快速演示）
1. 双击 `start_all.bat`
2. 脚本会自动执行环境自检、自动尝试安装缺失依赖、自动释放 8080/5173 端口
3. 启动后会自动健康检查并自动尝试打开浏览器
3. 浏览器访问：`http://localhost:5173`

### 4.3 MySQL模式（答辩推荐）
1. 使用 Navicat 新建数据库：`management_system`
2. 导入脚本：`src/main/resources/schema.sql`
3. 修改 `src/main/resources/application-mysql.properties` 中账号密码
4. 双击 `start_all_mysql.bat`
5. 脚本会自动执行环境自检、自动尝试安装缺失依赖、自动释放 8080/5173 端口
6. 启动后会自动健康检查并自动尝试打开浏览器
6. 浏览器访问：`http://localhost:5173`

### 4.4 可单独执行的自检/预检脚本
- `check_env.bat --auto-fix --no-pause`：环境检测并自动修复
- `preflight.bat h2`：释放端口并做H2模式预检
- `preflight.bat mysql`：释放端口并检测3306连通
- `health_check.bat --open`：启动后端口健康检查并自动打开浏览器
- `doctor.bat`：一键诊断 localhost 拒绝连接问题
- `install_env.ps1`：自动安装 JDK17 与 Node.js LTS

默认账号：
- `admin / 123456`
- `hr / 123456`
- `employee / 123456`

API 文档：
- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

## 5. 配置文件说明
- `application.properties`：公共配置 + 默认启用 dev
- `application-dev.properties`：H2内存库（开箱即用）
- `application-mysql.properties`：MySQL配置（用于Navicat/MySQL）

## 6. 项目目录（清晰版）
```
management-system
├─ src/main/java/com/ch/managementsystem
│  ├─ common          # 统一返回与异常
│  ├─ config          # 安全、跨域、MyBatis配置
│  ├─ controller      # 接口层
│  ├─ entity          # 实体与DTO/VO
│  ├─ mapper          # 持久层
│  ├─ service         # 业务层
│  └─ utils           # 工具与算法
├─ src/main/resources
│  ├─ application*.properties
│  └─ schema.sql
├─ frontend/src
│  ├─ api
│  ├─ router
│  └─ views
├─ check_env.bat
├─ preflight.bat
├─ health_check.bat
├─ doctor.bat
├─ install_env.ps1
├─ start_all.bat
├─ start_all_mysql.bat
└─ pom.xml
```

## 7. 对标参考项目说明
已参考你提供的 `C:\Users\a6668\Desktop\毕业设计\oa_system-master` 的模块化思路与后台风格，结合当前前后端分离架构完成适配：  
保留了“清晰菜单、模块化目录、可扩展业务流”的优点，同时使用 Vue3 + REST 方式实现现代化落地。

## 8. 测试与文档
- 后端接口测试：`./mvnw test`
- 前端构建验证：`cd frontend && npm run build`
- 需求说明：`docs/REQUIREMENTS.md`
- 数据库连接：`docs/DB_CONNECTION.md`
- 接口测试指南：`docs/API_TEST_GUIDE.md`
- APIfox 测试教程：`docs/apifox/README.md`
- APIfox 场景步骤表：`docs/apifox/SCENARIO_TEST_STEPS.md`
- APIfox 报告模板：`docs/apifox/SCENARIO_TEST_REPORT_TEMPLATE.md`
- 功能测试用例：`docs/FUNCTIONAL_TEST_CASES.md`
- 使用教程手册：`docs/USER_MANUAL.md`
