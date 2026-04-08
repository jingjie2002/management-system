# 企业员工管理系统 - 完整启动指南

## 🎯 快速启动（交付版）

## ✅ 唯一启动入口（推荐）
项目仅保留一个启动脚本：`start_all.bat`

### 一键启动（默认 H2，可直接演示）
```bash
start_all.bat
```
脚本会自动完成以下动作：
1. 检查 Java/Node 环境
2. 启动后端（8080）
3. 等待后端健康检查
4. 启动前端（5173）
5. 自动打开浏览器

访问地址：http://localhost:5173

### MySQL 启动（可选）

#### 1. 启动MySQL服务
```powershell
# 方法1：命令行启动
Start-Process -FilePath "cmd.exe" -ArgumentList "/c cd /d 'C:\Program Files\MySQL\MySQL Server 8.4\bin' && mysqld --defaults-file='C:\Program Files\MySQL\MySQL Server 8.4\my.ini' --console" -Verb RunAs

# 方法2：如果已注册为服务
net start MySQL84
```

#### 2. 使用Navicat连接数据库
打开 `D:\Navicat for MySQL\navicat.exe`

**连接配置：**
- 连接名：企业员工管理系统
- 主机：localhost
- 端口：3306
- 用户名：root
- 密码：(留空)
- 数据库：management_system

#### 3. 导入数据库结构
在Navicat中：
1. 右键点击 `management_system` 数据库
2. 选择 "运行SQL文件"
3. 选择 `src\main\resources\schema.sql`
4. 执行导入

#### 4. 启动系统
```bash
# 统一仍使用唯一脚本入口
start_all.bat
```
如需指定连接，启动前可设置环境变量：`DB_URL`、`DB_USERNAME`、`DB_PASSWORD`。

## 📊 系统架构

```
前端 (Vue 3 + Element Plus)
    ↓
后端 (Spring Boot + MyBatis-Plus)
    ↓
数据库 (MySQL 8.4 / H2)
```

## 🔧 技术栈详情

| 组件 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 后端运行环境 |
| Spring Boot | 3.5.11 | 后端框架 |
| MySQL | 8.4.8 | 数据库 |
| Vue 3 | 3.4.21 | 前端框架 |
| Element Plus | 2.8.6 | UI组件库 |
| Vite | 5.2.0 | 构建工具 |

## 🚀 功能模块

## 📦 部署说明
- **本地开发** 使用上面的快速启动脚本即可。
- **生产打包**
  1. 前端：
     ```bash
     cd frontend
     npm run build
     ```
     产物在 `frontend/dist`，可部署到任意静态服务器（Nginx、Apache、Vercel 等）。
  2. 后端：
     ```bash
     ./mvnw clean package -DskipTests
     ```
     生成 `target/management-system-0.0.1-SNAPSHOT.jar`，通过 `java -jar` 启动，或部署到 Tomcat/Jetty。
  3. 数据库：生产环境建议使用 MySQL，将 `schema.sql` 导入至新建数据库，并在 `application.properties` 中切换到 `mysql` profile，配置正确的 URL/用户名/密码。
  4. Nginx 示例配置：
     ```nginx
     server {
       listen 80;
       server_name example.com;
       location / {
         root /var/www/management-system/frontend/dist;
         try_files $uri $uri/ /index.html;
       }
       location /api/ {
         proxy_pass http://localhost:8080/;
       }
     }
     ```


- ✅ 用户认证与权限管理
- ✅ 部门组织架构管理
- ✅ 员工信息管理
- ✅ 考勤打卡系统
- ✅ 请假审批流程
- ✅ 绩效考核管理
- ✅ 培训计划管理
- ✅ 项目进度跟踪
- ✅ 通知公告系统
- ✅ 数据可视化仪表盘

## 📈 算法特色

系统内置两个可论文算法：
1. **TOP-K最小堆算法** - 部门出勤率Top5排名
2. **加权评分算法** - 员工综合贡献分计算

## 🎨 前端特色

- 现代化响应式设计
- ECharts数据可视化
- 实时数据更新
- 友好的用户界面

## 🔍 数据库设计

包含10+个数据表：
- 用户表、角色表、权限表
- 部门表、员工表
- 考勤表、请假表
- 绩效表、培训表
- 项目表、通知表

## 🌐 访问地址

- 前端界面：http://localhost:5173
- 后端API：http://localhost:8080
- H2控制台：http://localhost:8080/h2-console

## 👤 默认账号

- admin / 123456
- hr / 123456
- employee / 123456

系统启动时会自动修复以上三个账号密码，支持登录-退出-账号切换。

## 📝 开发说明

### 前端开发（Vue项目目录为 frontend）
```bash
cd frontend
npm install
npm run dev
```

### 后端开发（Spring Boot）
```bash
./mvnw spring-boot:run
```

### IDEA运行
1. 导入根目录 `pom.xml` 为 Maven 项目
2. 使用 JDK 17
3. 运行 `ManagementSystemApplication`
4. 前端在 `frontend` 目录执行 `npm run dev`

### VSCode运行
1. 打开根目录
2. 安装 Java Extension Pack
3. 在 `frontend` 目录执行 `npm run dev`
4. 后端执行 `./mvnw spring-boot:run`

### 数据库管理
使用Navicat for MySQL连接：
- 主机：localhost:3306
- 用户：root
- 数据库：management_system

## 🛠️ 故障排除

### MySQL连接失败
1. 检查MySQL服务是否运行：`netstat -ano | findstr :3306`
2. 重新启动MySQL服务
3. 检查防火墙设置

### 图像不起作用
- 前端静态资源放在 `frontend/public` 或 `frontend/src/assets`。
- 在组件中引用示例：
  ```vue
  <template>
    <img src="@/assets/logo.png" alt="logo" />
  </template>
  ```
  或者使用 `import logo from '@/assets/logo.png'`。
- Vite 在开发时会热更新，若图片修改后未显示请清理浏览器缓存（Ctrl+Shift+R）或重启开发服务器。

### 清理本地缓存
1. 运行根目录下的 `clean_cache.bat`：
   ```bat
   clean_cache.bat
   ```
   它会删除 `target`、`node_modules`、`frontend/dist` 并清空 npm 缓存。
2. 手动清理浏览器缓存和IDE缓存（File → Invalidate Caches）。

### 前端启动失败
1. 检查Node.js版本：`node --version`
2. 重新安装依赖：`npm install`
3. 检查端口5173是否被占用

### 后端启动失败
1. 检查Java版本：`java -version`
2. 检查端口8080是否被占用
3. 查看日志输出

## 📞 技术支持

如遇到问题，请：
1. 查看控制台错误信息
2. 检查环境配置
3. 参考本文档的故障排除部分
4. 使用H2数据库作为备用方案
