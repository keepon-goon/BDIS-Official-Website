package com.bdis.bdis_website.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bdis.bdis_website.entity.EducationResource;

public interface EducationResourceService {
    IPage<EducationResource> page(int page, int limit, String category, String keyword);
}