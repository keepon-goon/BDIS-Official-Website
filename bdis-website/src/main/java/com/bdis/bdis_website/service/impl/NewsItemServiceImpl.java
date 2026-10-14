package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.NewsItem;
import com.bdis.bdis_website.mapper.NewsItemMapper;
import com.bdis.bdis_website.service.NewsItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @Service：标记为Spring的服务类，自动注入
@Service
public class NewsItemServiceImpl extends ServiceImpl<NewsItemMapper, NewsItem> implements NewsItemService {
    @Override
    @Transactional
    public boolean createAtTop(NewsItem item) {
        item.setOrder(0);
        baseMapper.update(null, new UpdateWrapper<NewsItem>()
                .ge("`order`", 0)
                .setSql("`order` = `order` + 1"));
        return save(item);
    }

    @Override
    @Transactional
    public boolean moveToTop(Long id) {
        NewsItem item = getById(id);
        if (item == null) {
            return false;
        }

        int currentOrder = item.getOrder() == null ? 0 : item.getOrder();
        if (currentOrder > 0) {
            baseMapper.update(null, new UpdateWrapper<NewsItem>()
                    .lt("`order`", currentOrder)
                    .setSql("`order` = `order` + 1"));
            item.setOrder(0);
            return updateById(item);
        }
        if (item.getOrder() == null) {
            item.setOrder(0);
            return updateById(item);
        }
        return true;
    }
}
