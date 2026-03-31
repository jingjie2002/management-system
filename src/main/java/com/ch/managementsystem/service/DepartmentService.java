package com.ch.managementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.managementsystem.entity.Department;
import java.util.List;

public interface DepartmentService extends IService<Department> {
    List<Department> getTree();
}
