package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("banner_item")
public class BannerItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String bannerTitle;
    private String bannerImgUrl;
    private String bannerLink;
    private Integer sort; // 替换原private Integer order;
    private Integer isShow;
}
