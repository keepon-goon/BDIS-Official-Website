package com.bdis.bdis_website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeUploadDTO {
    @NotBlank(message = "请填写姓名") @Size(max = 30, message = "姓名最多30字")
    private String name;
    @NotBlank(message = "请填写学号") @Size(max = 40, message = "学号最多40字")
    private String studentNo;
    @NotBlank(message = "请填写联系电话") @Size(max = 30, message = "联系电话最多30字")
    private String phone;
}
