package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.EducationResource;
import com.bdis.bdis_website.mapper.EducationResourceMapper;
import com.bdis.bdis_website.service.EducationResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
@Service
public class EducationResourceServiceImpl
        extends ServiceImpl<EducationResourceMapper, EducationResource>
        implements EducationResourceService {

    @Override
    @Transactional
    public boolean createAtTop(EducationResource item) {
        item.setSortOrder(0);
        baseMapper.update(null, new UpdateWrapper<EducationResource>()
                .ge("sort_order", 0)
                .setSql("sort_order = sort_order + 1"));
        return save(item);
    }

    @Override
    @Transactional
    public boolean moveToTop(Long id) {
        EducationResource item = getById(id);
        if (item == null) {
            return false;
        }

        int currentOrder = item.getSortOrder() == null ? 0 : item.getSortOrder();
        if (currentOrder > 0) {
            baseMapper.update(null, new UpdateWrapper<EducationResource>()
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
    public IPage<EducationResource> page(int page, int limit, String category, String keyword) {
        Page<EducationResource> p = new Page<>(page, limit);

        QueryWrapper<EducationResource> wrapper = new QueryWrapper<>();

        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq("category", category);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like("title", keyword).or().like("description", keyword);
        }

        // 关键：按 sort_order 升序，如果 sort_order 相同再按 id 升序
        wrapper.orderByAsc("sort_order").orderByAsc("id");

        return baseMapper.selectPage(p, wrapper);
    }
}
