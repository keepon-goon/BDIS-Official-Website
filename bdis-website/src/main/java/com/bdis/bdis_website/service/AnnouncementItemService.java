package com.bdis.bdis_website.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bdis.bdis_website.entity.AnnouncementItem;

public interface AnnouncementItemService extends IService<AnnouncementItem> {
    // 文档3.2要求：分页+分类查询公告列表
    IPage<AnnouncementItem> getAnnouncementPage(Integer page, Integer limit, String category);
}

