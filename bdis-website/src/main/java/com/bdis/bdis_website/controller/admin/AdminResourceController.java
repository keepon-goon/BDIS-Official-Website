package com.bdis.bdis_website.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.EducationResource;
import com.bdis.bdis_website.service.AdminSortService;
import com.bdis.bdis_website.service.EducationResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/resources")
public class AdminResourceController {

    @Autowired
    private EducationResourceService resourceService;

    @Autowired
    private AdminSortService adminSortService;

    @GetMapping
    public Result<List<EducationResource>> list(@RequestParam(required = false) String category) {
        QueryWrapper<EducationResource> qw = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            qw.eq("category", category);
        }
        qw.orderByAsc("sort_order");
        return Result.success(resourceService.list(qw));
    }

    @GetMapping("/{id}")
    public Result<EducationResource> get(@PathVariable Long id) {
        return Result.success(resourceService.getById(id));
    }

    @PostMapping
    public Result<EducationResource> create(@RequestBody EducationResource item) {
        resourceService.createAtTop(item);
        return Result.success(item);
    }

    @PutMapping("/reorder")
    public Result<Void> reorder(@RequestBody List<String> orderedIds) {
        boolean updated = adminSortService.reorder(
                resourceService,
                orderedIds,
                item -> String.valueOf(item.getId()),
                (id, order) -> {
                    EducationResource update = new EducationResource();
                    update.setId(Long.valueOf(id));
                    update.setSortOrder(order);
                    return update;
                });
        return updated ? Result.success() : Result.fail("排序列表已变化，请刷新后重试");
    }

    @PutMapping("/{id}/top")
    public Result<Void> moveToTop(@PathVariable Long id) {
        return resourceService.moveToTop(id) ? Result.success() : Result.fail("资源不存在");
    }

    @PutMapping("/{id}")
    public Result<EducationResource> update(@PathVariable Long id, @RequestBody EducationResource item) {
        item.setId(id);
        resourceService.updateById(item);
        return Result.success(item);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resourceService.removeById(id);
        return Result.success();
    }
}
