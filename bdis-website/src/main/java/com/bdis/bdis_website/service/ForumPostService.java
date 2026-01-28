package com.bdis.bdis_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdis.bdis_website.entity.ForumPost;

import java.util.List;

public interface ForumPostService extends IService<ForumPost> {
    // 文档4.2要求：按limit查询组队贴列表（首页用）
    List<ForumPost> getForumPosts(Integer limit);
}
