package com.ch.managementsystem.controller; // 定义当前类所在的包路径

import com.alibaba.excel.EasyExcel; // 导入EasyExcel库，用于Excel文件导出
import com.baomidou.mybatisplus.core.metadata.IPage; // MyBatisPlus分页接口
import com.ch.managementsystem.common.Result; // 项目统一返回结果封装类
import com.ch.managementsystem.entity.Employee; // 员工实体类
import com.ch.managementsystem.entity.dto.EmployeeExportDTO; // 员工导出Excel使用的DTO对象
import com.ch.managementsystem.entity.vo.ProfileVO;
import com.ch.managementsystem.service.EmployeeService; // 员工业务逻辑服务接口
import org.springframework.beans.factory.annotation.Autowired; // Spring自动注入注解
import org.springframework.security.access.prepost.PreAuthorize; // Spring Security权限控制注解
import org.springframework.web.bind.annotation.*; // Spring MVC控制器相关注解

import jakarta.servlet.http.HttpServletResponse; // HTTP响应对象，用于返回文件流
import org.springframework.web.multipart.MultipartFile; // 用于处理文件上传
import java.io.IOException; // IO异常类
import java.net.URLEncoder; // URL编码工具类
import java.util.List; // List集合接口

@RestController // 声明当前类为REST控制器，返回JSON数据
@RequestMapping("/api/employees") // 定义控制器基础访问路径
public class EmployeeController { // 员工管理控制器类

    @Autowired // 自动注入EmployeeService业务层对象
    private EmployeeService employeeService; // 员工业务逻辑服务

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<ProfileVO> getProfile() {
        return Result.success(employeeService.getCurrentUserProfile());
    }

    @GetMapping // 定义GET请求接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<IPage<Employee>> getPage( // 分页查询员工信息接口
            @RequestParam(defaultValue = "1") int page, // 当前页码，默认值为1
            @RequestParam(defaultValue = "10") int size, // 每页显示条数，默认10条
            @RequestParam(required = false) String name, // 员工姓名查询条件，可为空
            @RequestParam(required = false) Long deptId) { // 部门ID查询条件，可为空
        return Result.success(employeeService.getPageWithDept(page, size, name, deptId)); // 调用service层分页查询并返回结果
    }

    @GetMapping("/{id}") // 定义根据ID查询员工接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Employee> getById(@PathVariable Long id) { // 获取路径参数id
        return Result.success(employeeService.getById(id)); // 根据ID查询员工并返回
    }

    @PostMapping // 定义POST请求接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> save(@RequestBody Employee employee) { // 新增员工接口，请求体为员工对象
        return Result.success(employeeService.save(employee)); // 调用service保存员工数据
    }

    @PutMapping // 定义PUT请求接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> update(@RequestBody Employee employee) { // 更新员工接口，请求体为员工对象
        return Result.success(employeeService.updateById(employee)); // 根据ID更新员工数据
    }

    @DeleteMapping("/{id}") // 定义DELETE请求接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> delete(@PathVariable Long id) { // 删除员工接口，获取路径参数id
        return Result.success(employeeService.removeById(id)); // 根据ID删除员工
    }

    @DeleteMapping("/batch") // 定义批量删除接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) { // 接收员工ID列表
        return Result.success(employeeService.removeByIds(ids)); // 批量删除员工数据
    }

    @GetMapping("/export") // 定义员工数据导出Excel接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public void export(HttpServletResponse response) throws IOException { // 向浏览器返回Excel文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // 设置响应类型为Excel文件
        response.setCharacterEncoding("utf-8"); // 设置字符编码为UTF-8
        String fileName = URLEncoder.encode("Employee_List", "UTF-8").replaceAll("\\+", "%20"); // 对文件名进行URL编码，防止中文乱码
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx"); // 设置浏览器下载文件的响应头
        
        List<EmployeeExportDTO> list = employeeService.getExportList(); // 调用service层获取需要导出的员工数据
        EasyExcel.write(response.getOutputStream(), EmployeeExportDTO.class) // 使用EasyExcel写入输出流
                .sheet("Employees") // 创建Excel工作表名称为Employees
                .doWrite(list); // 将数据写入Excel文件
    }

    @PostMapping("/import") // 定义员工数据导入Excel接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> importExcel(@RequestParam("file") MultipartFile file) throws IOException { // 接收Excel文件
        return Result.success(employeeService.importExcel(file)); // 调用service层处理导入逻辑
    }

    @GetMapping("/import-template") // 定义导出导入模板接口
    @PreAuthorize("hasAnyRole('admin','hr')")
    public void importTemplate(HttpServletResponse response) throws IOException { // 向浏览器返回Excel模板
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // 设置响应类型为Excel文件
        response.setCharacterEncoding("utf-8"); // 设置字符编码为UTF-8
        String fileName = URLEncoder.encode("Employee_Import_Template", "UTF-8").replaceAll("\\+", "%20"); // 对文件名进行URL编码，防止中文乱码
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx"); // 设置浏览器下载文件的响应头
        
        List<Employee> templateList = employeeService.getImportTemplate(); // 调用service层获取模板数据
        EasyExcel.write(response.getOutputStream(), Employee.class) // 使用EasyExcel写入输出流
                .sheet("Template") // 创建Excel工作表名称为Template
                .doWrite(templateList); // 将模板数据写入Excel文件
    }
}
