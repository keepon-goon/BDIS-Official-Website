package com.bdis.bdis_website.service.impl;

import com.bdis.bdis_website.config.ResumeProperties;
import com.bdis.bdis_website.dto.ResumeFormDTO;
import com.bdis.bdis_website.model.ResumePreviewSnapshot;
import com.bdis.bdis_website.service.*;
import com.bdis.bdis_website.vo.ResumePreviewVO;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import com.bdis.bdis_website.exception.ResumeException;
import java.io.IOException;

@Service
public class ResumeServiceImpl implements ResumeService {
    private final ResumeStorageService storage;
    private final ResumeDocumentService documents;
    private final ResumeConversionService conversions;
    private final ResumeProperties properties;
    private final Clock clock;
    private final ResumePhotoService photos;
    private final Object stateLock = new Object();
    private final java.util.Set<String> replacing = new java.util.HashSet<>();

    public ResumeServiceImpl(ResumeStorageService storage, ResumeDocumentService documents,
                             ResumeConversionService conversions, ResumeProperties properties, Clock resumeClock,
                             ResumePhotoService photos) {
        this.storage = storage;
        this.documents = documents;
        this.conversions = conversions;
        this.properties = properties;
        this.clock = resumeClock;
        this.photos = photos;
    }

    @Override
    public ResumePreviewVO preview(ResumeFormDTO form) {
        return conversions.execute(() -> {
            String token = storage.newToken();
            String hash = storage.hash(token);
            Path work = storage.directory("work", hash);
            Files.createDirectory(work);
            try {
                ResumePreviewSnapshot snapshot = new ResumePreviewSnapshot();
                snapshot.setTokenHash(hash);
                snapshot.setSourceType("FORM");
                snapshot.setForm(form);
                snapshot.setAcademicYear(documents.currentAcademicYear());
                snapshot.setDocumentName(documents.documentName(form.getName(), form.getStudentNo(), snapshot.getAcademicYear()));
                snapshot.setTemplateVersion(properties.getTemplateVersion());
                snapshot.setExpiresAt(clock.instant().plusSeconds(properties.getPreviewMinutes() * 60L));
                Path word = storage.file(work, snapshot.getDocumentName() + ".docx");
                documents.generate(form, snapshot.getAcademicYear(), null, word);
                conversions.convert(word);
                storage.publish(work, snapshot);
                return view(token, snapshot);
            } finally {
                storage.deleteTemporary(work);
            }
        });
    }

    @Override
    public ResumeFile previewFile(String token, boolean pdf) {
        var snapshot = storage.load(token);
        String filename = snapshot.getDocumentName() + (pdf ? ".pdf" : ".docx");
        try {
            Path path = storage.file(storage.directory("previews", snapshot.getTokenHash()), filename);
            if (!Files.isRegularFile(path)) throw new ResumeException(404, "预览文件不存在，请重新生成");
            return new ResumeFile(Files.readAllBytes(path), filename);
        } catch (IOException e) {
            throw new ResumeException(503, "预览文件暂不可读，请稍后重试");
        }
    }

    @Override
    public ResumePreviewVO photo(String token, org.springframework.web.multipart.MultipartFile photo) {
        if (photo.isEmpty()) throw new ResumeException(400, "请选择照片");
        if (photo.getSize() > ResumePhotoService.MAX_BYTES) throw new ResumeException(413, "单张照片不能超过5MB");
        String oldHash = storage.hash(token);
        ResumePreviewSnapshot original;
        synchronized (stateLock) {
            original = storage.load(token);
            if (!"FORM".equals(original.getSourceType())) throw new ResumeException(400, "请在本地修改上传的Word后重新上传");
            if (!replacing.add(oldHash)) throw new ResumeException(409, "正在更新照片，请稍后操作");
        }
        try {
            return conversions.execute(() -> {
                String nextToken = storage.newToken();
                String nextHash = storage.hash(nextToken);
                Path work = storage.directory("work", nextHash);
                Files.createDirectory(work);
                boolean replaced = false;
                try {
                    byte[] normalized = photos.normalize(photo);
                    var snapshot = new ResumePreviewSnapshot();
                    snapshot.setTokenHash(nextHash);
                    snapshot.setSourceType("FORM");
                    snapshot.setForm(original.getForm());
                    snapshot.setAcademicYear(original.getAcademicYear());
                    snapshot.setDocumentName(original.getDocumentName());
                    snapshot.setTemplateVersion(original.getTemplateVersion());
                    snapshot.setExpiresAt(clock.instant().plusSeconds(properties.getPreviewMinutes() * 60L));
                    snapshot.setHasPhoto(true);
                    Files.write(work.resolve("photo.png"), normalized);
                    Path word = storage.file(work, snapshot.getDocumentName() + ".docx");
                    documents.generate(snapshot.getForm(), snapshot.getAcademicYear(), normalized, word);
                    conversions.convert(word);
                    if (Thread.currentThread().isInterrupted()) throw new ResumeException(503, "照片合成已取消，请重试");
                    synchronized (stateLock) {
                        var active = storage.load(token);
                        storage.publish(work, snapshot);
                        active.setSuperseded(true);
                        storage.writeSnapshot(storage.directory("previews", oldHash), active);
                        replaced = true;
                    }
                    return view(nextToken, snapshot);
                } finally {
                    storage.deleteTemporary(work);
                    if (!replaced) storage.deleteTemporary(storage.directory("previews", nextHash));
                }
            });
        } finally {
            synchronized (stateLock) { replacing.remove(oldHash); }
        }
    }

    private ResumePreviewVO view(String token, ResumePreviewSnapshot snapshot) {
        return new ResumePreviewVO(token, snapshot.getExpiresAt(), snapshot.getTemplateVersion(), snapshot.getSourceType(),
                "FORM".equals(snapshot.getSourceType()) ? snapshot.isHasPhoto() : null,
                snapshot.getDocumentName() + ".docx", snapshot.getDocumentName() + ".pdf");
    }
}
