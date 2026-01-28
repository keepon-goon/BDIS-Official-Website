package com.bdis.bdis_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdis.bdis_website.entity.ProjectProgressItem;

import java.util.List;

/**
 * 项目进展Service接口（方法名不变，适配新实体类）
 */
public interface ProjectProgressService extends IService<ProjectProgressItem> {
    List<ProjectProgressItem> getProjectProgressList(Integer limit);
}

