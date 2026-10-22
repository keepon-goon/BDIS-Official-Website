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

@Service
public class ResumeServiceImpl implements ResumeService {
    private final ResumeStorageService storage;
    private final ResumeDocumentService documents;
    private final ResumeConversionService conversions;
    private final ResumeProperties properties;
    private final Clock clock;

    public ResumeServiceImpl(ResumeStorageService storage, ResumeDocumentService documents,
                             ResumeConversionService conversions, ResumeProperties properties, Clock resumeClock) {
        this.storage = storage;
        this.documents = documents;
        this.conversions = conversions;
        this.properties = properties;
        this.clock = resumeClock;
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

    private ResumePreviewVO view(String token, ResumePreviewSnapshot snapshot) {
        return new ResumePreviewVO(token, snapshot.getExpiresAt(), snapshot.getTemplateVersion(), snapshot.getSourceType(),
                "FORM".equals(snapshot.getSourceType()) ? snapshot.isHasPhoto() : null,
                snapshot.getDocumentName() + ".docx", snapshot.getDocumentName() + ".pdf");
    }
}
