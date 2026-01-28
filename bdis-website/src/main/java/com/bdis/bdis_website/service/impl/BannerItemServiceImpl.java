package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.BannerItem;
import com.bdis.bdis_website.mapper.BannerItemMapper;
import com.bdis.bdis_website.service.BannerItemService;
import org.springframework.stereotype.Service;

@Service
public class BannerItemServiceImpl extends ServiceImpl<BannerItemMapper, BannerItem> implements BannerItemService {
}
