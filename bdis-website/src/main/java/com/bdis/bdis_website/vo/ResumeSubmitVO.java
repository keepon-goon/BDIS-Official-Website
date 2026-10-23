package com.bdis.bdis_website.vo;

import java.time.OffsetDateTime;

public record ResumeSubmitVO(String submissionId, OffsetDateTime savedAt, int mailStatus, String message) {
}
