package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resume_mail_log")
public class ResumeMailLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long submissionId;
    private String triggerType;
    private String recipient;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long durationMs;
    private String errorType;
    private Integer smtpCode;
    private String errorMessage;
}
