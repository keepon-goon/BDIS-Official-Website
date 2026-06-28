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
        limit = limit == null ? 5 : limit;

        QueryWrapper<ForumPost> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort_order").last("limit " + limit);

        List<ForumPost> list = baseMapper.selectList(wrapper);
        for (ForumPost post : list) {
            String roles = post.getRecruitRoles();
            post.setRecruitRolesArray(roles != null && !roles.isEmpty() ? roles.split(",") : new String[0]);
        }
        return list;
    }
}
