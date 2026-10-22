package com.bdis.bdis_website.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "resume")
public class ResumeProperties {
    private String storageRoot = "./private-resumes";
    private String template = "classpath:templates/resume-blank.docx";
    private String templateVersion = "20261022-v1";
    private String converter = "native";
    private String libreofficePath = "soffice";
    private String dockerPath = "docker";
    private String dockerImage = "bdis-docx-tools:ubuntu24.04";
    private int conversionTimeoutSeconds = 30;
    private int requestTimeoutSeconds = 120;
    private int previewMinutes = 30;
}
