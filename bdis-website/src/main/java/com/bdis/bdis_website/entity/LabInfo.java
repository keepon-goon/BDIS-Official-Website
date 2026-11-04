package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(max = 16000)
    private String introShort;
    @Size(max = 16000)
    private String joinIntro;
    @Size(max = 200)
    private String address;
    @Email
    @Size(max = 100)
    private String email;
    @Pattern(regexp = "[0-9]*")
    @Size(max = 50)
    private String qqGroup;
    private String logoUrl;
    private String picture;
}
