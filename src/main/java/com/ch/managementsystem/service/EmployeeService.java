package com.ch.managementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.managementsystem.entity.Employee;
import com.ch.managementsystem.entity.dto.EmployeeExportDTO;
import com.ch.managementsystem.entity.vo.ProfileVO;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface EmployeeService extends IService<Employee> {
    IPage<Employee> getPageWithDept(int page, int size, String name, Long deptId);
    List<EmployeeExportDTO> getExportList();
    ProfileVO getCurrentUserProfile();
    boolean importExcel(List<Employee> employees);
    boolean importExcel(MultipartFile file) throws IOException;
    List<Employee> getImportTemplate();
}
