package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.ForumPost;
import com.bdis.bdis_website.service.ForumPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<Result<List<ForumPost>>> getForumPosts(
            @RequestParam(required = false) Integer limit
    ) {
        if (limit != null && (limit < 1 || limit > 100)) {
            return ResponseEntity.badRequest().body(Result.fail("查询条数须为1到100"));
        }
        List<ForumPost> posts = forumPostService.getForumPosts(limit);
        return ResponseEntity.ok(Result.success(posts));
    }

    @GetMapping("/forum/{id}")
    public Result<ForumPost> getForumDetail(@PathVariable String id) {
        ForumPost post = forumPostService.getById(id);
        if (post != null) {
            String roles = post.getRecruitRoles();
            post.setRecruitRolesArray(roles != null && !roles.isEmpty() ? roles.split(",") : new String[0]);
        }
        return Result.success(post);
    }
}
