package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("news_item") // 严格对应数据库表名
public class NewsItem {
    @TableId(type = IdType.AUTO)
    private Long id; // 重点：是id不是1d，字段名无任何笔误
    private String title;
    private String category;
    private String summary;
    private String publishDate;
    private String link;
    private String coverThumbUrl;
    private Integer order;
}
