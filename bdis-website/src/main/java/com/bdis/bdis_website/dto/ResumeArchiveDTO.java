package com.bdis.bdis_website.dto;

import jakarta.validation.constraints.NotNull;

public record ResumeArchiveDTO(@NotNull(message = "请提供归档状态") Boolean archived) {
}
