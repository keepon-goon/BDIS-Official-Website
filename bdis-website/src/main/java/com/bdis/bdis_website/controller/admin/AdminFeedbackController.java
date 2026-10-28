package com.bdis.bdis_website.controller.admin;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.service.FeedbackService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/feedback")
public class AdminFeedbackController {
    private final FeedbackService service;
    public AdminFeedbackController(FeedbackService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<Result<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status, @RequestParam(required = false) String category) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Result.success(service.list(page, pageSize, status, category)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<FeedbackService.FeedbackView>> detail(@PathVariable String id) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Result.success(service.detail(id)));
    }

    @PutMapping("/{id}/status")
    public Result<Void> update(@PathVariable String id, @RequestBody StatusUpdate body) {
        service.updateStatus(id, body.status());
        return Result.success();
    }

    @GetMapping("/{id}/images/{imageId}")
    public ResponseEntity<byte[]> image(@PathVariable String id, @PathVariable String imageId) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff").body(service.image(id, imageId));
    }

    @PutMapping("/{id}/reply")
    public Result<FeedbackService.FeedbackView> reply(@PathVariable String id, @RequestBody ReplyUpdate body) {
        service.saveReply(id, body.replyContent(), body.published());
        return Result.success(service.detail(id));
    }

    @PostMapping("/{id}/reply/email")
    public Result<FeedbackService.FeedbackView> emailReply(@PathVariable String id) {
        service.sendReplyEmail(id);
        return Result.success(service.detail(id));
    }

    public record StatusUpdate(String status) {}
    public record ReplyUpdate(String replyContent, boolean published) {}
}
