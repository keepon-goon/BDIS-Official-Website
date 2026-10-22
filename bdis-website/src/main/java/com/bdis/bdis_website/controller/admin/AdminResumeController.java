package com.bdis.bdis_website.controller.admin;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.service.ResumeService;
import com.bdis.bdis_website.vo.ResumeSettingsVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/resumes")
public class AdminResumeController {
    private final ResumeService service;

    public AdminResumeController(ResumeService service) {
        this.service = service;
    }

    @ModelAttribute
    public void noCache(jakarta.servlet.http.HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
    }

    @GetMapping("/settings")
    public Result<ResumeSettingsVO> settings() {
        return Result.success(service.settings());
    }

    @PutMapping("/settings")
    public Result<ResumeSettingsVO> updateSettings(@jakarta.validation.Valid @RequestBody com.bdis.bdis_website.dto.ResumeSettingsDTO form) {
        return Result.success(service.updateSettings(form));
    }
}
