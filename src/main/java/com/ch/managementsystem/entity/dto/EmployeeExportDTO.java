package com.ch.managementsystem.entity.dto; // 声明当前类所在的包路径，表示该类属于数据传输对象（DTO）包

import com.alibaba.excel.annotation.ExcelProperty; // 导入阿里巴巴EasyExcel的ExcelProperty注解，用于Excel字段映射
import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可序列化
import java.math.BigDecimal; // 导入BigDecimal类，用于表示高精度的货币或数值
import java.time.LocalDate; // 导入LocalDate类，用于表示日期（不包含时间）

@Data // 自动生成getter、setter、toString、equals、hashCode等方法
public class EmployeeExportDTO implements Serializable { // 定义员工导出DTO类，用于导出员工数据到Excel

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @ExcelProperty("ID") // 设置Excel列的标题为"ID"，用于映射到Excel中的列
    private Long id; // 定义员工ID属性，数据类型为Long

    @ExcelProperty("Name") // 设置Excel列的标题为"Name"，用于映射到Excel中的列
    private String name; // 定义员工姓名属性，数据类型为String

    @ExcelProperty("Gender") // 设置Excel列的标题为"Gender"，用于映射到Excel中的列
    private String genderStr; // 定义性别属性，数据类型为String，用于展示性别字符串（例如："Male", "Female"）

    @ExcelProperty("Job Number") // 设置Excel列的标题为"Job Number"，用于映射到Excel中的列
    private String jobNumber; // 定义员工工号属性，数据类型为String

    @ExcelProperty("Position") // 设置Excel列的标题为"Position"，用于映射到Excel中的列
    private String position; // 定义员工职位属性，数据类型为String

    @ExcelProperty("Base Salary") // 设置Excel列的标题为"Base Salary"，用于映射到Excel中的列
    private BigDecimal baseSalary; // 定义基本工资属性，数据类型为BigDecimal（用于高精度金额）

    @ExcelProperty("Hire Date") // 设置Excel列的标题为"Hire Date"，用于映射到Excel中的列
    private LocalDate hireDate; // 定义入职日期属性，数据类型为LocalDate（仅包含日期）

    @ExcelProperty("Status") // 设置Excel列的标题为"Status"，用于映射到Excel中的列
    private String statusStr; // 定义员工状态属性，数据类型为String，表示员工的状态（例如："Active", "Inactive"）

    @ExcelProperty("ID Card") // 设置Excel列的标题为"ID Card"，用于映射到Excel中的列
    private String idCard; // 定义员工身份证号码属性，数据类型为String

    @ExcelProperty("Address") // 设置Excel列的标题为"Address"，用于映射到Excel中的列
    private String address; // 定义员工地址属性，数据类型为String

    @ExcelProperty("Department") // 设置Excel列的标题为"Department"，用于映射到Excel中的列
    private String deptName; // 定义部门名称属性，数据类型为String，表示员工所在的部门
}