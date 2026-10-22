package com.bdis.bdis_website.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResumeFormDTO {
    @NotBlank(message = "请填写姓名") @Size(max = 30, message = "姓名最多30字")
    private String name;
    @Size(max = 10, message = "性别最多10字")
    private String gender;
    @Size(max = 30, message = "民族最多30字")
    private String ethnicity;
    @NotBlank(message = "请填写学院") @Size(max = 80, message = "学院最多80字")
    private String college;
    @NotBlank(message = "请填写学号") @Size(max = 40, message = "学号最多40字")
    private String studentNo;
    @Pattern(regexp = "^(\\d{4}-(0[1-9]|1[0-2]))?$", message = "出生年月格式应为YYYY-MM")
    private String birthMonth;
    @NotBlank(message = "请填写专业") @Size(max = 80, message = "专业最多80字")
    private String major;
    @NotBlank(message = "请填写联系电话") @Size(max = 30, message = "联系电话最多30字")
    private String phone;
    @Size(max = 120, message = "特点特长最多120字")
    private String specialties;
    @Size(max = 20, message = "绩点最多20字")
    private String gpa;
    @NotNull(message = "请选择是否参加过相关比赛")
    private Boolean hasCompetitionExperience;
    @NotBlank(message = "请填写个人简历") @Size(max = 800, message = "个人简历最多800字")
    private String intro;
    @Size(max = 500, message = "获奖情况最多500字")
    private String awards;
    @Size(max = 500, message = "个人风采最多500字")
    private String showcase;
}
