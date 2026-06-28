package com.bdis.bdis_website.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.ForumPost;
import com.bdis.bdis_website.service.ForumPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/forum-posts")
public class AdminForumPostController {

    @Autowired
    private ForumPostService forumPostService;

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
        forumPostService.save(item);
        return Result.success(item);
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
