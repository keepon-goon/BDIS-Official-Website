package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("resume_submission")
public class ResumeSubmission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String grade;
    private String major;
    private String phone;
    private String email;
    private String direction;
    private String intro;
    private String resumeUrl;
    private Integer status;
    private LocalDateTime createdAt;
}
