package com.bdis.bdis_website.service;

import com.bdis.bdis_website.dto.ResumeFormDTO;
import com.bdis.bdis_website.vo.ResumePreviewVO;

public interface ResumeService {
    ResumePreviewVO preview(ResumeFormDTO form);
    ResumeFile previewFile(String token, boolean pdf);
    ResumePreviewVO photo(String token, org.springframework.web.multipart.MultipartFile photo);
    ResumePreviewVO upload(com.bdis.bdis_website.dto.ResumeUploadDTO form,
                           org.springframework.web.multipart.MultipartFile word, String sourceToken);
    com.bdis.bdis_website.vo.ResumeSubmitVO confirm(String token);
    com.bdis.bdis_website.vo.ResumeSettingsVO settings();
    com.bdis.bdis_website.vo.ResumeSettingsVO updateSettings(com.bdis.bdis_website.dto.ResumeSettingsDTO form);
    PageResult list(int page, int pageSize, String name, String studentNo, Integer mailStatus);
    com.bdis.bdis_website.vo.ResumeAdminVO detail(long id);

    record PageResult(java.util.List<com.bdis.bdis_website.vo.ResumeAdminVO> list, long total, int page, int pageSize) { }

    record ResumeFile(byte[] bytes, String filename) { }
}
