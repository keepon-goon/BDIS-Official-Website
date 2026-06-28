package com.bdis.bdis_website.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.AnnouncementItem;
import com.bdis.bdis_website.service.AnnouncementItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/announcements")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementItemService announcementService;

    @GetMapping
    public Result<List<AnnouncementItem>> list(@RequestParam(required = false) String category) {
        QueryWrapper<AnnouncementItem> qw = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            qw.eq("category", category);
        }
        qw.orderByAsc("sort_order");
        return Result.success(announcementService.list(qw));
    }

    @GetMapping("/{id}")
    public Result<AnnouncementItem> get(@PathVariable Long id) {
        return Result.success(announcementService.getById(id));
    }

    @PostMapping
    public Result<AnnouncementItem> create(@RequestBody AnnouncementItem item) {
        announcementService.save(item);
        return Result.success(item);
    }

    @PutMapping("/{id}")
    public Result<AnnouncementItem> update(@PathVariable Long id, @RequestBody AnnouncementItem item) {
        item.setId(id);
        announcementService.updateById(item);
        return Result.success(item);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.removeById(id);
        return Result.success();
    }
}
