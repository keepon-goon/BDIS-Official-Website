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

    @ModelAttribute
    public void noCache(jakarta.servlet.http.HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> template() {
        return download(documents.blankTemplate(), documents.blankFilename(), false);
    }

    @PostMapping("/previews")
    public Result<ResumePreviewVO> preview(@Valid @RequestBody ResumeFormDTO form) {
        return Result.success(service.preview(form));
    }

    @GetMapping("/previews/pdf")
    public ResponseEntity<byte[]> pdf(@RequestHeader(value = "X-Resume-Preview-Token", required = false) String token) {
        var file = service.previewFile(token, true);
        return download(file.bytes(), file.filename(), true);
    }

    @GetMapping("/previews/word")
    public ResponseEntity<byte[]> word(@RequestHeader(value = "X-Resume-Preview-Token", required = false) String token) {
        var file = service.previewFile(token, false);
        return download(file.bytes(), file.filename(), false);
    }

    @PostMapping(value = "/previews/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ResumePreviewVO> photo(@RequestHeader(value = "X-Resume-Preview-Token", required = false) String token,
                                         @RequestPart("photo") org.springframework.web.multipart.MultipartFile photo) {
        return Result.success(service.photo(token, photo));
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ResumePreviewVO> upload(@Valid @ModelAttribute com.bdis.bdis_website.dto.ResumeUploadDTO form,
                                          @RequestPart("resume") org.springframework.web.multipart.MultipartFile resume,
                                          @RequestHeader(value = "X-Resume-Preview-Token", required = false) String token) {
        return Result.success(service.upload(form, resume, token));
    }

    @PostMapping("/confirm")
    public Result<com.bdis.bdis_website.vo.ResumeSubmitVO> confirm(
            @RequestHeader(value = "X-Resume-Preview-Token", required = false) String token) {
        return Result.success(service.confirm(token));
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
