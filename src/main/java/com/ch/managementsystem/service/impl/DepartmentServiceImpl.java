package com.ch.managementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.managementsystem.entity.Department;
import com.ch.managementsystem.mapper.DepartmentMapper;
import com.ch.managementsystem.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public List<Department> getTree() {
        List<Department> allDepts = this.list(new LambdaQueryWrapper<Department>().orderByAsc(Department::getOrderNum));
        
        // Use stream grouping by parentId
        Map<Long, List<Department>> deptMap = allDepts.stream()
                .collect(Collectors.groupingBy(dept -> dept.getParentId() == null ? 0L : dept.getParentId()));

        // Get root nodes (parentId = 0)
        List<Department> rootDepts = deptMap.getOrDefault(0L, new ArrayList<>());

        // Build tree recursively
        for (Department root : rootDepts) {
            buildChildren(root, deptMap);
        }




        return rootDepts;
    }

    private void buildChildren(Department parent, Map<Long, List<Department>> deptMap) {
        List<Department> children = deptMap.get(parent.getId());
        if (children != null) {
            parent.setChildren(children);
            for (Department child : children) {
                buildChildren(child, deptMap);
            }
        }
    }
}
