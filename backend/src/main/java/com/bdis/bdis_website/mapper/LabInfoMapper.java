package com.bdis.bdis_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bdis.bdis_website.entity.LabInfo;
import org.apache.ibatis.annotations.Mapper;

// 加@Mapper注解（或通过启动类扫描）
@Mapper
public interface LabInfoMapper extends BaseMapper<LabInfo> {
}

