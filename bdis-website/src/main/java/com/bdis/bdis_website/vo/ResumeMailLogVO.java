package com.bdis.bdis_website.vo;

import java.time.LocalDateTime;

public record ResumeMailLogVO(String id, String triggerType, String recipient, String status,
                              LocalDateTime startedAt, LocalDateTime finishedAt, Long durationMs,
                              String errorType, Integer smtpCode, String errorMessage) {
}
