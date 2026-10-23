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
    private String gender;
    private String ethnicity;
    private String college;
    private String studentNo;
    private String birthMonth;
    private String specialties;
    private String gpa;
    private Boolean hasCompetitionExperience;
    private String awards;
    private String showcase;
    private String academicYear;
    private String documentName;
    private String templateVersion;
    private String submissionSource;
    private String submissionKey;
    private String wordKey;
    private String pdfKey;
    private Integer mailStatus;
    private String submitMailMode;
    private String mailRecipient;
    private LocalDateTime mailSentAt;
    private String mailLastError;
}
