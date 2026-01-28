package com.bdis.bdis_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bdis.bdis_website.entity.NewsItem;
import org.apache.ibatis.annotations.Mapper;

    // @Mapper：告诉Spring这是MyBatis的Mapper接口，自动扫描
    @Mapper
    public interface NewsItemMapper extends BaseMapper<NewsItem> {
        // BaseMapper已包含增删改查方法，无需额外写代码
    }


