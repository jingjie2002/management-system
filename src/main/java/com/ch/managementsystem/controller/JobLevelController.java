package com.ch.managementsystem.controller;

import com.ch.managementsystem.common.Result;
import com.ch.managementsystem.entity.JobLevel;
import com.ch.managementsystem.service.JobLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/job-levels")
public class JobLevelController {

    @Autowired
    private JobLevelService jobLevelService;

    @GetMapping
    @PreAuthorize("hasAnyRole('admin','hr','employee')")
    public Result<List<JobLevel>> getAll() {
        return Result.success(jobLevelService.listOrderedByRank());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> save(@RequestBody JobLevel jobLevel) {
        return Result.success(jobLevelService.save(jobLevel));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> update(@RequestBody JobLevel jobLevel) {
        return Result.success(jobLevelService.updateById(jobLevel));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin','hr')")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(jobLevelService.removeById(id));
    }
}
