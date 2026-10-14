package com.bdis.bdis_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdis.bdis_website.entity.BannerItem;

public interface BannerItemService extends IService<BannerItem> {
    boolean createAtTop(BannerItem item);

    boolean moveToTop(Long id);
}
