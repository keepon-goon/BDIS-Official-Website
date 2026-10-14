package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.ForumPost;
import com.bdis.bdis_website.mapper.ForumPostMapper;
import com.bdis.bdis_website.service.ForumPostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForumPostServiceImpl extends ServiceImpl<ForumPostMapper, ForumPost>
        implements ForumPostService {

    @Override
    @Transactional
    public boolean createAtTop(ForumPost item) {
        item.setSortOrder(0);
        baseMapper.update(null, new UpdateWrapper<ForumPost>()
                .ge("sort_order", 0)
                .setSql("sort_order = sort_order + 1"));
        return save(item);
    }

    @Override
    @Transactional
    public boolean moveToTop(String id) {
        ForumPost item = getById(id);
        if (item == null) {
            return false;
        }

        int currentOrder = item.getSortOrder() == null ? 0 : item.getSortOrder();
        if (currentOrder > 0) {
            baseMapper.update(null, new UpdateWrapper<ForumPost>()
                    .lt("sort_order", currentOrder)
                    .setSql("sort_order = sort_order + 1"));
            item.setSortOrder(0);
            return updateById(item);
        }
        if (item.getSortOrder() == null) {
            item.setSortOrder(0);
            return updateById(item);
        }
        return true;
    }

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
