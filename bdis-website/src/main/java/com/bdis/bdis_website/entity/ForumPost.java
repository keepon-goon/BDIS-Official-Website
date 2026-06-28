package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("forum_post")
public class ForumPost {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String title;
    private String summary;
    private String recruitRoles;
    private Integer currentSize;
    private Integer targetSize;
    private String type;
    private String value;
    private String deadline;
    private String coverUrl;
    private String link;
    @TableField("sort_order")
    private Integer sortOrder;

    @TableField(exist = false)
    private String[] recruitRolesArray;
}
