package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resume_settings")
public class ResumeSettings {
    @TableId
    private Long id;
    private String sendMode;
    private String recipient;
    private LocalDateTime updatedAt;
}
