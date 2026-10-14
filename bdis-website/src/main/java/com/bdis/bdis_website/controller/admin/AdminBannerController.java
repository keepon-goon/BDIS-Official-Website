package com.bdis.bdis_website.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.BannerItem;
import com.bdis.bdis_website.service.AdminSortService;
import com.bdis.bdis_website.service.BannerItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banners")
public class AdminBannerController {

    @Autowired
    private BannerItemService bannerItemService;

    @Autowired
    private AdminSortService adminSortService;

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
        bannerItemService.createAtTop(item);
        return Result.success(item);
    }

    @PutMapping("/reorder")
    public Result<Void> reorder(@RequestBody List<String> orderedIds) {
        boolean updated = adminSortService.reorder(
                bannerItemService,
                orderedIds,
                item -> String.valueOf(item.getId()),
                (id, order) -> {
                    BannerItem update = new BannerItem();
                    update.setId(Long.valueOf(id));
                    update.setSortOrder(order);
                    return update;
                });
        return updated ? Result.success() : Result.fail("排序列表已变化，请刷新后重试");
    }

    @PutMapping("/{id}/top")
    public Result<Void> moveToTop(@PathVariable Long id) {
        return bannerItemService.moveToTop(id) ? Result.success() : Result.fail("轮播图不存在");
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
