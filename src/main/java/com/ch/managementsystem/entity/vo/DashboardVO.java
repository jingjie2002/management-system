// package com.ch.managementsystem.entity.vo;

// import lombok.Data;
// import java.io.Serializable;
// import java.util.List;
// import java.util.Map;

// @Data
// public class DashboardVO implements Serializable {
//     private static final long serialVersionUID = 1L;
    
//     private Long totalEmployees;
//     private Long activeEmployees;
//     private Long resignedEmployees;
//     private Long totalDepartments;
//     private Long totalProjects;
//     private Long ongoingProjects;
    
//     // For Charts
//     private List<Map<String, Object>> deptEmployeeStats; // {name: 'IT', value: 10}
//     private List<Map<String, Object>> attendanceStats;   // {name: 'Normal', value: 50}
//     private List<Map<String, Object>> projectStatusStats; // {name: 'Ongoing', value: 5}
//     private List<Map<String, Object>> topAttendanceDepartments; // 部门出勤率Top5
//     private List<Map<String, Object>> employeeContributionTop; // 员工综合贡献Top5
//     private List<Map<String, Object>> leaveStatusStats; // 请假审批状态分布
//     private List<Map<String, Object>> monthlyPerformanceTrend; // 近6个月绩效趋势
// }



package com.ch.managementsystem.entity.vo; // 声明当前类所在的包路径，表示该类属于视图对象（VO）包，用于数据展示和传输

import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter、toString等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可以被序列化
import java.util.List; // 导入List集合类，用于存储多个对象
import java.util.Map; // 导入Map类，用于存储键值对形式的数据

@Data // Lombok注解，自动生成getter、setter、toString、equals、hashCode等常用方法
public class DashboardVO implements Serializable { // 定义仪表盘视图对象类，用于封装仪表盘统计数据

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    private Long totalEmployees; // 定义总员工数属性，数据类型为Long
    private Long activeEmployees; // 定义在职员工数属性，数据类型为Long
    private Long resignedEmployees; // 定义已辞职员工数属性，数据类型为Long
    private Long totalDepartments; // 定义总部门数属性，数据类型为Long
    private Long totalProjects; // 定义总项目数属性，数据类型为Long
    private Long ongoingProjects; // 定义进行中的项目数属性，数据类型为Long
    
    // For Charts
    private List<Map<String, Object>> deptEmployeeStats; // 定义部门员工统计数据属性，用于生成部门与员工数量的统计图 {name: 'IT', value: 10}
    private List<Map<String, Object>> attendanceStats;   // 定义出勤统计数据属性，用于生成员工出勤状态的统计图 {name: 'Normal', value: 50}
    private List<Map<String, Object>> projectStatusStats; // 定义项目状态统计数据属性，用于生成项目状态分布图 {name: 'Ongoing', value: 5}
    private List<Map<String, Object>> topAttendanceDepartments; // 定义出勤率Top5部门统计数据，表示部门出勤率排名前5
    private List<Map<String, Object>> employeeContributionTop; // 定义员工综合贡献Top5统计数据，表示贡献度排名前5的员工
    private List<Map<String, Object>> leaveStatusStats; // 定义请假审批状态分布，用于展示各个请假状态的统计数据
    private List<Map<String, Object>> monthlyPerformanceTrend; // 定义近6个月绩效趋势，用于展示员工绩效的变化趋势
}