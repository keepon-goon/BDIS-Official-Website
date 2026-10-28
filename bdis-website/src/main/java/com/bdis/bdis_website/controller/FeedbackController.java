package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.service.FeedbackService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackService service;
    public FeedbackController(FeedbackService service) { this.service = service; }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Result<Map<String, Boolean>>> submit(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(defaultValue = "false") boolean replyRequested,
            @RequestParam(defaultValue = "false") boolean publicReply,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String replyEmail) {
        service.submit(category, content, images, replyRequested, publicReply, displayName, replyEmail);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Result.success(Map.of("received", true)));
    }

    @GetMapping("/qa")
    public ResponseEntity<Result<Map<String, Object>>> qa(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int pageSize) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Result.success(service.publicQa(page, pageSize)));
    }
}
