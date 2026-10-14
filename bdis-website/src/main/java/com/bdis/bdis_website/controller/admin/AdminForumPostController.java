package com.bdis.bdis_website.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.ForumPost;
import com.bdis.bdis_website.service.AdminSortService;
import com.bdis.bdis_website.service.ForumPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/forum-posts")
public class AdminForumPostController {

    @Autowired
    private ForumPostService forumPostService;

    @Autowired
    private AdminSortService adminSortService;

    @GetMapping
    public Result<List<ForumPost>> list() {
        QueryWrapper<ForumPost> qw = new QueryWrapper<>();
        qw.orderByAsc("sort_order");
        return Result.success(forumPostService.list(qw));
    }

    @GetMapping("/{id}")
    public Result<ForumPost> get(@PathVariable String id) {
        return Result.success(forumPostService.getById(id));
    }

    @PostMapping
    public Result<ForumPost> create(@RequestBody ForumPost item) {
        forumPostService.createAtTop(item);
        return Result.success(item);
    }

    @PutMapping("/reorder")
    public Result<Void> reorder(@RequestBody List<String> orderedIds) {
        boolean updated = adminSortService.reorder(
                forumPostService,
                orderedIds,
                ForumPost::getId,
                (id, order) -> {
                    ForumPost update = new ForumPost();
                    update.setId(id);
                    update.setSortOrder(order);
                    return update;
                });
        return updated ? Result.success() : Result.fail("排序列表已变化，请刷新后重试");
    }

    @PutMapping("/{id}/top")
    public Result<Void> moveToTop(@PathVariable String id) {
        return forumPostService.moveToTop(id) ? Result.success() : Result.fail("帖子不存在");
    }

    @PutMapping("/{id}")
    public Result<ForumPost> update(@PathVariable String id, @RequestBody ForumPost item) {
        item.setId(id);
        forumPostService.updateById(item);
        return Result.success(item);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        forumPostService.removeById(id);
        return Result.success();
    }
}
