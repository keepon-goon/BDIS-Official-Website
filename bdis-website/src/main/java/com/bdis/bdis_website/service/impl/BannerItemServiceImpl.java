package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.BannerItem;
import com.bdis.bdis_website.mapper.BannerItemMapper;
import com.bdis.bdis_website.service.BannerItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BannerItemServiceImpl extends ServiceImpl<BannerItemMapper, BannerItem> implements BannerItemService {
    @Override
    @Transactional
    public boolean createAtTop(BannerItem item) {
        item.setSortOrder(0);
        baseMapper.update(null, new UpdateWrapper<BannerItem>()
                .ge("sort_order", 0)
                .setSql("sort_order = sort_order + 1"));
        return save(item);
    }

    @Override
    @Transactional
    public boolean moveToTop(Long id) {
        BannerItem item = getById(id);
        if (item == null) {
            return false;
        }

        int currentOrder = item.getSortOrder() == null ? 0 : item.getSortOrder();
        if (currentOrder > 0) {
            baseMapper.update(null, new UpdateWrapper<BannerItem>()
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
}
