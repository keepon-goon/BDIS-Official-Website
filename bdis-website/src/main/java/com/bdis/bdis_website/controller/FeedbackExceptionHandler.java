package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.controller.admin.AdminFeedbackController;
import com.bdis.bdis_website.exception.FeedbackException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = {FeedbackController.class, AdminFeedbackController.class})
public class FeedbackExceptionHandler {
    @ExceptionHandler(FeedbackException.class)
    public ResponseEntity<Result<Void>> feedback(FeedbackException error) {
        return ResponseEntity.status(error.getStatus()).header("Cache-Control", "no-store").body(Result.fail(error.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Result<Void>> invalid(Exception error) {
        return ResponseEntity.badRequest().body(Result.fail("请求参数无效，请检查后重试"));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> oversized(MaxUploadSizeExceededException error) {
        return ResponseEntity.status(413).body(Result.fail("图片总大小超出限制，请减少图片后重试"));
    }
}
