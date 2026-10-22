package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.model.ResumePreviewSnapshot;
import com.bdis.bdis_website.service.ResumeStorageService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ResumeAccessTest {
    @Autowired private MockMvc mvc;
    @Autowired private ResumeStorageService storage;
    private String token;
    private ResumePreviewSnapshot snapshot;

    @BeforeEach
    void createPrivatePreview() throws Exception {
        token = storage.newToken();
        snapshot = new ResumePreviewSnapshot();
        snapshot.setTokenHash(storage.hash(token));
        snapshot.setDocumentName("测试-001-2026学年-2027学年大数据智能工作室招新报名表");
        snapshot.setExpiresAt(Instant.now().plusSeconds(600));
        Path work = storage.directory("work", snapshot.getTokenHash());
        Files.createDirectory(work);
        try (var pdf = new PDDocument()) {
            pdf.addPage(new PDPage());
            pdf.save(work.resolve(snapshot.getDocumentName() + ".pdf").toFile());
        }
        Files.write(work.resolve(snapshot.getDocumentName() + ".docx"), new byte[]{80, 75, 3, 4});
        storage.publish(work, snapshot);
    }

    @Test
    void pdfRequiresTokenAndReturnsJsonErrors() throws Exception {
        mvc.perform(get("/api/resume/previews/pdf")).andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
        mvc.perform(get("/api/resume/previews/pdf").header("X-Resume-Preview-Token", "invalid"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(1));
        mvc.perform(get("/api/resume/previews/pdf").header("X-Resume-Preview-Token", token))
                .andExpect(status().isOk()).andExpect(content().contentType("application/pdf"))
                .andExpect(header().doesNotExist("Content-Disposition"))
                .andExpect(header().exists("X-Resume-Filename"))
                .andExpect(header().string("Cache-Control", "no-store"));
    }

    @Test
    void expiredPdfCannotBeRead() throws Exception {
        snapshot.setExpiresAt(Instant.now().minusSeconds(1));
        storage.writeSnapshot(storage.directory("previews", snapshot.getTokenHash()), snapshot);
        mvc.perform(get("/api/resume/previews/pdf").header("X-Resume-Preview-Token", token))
                .andExpect(status().isGone()).andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void wordDownloadsExactPrivateBytesAndRejectsInvalidToken() throws Exception {
        mvc.perform(get("/api/resume/previews/word")).andExpect(status().isNotFound());
        mvc.perform(get("/api/resume/previews/word").header("X-Resume-Preview-Token", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .andExpect(content().bytes(new byte[]{80, 75, 3, 4}))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Cache-Control", "no-store"));
    }
}
