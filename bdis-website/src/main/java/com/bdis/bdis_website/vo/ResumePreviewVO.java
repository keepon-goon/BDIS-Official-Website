package com.bdis.bdis_website.vo;

import java.time.Instant;

public record ResumePreviewVO(String previewToken, Instant expiresAt, String templateVersion,
                              String sourceType, Boolean hasPhoto, String wordFilename, String pdfFilename) {
}
