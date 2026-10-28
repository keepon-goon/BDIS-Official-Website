package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("lab_info") // 数据库表名
public class LabInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String labName; // 数据库字段lab_name，MyBatis-Plus自动驼峰转换
    private String labDesc;
    // 其他字段...
}
