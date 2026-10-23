package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.exception.ResumeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.MissingServletRequestParameterException;

@RestControllerAdvice(assignableTypes = {ResumeController.class, com.bdis.bdis_website.controller.admin.AdminResumeController.class})
public class ResumeExceptionHandler {
    @ExceptionHandler(ResumeException.class)
    public ResponseEntity<Result<Void>> business(ResumeException e) {
        return ResponseEntity.status(e.getStatus()).body(Result.fail(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> validation(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Result.fail(e.getBindingResult().getFieldErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestPartException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<Result<Void>> invalid(Exception e) {
        return ResponseEntity.badRequest().body(Result.fail("请求信息不完整或格式不正确，请检查后重试"));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> tooLarge() {
        return ResponseEntity.status(413).body(Result.fail("上传文件过大：照片最多10MB，Word最多50MB"));
    }
}
