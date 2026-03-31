package com.ch.managementsystem.entity.dto; // 声明当前类所在的包路径，表示该类属于数据传输对象（DTO）包

import com.alibaba.excel.annotation.ExcelProperty; // 导入阿里巴巴EasyExcel的ExcelProperty注解，用于Excel字段映射
import lombok.Data; // 导入Lombok的@Data注解，用于自动生成getter、setter等方法
import java.io.Serializable; // 导入Serializable接口，表示该类可序列化

@Data // 自动生成getter、setter、toString、equals、hashCode等方法
public class DepartmentExcelDTO implements Serializable { // 定义部门Excel数据传输对象类，用于导入导出Excel数据

    private static final long serialVersionUID = 1L; // 定义serialVersionUID，确保序列化兼容性

    @ExcelProperty("上级部门ID") // 设置Excel列的标题为"上级部门ID"，用于映射到Excel中的列
    private Long parentId; // 定义上级部门ID属性，数据类型为Long

    @ExcelProperty("部门名称") // 设置Excel列的标题为"部门名称"，用于映射到Excel中的列
    private String deptName; // 定义部门名称属性，数据类型为String

    @ExcelProperty("排序") // 设置Excel列的标题为"排序"，用于映射到Excel中的列
    private Integer orderNum; // 定义排序属性，数据类型为Integer

    @ExcelProperty("负责人") // 设置Excel列的标题为"负责人"，用于映射到Excel中的列
    private String leader; // 定义负责人属性，数据类型为String

    @ExcelProperty("联系电话") // 设置Excel列的标题为"联系电话"，用于映射到Excel中的列
    private String phone; // 定义联系电话属性，数据类型为String

    @ExcelProperty("邮箱") // 设置Excel列的标题为"邮箱"，用于映射到Excel中的列
    private String email; // 定义邮箱属性，数据类型为String

    @ExcelProperty("状态(1启用0停用)") // 设置Excel列的标题为"状态(1启用0停用)"，用于映射到Excel中的列
    private Integer status; // 定义状态属性，数据类型为Integer，表示部门的启用状态
}