package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.ForumPost;
import com.bdis.bdis_website.service.ForumPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/platform") // 接口根路径
public class PlatformController {

    @Autowired
    private ForumPostService forumPostService;

    // 文档4.2接口：GET /api/platform/forum
    @GetMapping("/forum")
    public Result<List<ForumPost>> getForumPosts(
            @RequestParam(required = false) Integer limit
    ) {
        List<ForumPost> posts = forumPostService.getForumPosts(limit);
        return Result.success(posts); // data直接返回数组，符合文档要求
    }
}
