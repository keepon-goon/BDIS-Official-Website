package com.bdis.bdis_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bdis.bdis_website.entity.ProjectProgressItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目进展Mapper
 * 表结构更新，Mapper无需额外修改，继承BaseMapper即可
 */
@Mapper
public interface ProjectProgressMapper extends BaseMapper<ProjectProgressItem> {
    // 无额外方法，复用MyBatis-Plus基础查询
}

