package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.dto.ResumeFormDTO;
import com.bdis.bdis_website.service.ResumeDocumentService;
import com.bdis.bdis_website.service.ResumeService;
import com.bdis.bdis_website.vo.ResumePreviewVO;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {
    private final ResumeDocumentService documents;
    private final ResumeService service;

    public ResumeController(ResumeDocumentService documents, ResumeService service) {
        this.documents = documents;
        this.service = service;
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> template() {
        return download(documents.blankTemplate(), documents.blankFilename(), false);
    }

    @PostMapping("/previews")
    public Result<ResumePreviewVO> preview(@Valid @RequestBody ResumeFormDTO form) {
        return Result.success(service.preview(form));
    }

    public static ResponseEntity<byte[]> download(byte[] bytes, String filename, boolean pdf) {
        return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.CONTENT_DISPOSITION, (pdf ? ContentDisposition.inline() : ContentDisposition.attachment())
                        .filename(filename, StandardCharsets.UTF_8).build().toString())
                .contentType(MediaType.parseMediaType(pdf ? "application/pdf" :
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(bytes);
    }
}
