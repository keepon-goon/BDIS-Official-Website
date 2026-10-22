package com.bdis.bdis_website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResumeUploadDTO {
    @NotBlank(message = "请填写姓名") @Size(max = 30, message = "姓名最多30字")
    private String name;
    @NotBlank(message = "请填写学号") @Size(max = 40, message = "学号最多40字")
    private String studentNo;
    @NotBlank(message = "请填写联系电话") @Size(max = 30, message = "联系电话最多30字")
    @Pattern(regexp = "^(?:\\+?86[- ]?)?1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}(?:-\\d{1,6})?$", message = "请填写有效的手机号或带区号的固定电话")
    private String phone;
}
