# 企业员工管理系统 - 前端

基于 Vue 3 + TypeScript + Element Plus + Vite 构建的现代化企业人事管理系统前端。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **语言**: TypeScript
- **UI库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **构建工具**: Vite
- **HTTP客户端**: Axios
- **图表库**: ECharts
- **样式**: CSS3 + Element Plus

## 项目结构

```
frontend/
├── public/                 # 静态资源
│   └── favicon.ico        # 网站图标
├── src/
│   ├── api/               # API 接口
│   │   └── http.ts        # HTTP 客户端配置
│   ├── composables/       # 组合函数
│   │   └── index.ts       # 可复用逻辑
│   ├── router/            # 路由配置
│   │   └── index.ts       # 路由定义
│   ├── store/             # 状态管理
│   │   └── index.ts       # Pinia store
│   ├── types/             # TypeScript 类型定义
│   │   └── index.ts       # 全局类型
│   ├── utils/             # 工具函数
│   │   └── index.ts       # 通用工具
│   ├── views/             # 页面组件
│   │   ├── Login.vue      # 登录页
│   │   ├── Dashboard.vue  # 主布局
│   │   └── ...            # 其他页面
│   ├── App.vue            # 根组件
│   ├── main.ts            # 应用入口
│   └── style.css          # 全局样式
├── .env                   # 环境变量
├── index.html             # HTML 模板
├── package.json           # 项目配置
├── tsconfig.json          # TypeScript 配置
└── vite.config.ts         # Vite 配置
```

## 功能模块

### 1. 用户认证
- 登录/登出
- JWT Token 管理
- 路由守卫

### 2. 仪表盘
- 统计数据展示
- ECharts 图表可视化
- 实时数据更新

### 3. 组织管理
- 部门树形结构管理
- 部门信息 CRUD
- 层级关系维护

### 4. 员工管理
- 员工信息管理
- 员工状态跟踪
- 批量操作支持

### 5. 考勤管理
- 签到/签退功能
- 考勤记录查询
- 考勤统计分析

### 6. 请假管理
- 请假申请流程
- 审批工作流
- 请假记录管理

### 7. 通知公告
- 公告发布管理
- 公告分类
- 阅读状态跟踪

### 8. 绩效管理
- 月度绩效评估
- 绩效数据统计
- 历史绩效查询

### 9. 培训管理
- 培训计划管理
- 培训记录跟踪
- 培训效果评估

### 10. 项目管理
- 项目信息管理
- 项目进度跟踪
- 项目状态统计

### 11. 用户权限
- 角色管理
- 权限分配
- 用户角色配置

## 开发指南

### 环境要求

- Node.js 18+
- npm 8+

### 安装依赖

```bash
npm install
```

### 开发环境

```bash
npm run dev
```

访问 http://localhost:5173

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 环境变量

创建 `.env` 文件配置环境变量：

```env
VITE_APP_TITLE=企业员工管理系统
VITE_APP_API_BASE_URL=http://localhost:8080/api
VITE_APP_ENV=development
```

## API 接口

前端通过 `/api` 代理访问后端服务：

- 开发环境: 代理到 `http://localhost:8080`
- 生产环境: 根据部署配置调整

## 代码规范

### 命名规范

- 组件: PascalCase (如 `UserList.vue`)
- 文件: kebab-case (如 `user-list.ts`)
- 变量: camelCase (如 `userName`)
- 常量: UPPER_SNAKE_CASE (如 `API_BASE_URL`)

### 代码组织

- 使用 Composition API
- 组合函数放在 `composables/` 目录
- 工具函数放在 `utils/` 目录
- 类型定义放在 `types/` 目录
- API 调用放在 `api/` 目录

## 浏览器支持

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## 部署说明

1. 构建生产版本: `npm run build`
2. 将 `dist/` 目录部署到 Web 服务器
3. 配置反向代理指向后端 API
4. 确保 HTTPS 证书正确配置