package com.bdis.bdis_website.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bdis.bdis_website.entity.EducationResource;

public interface EducationResourceService extends IService<EducationResource> {
    IPage<EducationResource> page(int page, int limit, String category, String keyword);
}
