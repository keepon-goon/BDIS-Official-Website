package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("lab_info")
public class LabInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("lab_name")
    private String name;
    private String labDesc;
    private String nameEn;
    private String introShort;
    private String address;
    private String email;
    private String qqGroup;
    private String logoUrl;
    private String picture;
}
