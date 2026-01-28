package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;

@Getter
enum AnnouncementCategory {
    recruit("recruit"),
    activity("activity"),
    notice("notice"),
    contest("contest"),
    project_recruit("project_recruit");

    @EnumValue
    private final String value;

    AnnouncementCategory(String value) {
        this.value = value;
    }
}

@Data
@TableName("announcement_item")
public class AnnouncementItem {
    private Long id;
    private String title;
    private AnnouncementCategory category;
    private String summary;
    private String publishDate;
    private String link;
    private String coverThumbUrl;
    private Integer sortOrder;
}
