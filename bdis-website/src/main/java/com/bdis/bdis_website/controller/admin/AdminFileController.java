package com.bdis.bdis_website.controller.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/uploads")
public class AdminFileController {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        if (filename.contains("/") || filename.contains("\\")) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path root = Path.of(uploadPath).toRealPath();
            Path file = root.resolve(filename).normalize();
            if (!file.startsWith(root) || !Files.isRegularFile(file)) {
                return ResponseEntity.notFound().build();
            }
            Path realFile = file.toRealPath();
            if (!realFile.startsWith(root)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(realFile);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, getContentType(filename))
                    .header("X-Content-Type-Options", "nosniff")
                    .body(resource);
        } catch (IOException | InvalidPathException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String getContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }
}
