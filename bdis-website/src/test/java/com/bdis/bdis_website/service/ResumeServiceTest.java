package com.bdis.bdis_website.service;

import com.bdis.bdis_website.dto.ResumeFormDTO;
import com.bdis.bdis_website.entity.ResumeSubmission;
import com.bdis.bdis_website.mapper.ResumeSettingsMapper;
import com.bdis.bdis_website.mapper.ResumeSubmissionMapper;
import com.bdis.bdis_website.model.ResumePreviewSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.file.Files;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ResumeServiceTest {
    @Autowired private MockMvc mvc;
    @Autowired private ResumeStorageService storage;
    @Autowired private ResumeDocumentService documents;
    @Autowired private ObjectMapper json;
    @Autowired private ResumeSettingsMapper settings;
    @Autowired private ResumeSubmissionMapper submissions;
    @Autowired private ResumeService service;
    @Autowired private com.bdis.bdis_website.config.ResumeProperties properties;
    @MockBean private ResumeMailService mail;

    @BeforeEach
    void disableRealMail() throws Exception {
        doThrow(new com.bdis.bdis_website.exception.ResumeException(503, "邮件服务未配置，管理员可配置后补发"))
                .when(mail).send(any(ResumeSubmission.class), anyString());
    }

    private String preview() throws Exception {
        String token = storage.newToken();
        var snapshot = new ResumePreviewSnapshot();
        snapshot.setTokenHash(storage.hash(token));
        snapshot.setSourceType("FORM");
        snapshot.setForm(json.readValue(new ClassPathResource("resume-form.json").getInputStream(), ResumeFormDTO.class));
        snapshot.setDocumentName(documents.documentName("测试同学", "000020260001", "2026-2027"));
        snapshot.setAcademicYear("2026-2027");
        snapshot.setTemplateVersion("test");
        snapshot.setExpiresAt(Instant.now().plusSeconds(600));
        var work = storage.directory("work", snapshot.getTokenHash());
        Files.createDirectory(work);
        documents.generate(snapshot.getForm(), snapshot.getAcademicYear(), null, work.resolve(snapshot.getDocumentName() + ".docx"));
        try (var pdf = new PDDocument()) {
            pdf.addPage(new PDPage());
            pdf.save(work.resolve(snapshot.getDocumentName() + ".pdf").toFile());
        }
        storage.publish(work, snapshot);
        return token;
    }

    @Test
    void confirmationIsIdempotentAndPreservesPreviewBytes() throws Exception {
        var rules = settings.selectById(1L);
        rules.setSendMode("MANUAL");
        settings.updateById(rules);
        String token = preview();
        var snapshot = storage.load(token);
        byte[] expected = Files.readAllBytes(storage.directory("previews", snapshot.getTokenHash()).resolve(snapshot.getDocumentName() + ".docx"));
        long before = submissions.selectCount(null);
        String first = mvc.perform(post("/api/resume/confirm").header("X-Resume-Preview-Token", token)
                        .contentType("application/json").content("{\"name\":\"篡改姓名\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.mailStatus").value(0))
                .andReturn().getResponse().getContentAsString();
        String id = json.readTree(first).at("/data/submissionId").asText();
        mvc.perform(post("/api/resume/confirm").header("X-Resume-Preview-Token", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.submissionId").value(id));
        assertThat(submissions.selectCount(null)).isEqualTo(before + 1);
        ResumeSubmission record = submissions.selectById(id);
        assertThat(record.getName()).isEqualTo("测试同学");
        assertThat(storage.savedFile(record.getWordKey())).isEqualTo(expected);
        mvc.perform(get("/api/resume/previews/word").header("X-Resume-Preview-Token", token)).andExpect(status().isGone());
    }

    @Test
    void automaticMailFailureDoesNotRollbackSavedRecord() throws Exception {
        var rules = settings.selectById(1L);
        rules.setSendMode("AUTO");
        rules.setRecipient("");
        settings.updateById(rules);
        String token = preview();
        String body = mvc.perform(post("/api/resume/confirm").header("X-Resume-Preview-Token", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.mailStatus").value(3))
                .andReturn().getResponse().getContentAsString();
        String id = json.readTree(body).at("/data/submissionId").asText();
        var record = submissions.selectById(id);
        assertThat(storage.savedFile(record.getWordKey())).isNotEmpty();
        assertThat(record.getMailLastError()).contains("未配置");
        mvc.perform(post("/api/resume/confirm").header("X-Resume-Preview-Token", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.submissionId").value(id));
    }

    @Test
    void databaseConstraintFailureRollsBackAndPreservesPreview() throws Exception {
        String token = preview();
        var snapshot = storage.load(token);
        snapshot.getForm().setStudentNo("x".repeat(100));
        storage.writeSnapshot(storage.directory("previews", snapshot.getTokenHash()), snapshot);
        long before = submissions.selectCount(null);
        var directory = java.nio.file.Path.of(properties.getStorageRoot()).toAbsolutePath().resolve("submissions");
        long filesBefore;
        try (var stream = Files.list(directory)) { filesBefore = stream.count(); }
        mvc.perform(post("/api/resume/confirm").header("X-Resume-Preview-Token", token))
                .andExpect(status().isInternalServerError()).andExpect(jsonPath("$.code").value(1));
        assertThat(submissions.selectCount(null)).isEqualTo(before);
        assertThat(storage.load(token).isSuperseded()).isFalse();
        try (var stream = Files.list(directory)) { assertThat(stream.count()).isEqualTo(filesBefore); }
    }

    @Test
    void parallelConfirmationCreatesOneRecord() throws Exception {
        var rules = settings.selectById(1L);
        rules.setSendMode("MANUAL");
        settings.updateById(rules);
        String token = preview();
        long before = submissions.selectCount(null);
        var first = java.util.concurrent.CompletableFuture.supplyAsync(() -> service.confirm(token));
        var second = java.util.concurrent.CompletableFuture.supplyAsync(() -> service.confirm(token));
        assertThat(first.get().submissionId()).isEqualTo(second.get().submissionId());
        assertThat(submissions.selectCount(null)).isEqualTo(before + 1);
    }

    @Test
    void automaticSendRunsOnceAfterDatabaseCommit() throws Exception {
        var rules = settings.selectById(1L);
        rules.setSendMode("AUTO");
        rules.setRecipient("test@example.com");
        settings.updateById(rules);
        String token = preview();
        var snapshot = storage.load(token);
        byte[] expected = Files.readAllBytes(storage.directory("previews", snapshot.getTokenHash()).resolve(snapshot.getDocumentName() + ".docx"));
        doAnswer(invocation -> {
            ResumeSubmission record = invocation.getArgument(0);
            assertThat(java.util.concurrent.CompletableFuture.supplyAsync(() -> submissions.selectById(record.getId()))
                    .get(2, java.util.concurrent.TimeUnit.SECONDS)).isNotNull();
            assertThat(storage.savedFile(record.getWordKey())).isEqualTo(expected);
            return null;
        }).when(mail).send(any(ResumeSubmission.class), eq("test@example.com"));
        mvc.perform(post("/api/resume/confirm").header("X-Resume-Preview-Token", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.mailStatus").value(2));
        mvc.perform(post("/api/resume/confirm").header("X-Resume-Preview-Token", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.mailStatus").value(2));
        verify(mail, times(1)).send(any(ResumeSubmission.class), eq("test@example.com"));
    }
}
