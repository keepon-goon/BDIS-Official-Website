package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.AnnouncementItem;
import com.bdis.bdis_website.mapper.AnnouncementItemMapper;
import com.bdis.bdis_website.service.AnnouncementItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementItemServiceImpl extends ServiceImpl<AnnouncementItemMapper, AnnouncementItem>
        implements AnnouncementItemService {

    @Override
    @Transactional
    public boolean createAtTop(AnnouncementItem item) {
        item.setSortOrder(0);
        baseMapper.update(null, new UpdateWrapper<AnnouncementItem>()
                .ge("sort_order", 0)
                .setSql("sort_order = sort_order + 1"));
        return save(item);
    }

    @Override
    @Transactional
    public boolean moveToTop(Long id) {
        AnnouncementItem item = getById(id);
        if (item == null) {
            return false;
        }

        int currentOrder = item.getSortOrder() == null ? 0 : item.getSortOrder();
        if (currentOrder > 0) {
            baseMapper.update(null, new UpdateWrapper<AnnouncementItem>()
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
    public IPage<AnnouncementItem> getAnnouncementPage(Integer page, Integer limit, String category) {
        // 1. 设置分页默认值（文档3.2：page默认1，limit默认10）
        page = page == null ? 1 : page;
        limit = limit == null ? 10 : limit;
        IPage<AnnouncementItem> pageObj = new Page<>(page, limit);

        // 2. 分类条件（非all时添加过滤）
        QueryWrapper<AnnouncementItem> wrapper = new QueryWrapper<>();
        if (category != null && !"all".equals(category)) {
            wrapper.eq("category", category);
        }
        wrapper.orderByAsc("sort_order"); // 按排序字段升序

        // 3. 执行分页查询
        return baseMapper.selectPage(pageObj, wrapper);
    }
}
