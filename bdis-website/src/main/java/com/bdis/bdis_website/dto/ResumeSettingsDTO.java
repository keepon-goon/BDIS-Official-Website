package com.bdis.bdis_website.dto;

import jakarta.validation.constraints.*;

public record ResumeSettingsDTO(
        @NotBlank(message = "请选择邮件模式") @Pattern(regexp = "AUTO|MANUAL", message = "邮件模式只能为AUTO或MANUAL") String sendMode,
        @NotNull(message = "请提供目标邮箱字段，未设置时可留空") @Email(message = "目标邮箱格式不正确")
        @Size(max = 254, message = "目标邮箱过长") String recipient) {
}
