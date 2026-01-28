package com.bdis.bdis_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bdis.bdis_website.entity.AnnouncementItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标记为MyBatis映射接口
public interface AnnouncementItemMapper extends BaseMapper<AnnouncementItem> {
    // 无需额外方法，BaseMapper提供基础CRUD
}
