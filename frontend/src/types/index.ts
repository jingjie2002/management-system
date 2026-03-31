// // 全局类型定义
// declare module '*.vue' {
//   import type { DefineComponent } from 'vue'
//   const component: DefineComponent<{}, {}, any>
//   export default component
// }

// // API响应类型
// export interface ApiResponse<T = any> {
//   code: number
//   message: string
//   data: T
// }

// // 用户信息类型
// export interface User {
//   id: number
//   username: string
//   role: string
//   department?: string
// }

// // 部门类型
// export interface Department {
//   id: number
//   deptName: string
//   parentId?: number
//   leader?: string
//   phone?: string
//   email?: string
//   orderNum: number
//   children?: Department[]
// }

// // 员工类型
// export interface Employee {
//   id: number
//   name: string
//   gender: string
//   birthDate: string
//   phone: string
//   email: string
//   departmentId: number
//   position: string
//   hireDate: string
//   status: string
// }

// // 考勤类型
// export interface Attendance {
//   id: number
//   employeeId: number
//   checkInTime?: string
//   checkOutTime?: string
//   date: string
//   status: string
// }

// // 请假类型
// export interface Leave {
//   id: number
//   employeeId: number
//   type: string
//   startDate: string
//   endDate: string
//   reason: string
//   status: string
//   approverId?: number
// }

// // 通知类型
// export interface Notice {
//   id: number
//   title: string
//   content: string
//   type: string
//   publishTime: string
//   publisher: string
// }

// // 绩效类型
// export interface Performance {
//   id: number
//   employeeId: number
//   year: number
//   month: number
//   score: number
//   comment?: string
// }

// // 培训类型
// export interface Training {
//   id: number
//   title: string
//   description?: string
//   startDate: string
//   endDate: string
//   trainer: string
//   maxParticipants: number
// }

// // 项目类型
// export interface Project {
//   id: number
//   name: string
//   description?: string
//   startDate: string
//   endDate?: string
//   status: string
//   managerId: number
// }

// // 统计数据类型
// export interface DashboardStats {
//   totalEmployees: number
//   activeEmployees: number
//   totalDepartments: number
//   ongoingProjects: number
//   deptEmployeeStats: Array<{ name: string; value: number }>
//   attendanceStats: Array<{ name: string; value: number }>
//   projectStatusStats: Array<{ name: string; value: number }>
//   topAttendanceDepartments: Array<{ name: string; value: number }>
//   employeeContributionTop: Array<{ name: string; value: number }>
// }


// 全局类型定义
declare module '*.vue' {  // 声明所有 .vue 文件的类型
  import type { DefineComponent } from 'vue'  // 从 Vue 导入 DefineComponent 类型
  const component: DefineComponent<{}, {}, any>  // 定义一个 Vue 组件的类型
  export default component  // 默认导出该组件
}

// API响应类型
export interface ApiResponse<T = any> {  // 定义 API 响应类型，T 是泛型，默认值为 any
  code: number  // 响应码，表示请求的状态，通常是 200 成功，其他为错误
  message: string  // 响应消息，通常是错误信息或者成功信息
  data: T  // 响应数据，类型为泛型 T，可以是任何类型
}

// 用户信息类型
export interface User {  // 定义用户类型
  id: number  // 用户 ID
  username: string  // 用户名
  role: string  // 用户角色（例如 admin, user 等）
  department?: string  // 用户所属部门，可选
}

// 部门类型
export interface Department {  // 定义部门类型
  id: number  // 部门 ID
  deptName: string  // 部门名称
  parentId?: number  // 上级部门 ID，可选
  leader?: string  // 部门负责人，可选
  phone?: string  // 部门电话，可选
  email?: string  // 部门邮箱，可选
  orderNum: number  // 排序编号
  children?: Department[]  // 子部门，可选
}

// 员工类型
export interface Employee {  // 定义员工类型
  id: number  // 员工 ID
  name: string  // 员工姓名
  gender: string  // 性别
  birthDate: string  // 出生日期
  phone: string  // 电话
  email: string  // 邮箱
  departmentId: number  // 部门 ID
  position: string  // 职位
  hireDate: string  // 入职日期
  status: string  // 状态（例如在职、离职等）
}

// 考勤类型
export interface Attendance {  // 定义考勤类型
  id: number  // 考勤记录 ID
  employeeId: number  // 员工 ID
  checkInTime?: string  // 打卡时间，可选
  checkOutTime?: string  // 下班时间，可选
  date: string  // 考勤日期
  status: string  // 考勤状态（例如正常、迟到、早退等）
}

// 请假类型
export interface Leave {  // 定义请假类型
  id: number  // 请假记录 ID
  employeeId: number  // 员工 ID
  type: string  // 请假类型（如病假、事假等）
  startDate: string  // 请假开始日期
  endDate: string  // 请假结束日期
  reason: string  // 请假原因
  status: string  // 请假状态（如待审批、已批准等）
  approverId?: number  // 审批人 ID，可选
}

// 通知类型
export interface Notice {  // 定义通知类型
  id: number  // 通知 ID
  title: string  // 通知标题
  content: string  // 通知内容
  type: string  // 通知类型（如公告、通知等）
  publishTime: string  // 发布时间
  publisher: string  // 发布人
}

// 绩效类型
export interface Performance {  // 定义绩效类型
  id: number  // 绩效记录 ID
  employeeId: number  // 员工 ID
  year: number  // 绩效年份
  month: number  // 绩效月份
  score: number  // 绩效评分
  comment?: string  // 绩效评语，可选
}

// 培训类型
export interface Training {  // 定义培训类型
  id: number  // 培训 ID
  title: string  // 培训标题
  description?: string  // 培训描述，可选
  startDate: string  // 培训开始日期
  endDate: string  // 培训结束日期
  trainer: string  // 培训讲师
  maxParticipants: number  // 最大参与人数
}

// 项目类型
export interface Project {  // 定义项目类型
  id: number  // 项目 ID
  name: string  // 项目名称
  description?: string  // 项目描述，可选
  startDate: string  // 项目开始日期
  endDate?: string  // 项目结束日期，可选
  status: string  // 项目状态（如进行中、已完成等）
  managerId: number  // 项目经理 ID
}

// 统计数据类型
export interface DashboardStats {  // 定义仪表盘统计数据类型
  totalEmployees: number  // 总员工数
  activeEmployees: number  // 在职员工数
  totalDepartments: number  // 部门总数
  ongoingProjects: number  // 进行中的项目数
  deptEmployeeStats: Array<{ name: string; value: number }>  // 各部门员工统计
  attendanceStats: Array<{ name: string; value: number }>  // 考勤状态统计
  projectStatusStats: Array<{ name: string; value: number }>  // 项目状态统计
  topAttendanceDepartments: Array<{ name: string; value: number }>  // 出勤率Top5部门
  employeeContributionTop: Array<{ name: string; value: number }>  // 员工综合贡献Top5
}