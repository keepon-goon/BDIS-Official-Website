package com.bdis.bdis_website.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.ProjectProgressItem;
import com.bdis.bdis_website.service.AdminSortService;
import com.bdis.bdis_website.service.ProjectProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    @Autowired
    private ProjectProgressService projectService;

    @Autowired
    private AdminSortService adminSortService;

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
        projectService.createAtTop(item);
        return Result.success(item);
    }

    @PutMapping("/reorder")
    public Result<Void> reorder(@RequestBody List<String> orderedIds) {
        boolean updated = adminSortService.reorder(
                projectService,
                orderedIds,
                ProjectProgressItem::getId,
                (id, order) -> {
                    ProjectProgressItem update = new ProjectProgressItem();
                    update.setId(id);
                    update.setSortOrder(order);
                    return update;
                });
        return updated ? Result.success() : Result.fail("排序列表已变化，请刷新后重试");
    }

    @PutMapping("/{id}/top")
    public Result<Void> moveToTop(@PathVariable String id) {
        return projectService.moveToTop(id) ? Result.success() : Result.fail("项目不存在");
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
