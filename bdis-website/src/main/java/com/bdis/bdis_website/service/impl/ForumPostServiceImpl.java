package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.ForumPost;
import com.bdis.bdis_website.mapper.ForumPostMapper;
import com.bdis.bdis_website.service.ForumPostService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumPostServiceImpl extends ServiceImpl<ForumPostMapper, ForumPost>
        implements ForumPostService {

    @Override
    public List<ForumPost> getForumPosts(Integer limit) {
        // 1. 设置默认limit（文档4.2：前端当前传5）
        limit = limit == null ? 5 : limit;

        // 2. 查询条件：按排序升序，限制条数
        QueryWrapper<ForumPost> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort_order").last("limit " + limit); // 限制返回条数

        return baseMapper.selectList(wrapper);
    }
}
