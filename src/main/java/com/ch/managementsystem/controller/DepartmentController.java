package com.ch.managementsystem.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ch.managementsystem.common.Result;
import com.ch.managementsystem.entity.Department;
import com.ch.managementsystem.entity.dto.DepartmentExcelDTO;
import com.ch.managementsystem.service.DepartmentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<List<Department>> getAll() {
        return Result.success(departmentService.list());
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<List<Department>> getTree() {
        return Result.success(departmentService.getTree());
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public Result<Boolean> save(@RequestBody Department department) {
        return Result.success(departmentService.save(department));
    }

    @PutMapping
    @PreAuthorize("hasRole('admin')")
    public Result<Boolean> update(@RequestBody Department department) {
        return Result.success(departmentService.updateById(department));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(departmentService.removeById(id));
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('admin')")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        return Result.success(departmentService.removeByIds(ids));
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('admin')")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("Department_List", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<DepartmentExcelDTO> list = departmentService.list().stream()
                .map(item -> {
                    DepartmentExcelDTO dto = new DepartmentExcelDTO();
                    dto.setParentId(item.getParentId());
                    dto.setDeptName(item.getDeptName());
                    dto.setOrderNum(item.getOrderNum());
                    dto.setLeader(item.getLeader());
                    dto.setPhone(item.getPhone());
                    dto.setEmail(item.getEmail());
                    dto.setStatus(item.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());

        EasyExcel.write(response.getOutputStream(), DepartmentExcelDTO.class)
                .sheet("Departments")
                .doWrite(list);
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('admin')")
    public Result<Map<String, Integer>> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        List<DepartmentExcelDTO> rows = EasyExcel.read(file.getInputStream())
                .head(DepartmentExcelDTO.class)
                .sheet()
                .doReadSync();

        int success = 0;
        int skipped = 0;
        for (DepartmentExcelDTO row : rows) {
            if (row == null || row.getDeptName() == null || row.getDeptName().isBlank()) {
                skipped++;
                continue;
            }

            Long parentId = row.getParentId() == null ? 0L : row.getParentId();
            Department existing = departmentService.getOne(
                    new LambdaQueryWrapper<Department>()
                            .eq(Department::getDeptName, row.getDeptName())
                            .eq(Department::getParentId, parentId)
                            .last("limit 1")
            );
            if (existing != null) {
                skipped++;
                continue;
            }

            Department department = new Department();
            department.setParentId(parentId);
            department.setDeptName(row.getDeptName().trim());
            department.setOrderNum(Objects.requireNonNullElse(row.getOrderNum(), 0));
            department.setLeader(row.getLeader());
            department.setPhone(row.getPhone());
            department.setEmail(row.getEmail());
            department.setStatus(Objects.requireNonNullElse(row.getStatus(), 1));

            if (departmentService.save(department)) {
                success++;
            } else {
                skipped++;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("total", rows.size());
        result.put("success", success);
        result.put("skipped", skipped);
        return Result.success(result);
    }
}
