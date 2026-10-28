package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedback_submission")
public class FeedbackSubmission {
    @TableId(type = IdType.INPUT)
    private String id;
    private String category;
    private String content;
    private String status;
    private Boolean replyRequested;
    private Boolean publicReply;
    private String displayName;
    private String replyEmail;
    private String replyContent;
    private Boolean published;
    private LocalDateTime repliedAt;
    private LocalDateTime replyEmailedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
