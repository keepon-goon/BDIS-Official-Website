package com.bdis.bdis_website.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.NewsItem;
import com.bdis.bdis_website.service.NewsItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {

    @Autowired
    private NewsItemService newsItemService;

    @GetMapping
    public Result<List<NewsItem>> list(@RequestParam(required = false) String category) {
        QueryWrapper<NewsItem> qw = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            qw.eq("category", category);
        }
        qw.orderByAsc("`order`");
        return Result.success(newsItemService.list(qw));
    }

    @GetMapping("/{id}")
    public Result<NewsItem> get(@PathVariable Long id) {
        return Result.success(newsItemService.getById(id));
    }

    @PostMapping
    public Result<NewsItem> create(@RequestBody NewsItem item) {
        newsItemService.save(item);
        return Result.success(item);
    }

    @PutMapping("/{id}")
    public Result<NewsItem> update(@PathVariable Long id, @RequestBody NewsItem item) {
        item.setId(id);
        newsItemService.updateById(item);
        return Result.success(item);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        newsItemService.removeById(id);
        return Result.success();
    }
}
