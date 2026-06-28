package com.bdis.bdis_website.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.BannerItem;
import com.bdis.bdis_website.service.BannerItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banners")
public class AdminBannerController {

    @Autowired
    private BannerItemService bannerItemService;

    @GetMapping
    public Result<List<BannerItem>> list() {
        QueryWrapper<BannerItem> qw = new QueryWrapper<>();
        qw.orderByAsc("sort_order");
        return Result.success(bannerItemService.list(qw));
    }

    @GetMapping("/{id}")
    public Result<BannerItem> get(@PathVariable Long id) {
        return Result.success(bannerItemService.getById(id));
    }

    @PostMapping
    public Result<BannerItem> create(@RequestBody BannerItem item) {
        bannerItemService.save(item);
        return Result.success(item);
    }

    @PutMapping("/{id}")
    public Result<BannerItem> update(@PathVariable Long id, @RequestBody BannerItem item) {
        item.setId(id);
        bannerItemService.updateById(item);
        return Result.success(item);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerItemService.removeById(id);
        return Result.success();
    }
}
