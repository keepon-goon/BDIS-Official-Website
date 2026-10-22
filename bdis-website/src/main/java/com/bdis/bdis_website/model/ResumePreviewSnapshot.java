package com.bdis.bdis_website.model;

import com.bdis.bdis_website.dto.ResumeFormDTO;
import lombok.Data;
import java.time.Instant;

@Data
public class ResumePreviewSnapshot {
    private String tokenHash;
    private String sourceType;
    private ResumeFormDTO form;
    private String academicYear;
    private String documentName;
    private String templateVersion;
    private Instant expiresAt;
    private boolean hasPhoto;
    private boolean superseded;
}
