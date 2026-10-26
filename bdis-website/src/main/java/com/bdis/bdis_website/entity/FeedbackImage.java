package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("feedback_image")
public class FeedbackImage {
    @TableId(type = IdType.INPUT)
    private String id;
    private String submissionId;
    private String storageKey;
    private Long byteSize;
}
