package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("education_resource")
public class EducationResource {

    private Long id;

    private String title;

    private String description;

    private Integer sortOrder;

    private String category;

    @TableField("`link`")
    private String link;
}
