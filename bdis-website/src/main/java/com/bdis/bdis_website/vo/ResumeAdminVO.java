package com.bdis.bdis_website.vo;

import com.bdis.bdis_website.dto.ResumeFormDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResumeAdminVO extends ResumeFormDTO {
    private String id;
    private String academicYear;
    private String documentName;
    private String templateVersion;
    private String submissionSource;
    private LocalDateTime createdAt;
    private Integer mailStatus;
    private String submitMailMode;
    private String mailRecipient;
    private LocalDateTime mailSentAt;
    private String mailLastError;
    private boolean fileAvailable;
}
