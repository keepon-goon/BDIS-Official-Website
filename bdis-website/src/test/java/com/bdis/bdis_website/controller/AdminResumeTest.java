package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminResumeTest {
    @Autowired private MockMvc mvc;
    @Autowired private JwtUtil jwt;
    @Autowired private com.bdis.bdis_website.mapper.ResumeSubmissionMapper submissions;
    @Autowired private com.bdis.bdis_website.service.ResumeStorageService storage;
    @Autowired private com.bdis.bdis_website.service.ResumeDocumentService documents;

    private String auth() { return "Bearer " + jwt.generateToken(1L, "resume-test"); }

    @Test
    void settingsRequiresAdminAndDoesNotExposeCredentials() throws Exception {
        mvc.perform(get("/api/admin/resumes/settings")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/resumes/settings").header("Authorization", auth()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.sendMode").exists())
                .andExpect(jsonPath("$.data.mailConfigured").value(false))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(header().string("Cache-Control", "no-store"));
    }

    @Test
    void updatesMailModeAndRecipientWithoutSendingAnything() throws Exception {
        mvc.perform(put("/api/admin/resumes/settings").contentType("application/json")
                .content("{\"sendMode\":\"MANUAL\",\"recipient\":\"test@example.com\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(put("/api/admin/resumes/settings").header("Authorization", auth()).contentType("application/json")
                .content("{\"sendMode\":\"MANUAL\",\"recipient\":\"test@example.com\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.sendMode").value("MANUAL"));
        mvc.perform(get("/api/admin/resumes/settings").header("Authorization", auth()))
                .andExpect(jsonPath("$.data.recipient").value("test@example.com"));
        mvc.perform(put("/api/admin/resumes/settings").header("Authorization", auth()).contentType("application/json")
                .content("{\"sendMode\":\"INVALID\",\"recipient\":\"bad\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listsFilteredRecordsWithoutPrivateKeys() throws Exception {
        var record = new com.bdis.bdis_website.entity.ResumeSubmission();
        record.setName("后台列表测试");
        record.setStudentNo("00000999");
        record.setMailStatus(0);
        record.setCreatedAt(java.time.LocalDateTime.now());
        record.setSubmissionKey("a".repeat(64));
        submissions.insert(record);
        mvc.perform(get("/api/admin/resumes")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/resumes").header("Authorization", auth()).param("studentNo", "00000999"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].id").value(record.getId().toString()))
                .andExpect(jsonPath("$.data.list[0].fileAvailable").value(false))
                .andExpect(jsonPath("$.data.list[0].submissionKey").doesNotExist())
                .andExpect(jsonPath("$.data.list[0].wordKey").doesNotExist());
        mvc.perform(get("/api/admin/resumes").header("Authorization", auth()).param("pageSize", "999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detailsRequireAdminAndDoNotExposePaths() throws Exception {
        var record = new com.bdis.bdis_website.entity.ResumeSubmission();
        record.setName("详情测试");
        record.setStudentNo("000001");
        record.setIntro("测试正文");
        record.setSubmissionKey("b".repeat(64));
        submissions.insert(record);
        mvc.perform(get("/api/admin/resumes/" + record.getId())).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/resumes/" + record.getId()).header("Authorization", auth()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.intro").value("测试正文"))
                .andExpect(jsonPath("$.data.id").value(record.getId().toString()))
                .andExpect(jsonPath("$.data.submissionKey").doesNotExist())
                .andExpect(jsonPath("$.data.wordKey").doesNotExist());
        mvc.perform(get("/api/admin/resumes/99999999").header("Authorization", auth())).andExpect(status().isNotFound());
    }

    private com.bdis.bdis_website.entity.ResumeSubmission savedRecord() throws Exception {
        String hash = storage.hash(storage.newToken());
        var record = new com.bdis.bdis_website.entity.ResumeSubmission();
        record.setName("后台测试");
        record.setStudentNo("000010");
        record.setAcademicYear(documents.currentAcademicYear());
        record.setDocumentName(documents.documentName(record.getName(), record.getStudentNo(), record.getAcademicYear()));
        record.setSubmissionKey(hash);
        record.setMailStatus(0);
        record.setCreatedAt(java.time.LocalDateTime.now());
        var directory = storage.directory("submissions", hash);
        java.nio.file.Files.createDirectory(directory);
        java.nio.file.Files.write(directory.resolve(record.getDocumentName() + ".docx"), documents.blankTemplate());
        try (var pdf = new org.apache.pdfbox.pdmodel.PDDocument()) {
            pdf.addPage(new org.apache.pdfbox.pdmodel.PDPage());
            pdf.save(directory.resolve(record.getDocumentName() + ".pdf").toFile());
        }
        record.setWordKey(hash + "/" + record.getDocumentName() + ".docx");
        record.setPdfKey(hash + "/" + record.getDocumentName() + ".pdf");
        submissions.insert(record);
        return record;
    }

    @Test
    void savedPdfRequiresAdminAndUsesPersistentFile() throws Exception {
        var record = savedRecord();
        mvc.perform(get("/api/admin/resumes/" + record.getId() + "/pdf")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/resumes/" + record.getId() + "/pdf").header("Authorization", auth()))
                .andExpect(status().isOk()).andExpect(content().contentType("application/pdf"))
                .andExpect(content().bytes(storage.savedFile(record.getPdfKey())));
    }
}
