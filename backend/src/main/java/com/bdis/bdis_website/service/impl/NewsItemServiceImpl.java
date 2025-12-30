package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.NewsItem;
import com.bdis.bdis_website.mapper.NewsItemMapper;
import com.bdis.bdis_website.service.NewsItemService;
import org.springframework.stereotype.Service;

// @Service：标记为Spring的服务类，自动注入
@Service
public class NewsItemServiceImpl extends ServiceImpl<NewsItemMapper, NewsItem> implements NewsItemService {
    // 暂时无需自定义方法，用IService默认方法即可（如列表查询、新增等）
}
