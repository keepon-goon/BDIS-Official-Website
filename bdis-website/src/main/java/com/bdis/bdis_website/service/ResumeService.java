package com.bdis.bdis_website.service;

import com.bdis.bdis_website.dto.ResumeFormDTO;
import com.bdis.bdis_website.vo.ResumePreviewVO;

public interface ResumeService {
    ResumePreviewVO preview(ResumeFormDTO form);
    ResumeFile previewFile(String token, boolean pdf);

    record ResumeFile(byte[] bytes, String filename) { }
}
