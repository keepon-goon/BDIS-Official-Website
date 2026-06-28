package com.bdis.bdis_website.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.ProjectProgressItem;
import com.bdis.bdis_website.service.ProjectProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    @Autowired
    private ProjectProgressService projectService;

    @GetMapping
    public Result<List<ProjectProgressItem>> list() {
        QueryWrapper<ProjectProgressItem> qw = new QueryWrapper<>();
        qw.orderByAsc("sort_order");
        return Result.success(projectService.list(qw));
    }

    @GetMapping("/{id}")
    public Result<ProjectProgressItem> get(@PathVariable String id) {
        return Result.success(projectService.getById(id));
    }

    @PostMapping
    public Result<ProjectProgressItem> create(@RequestBody ProjectProgressItem item) {
        projectService.save(item);
        return Result.success(item);
    }

    @PutMapping("/{id}")
    public Result<ProjectProgressItem> update(@PathVariable String id, @RequestBody ProjectProgressItem item) {
        item.setId(id);
        projectService.updateById(item);
        return Result.success(item);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        projectService.removeById(id);
        return Result.success();
    }
}
