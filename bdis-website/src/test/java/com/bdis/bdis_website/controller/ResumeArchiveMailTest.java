package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.entity.ResumeSubmission;
import com.bdis.bdis_website.exception.ResumeException;
import com.bdis.bdis_website.mapper.ResumeMailLogMapper;
import com.bdis.bdis_website.mapper.ResumeSettingsMapper;
import com.bdis.bdis_website.mapper.ResumeSubmissionMapper;
import com.bdis.bdis_website.service.*;
import com.bdis.bdis_website.service.impl.ResumeServiceImpl;
import com.bdis.bdis_website.util.JwtUtil;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSendException;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:resume-archive-mail-tests;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "resume.storage-root=target/archive-mail-private-resumes"
})
@AutoConfigureMockMvc
class ResumeArchiveMailTest {
    @Autowired MockMvc mvc;
    @Autowired JwtUtil jwt;
    @Autowired ResumeSubmissionMapper submissions;
    @Autowired ResumeMailLogMapper logs;
    @Autowired ResumeSettingsMapper settings;
    @Autowired ResumeStorageService storage;
    @Autowired ResumeDocumentService documents;
    @Autowired ResumeService service;
    @Autowired ResumeServiceImpl implementation;
    @Autowired JdbcTemplate jdbc;
    @MockBean ResumeMailService mail;

    @BeforeEach
    void resetRecords() {
        logs.delete(null);
        submissions.delete(null);
        var rules = settings.selectById(1L);
        rules.setRecipient("qa@example.com");
        rules.setSendMode("MANUAL");
        settings.updateById(rules);
    }

    private String auth() { return "Bearer " + jwt.generateToken(1L, "test-admin"); }

    private ResumeSubmission savedRecord() throws Exception {
        String key = storage.hash(storage.newToken());
        var directory = storage.directory("submissions", key);
        Files.createDirectory(directory);
        Files.write(directory.resolve("audit.docx"), documents.blankTemplate());
        try (var pdf = new org.apache.pdfbox.pdmodel.PDDocument()) {
            pdf.addPage(new org.apache.pdfbox.pdmodel.PDPage());
            pdf.save(directory.resolve("audit.pdf").toFile());
        }
        var record = new ResumeSubmission();
        record.setName("归档日志测试");
        record.setStudentNo("ARCHIVE001");
        record.setDocumentName("audit");
        record.setWordKey(key + "/audit.docx");
        record.setPdfKey(key + "/audit.pdf");
        record.setSubmissionKey(key);
        record.setSubmitMailMode("AUTO");
        record.setMailStatus(0);
        record.setCreatedAt(LocalDateTime.now());
        submissions.insert(record);
        return record;
    }

    @Test
    void archivesAndRestoresWithoutChangingFilesOrMailResults() throws Exception {
        var first = savedRecord();
        savedRecord();
        first.setMailStatus(3);
        first.setMailLastError("原发送错误");
        submissions.updateById(first);
        byte[] word = storage.savedFile(first.getWordKey());
        byte[] pdf = storage.savedFile(first.getPdfKey());
        String path = "/api/admin/resumes/" + first.getId();
        mvc.perform(get("/api/admin/resumes").header("Authorization", auth()))
                .andExpect(jsonPath("$.data.total").value(2));
        mvc.perform(put(path + "/archive").header("Authorization", auth()).contentType("application/json").content("{\"archived\":true}"))
                .andExpect(status().isOk());
        mvc.perform(put(path + "/archive").header("Authorization", auth()).contentType("application/json").content("{\"archived\":true}"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/admin/resumes").header("Authorization", auth()))
                .andExpect(jsonPath("$.data.total").value(1)).andExpect(jsonPath("$.data.list[0].archived").value(false));
        mvc.perform(get("/api/admin/resumes").header("Authorization", auth()).param("archiveStatus", "ARCHIVED").param("mailStatus", "3"))
                .andExpect(jsonPath("$.data.total").value(1)).andExpect(jsonPath("$.data.list[0].id").value(first.getId().toString()));
        mvc.perform(get("/api/admin/resumes").header("Authorization", auth()).param("archiveStatus", "ALL"))
                .andExpect(jsonPath("$.data.total").value(2));
        mvc.perform(get(path + "/word").header("Authorization", auth())).andExpect(content().bytes(word));
        mvc.perform(get(path + "/pdf").header("Authorization", auth())).andExpect(content().bytes(pdf));
        assertThat(submissions.selectById(first.getId()).getMailStatus()).isEqualTo(3);
        assertThat(submissions.selectById(first.getId()).getMailLastError()).isEqualTo("原发送错误");
        mvc.perform(put(path + "/archive").header("Authorization", auth()).contentType("application/json").content("{\"archived\":false}"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/admin/resumes").header("Authorization", auth())).andExpect(jsonPath("$.data.total").value(2));
        verifyNoInteractions(mail);
    }

    @Test
    void protectsArchiveAndMailHistoryAndValidatesParameters() throws Exception {
        var record = savedRecord();
        String path = "/api/admin/resumes/" + record.getId();
        mvc.perform(put(path + "/archive").contentType("application/json").content("{\"archived\":true}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get(path + "/mail-logs")).andExpect(status().isUnauthorized());
        mvc.perform(put(path + "/archive").header("Authorization", auth()).contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/admin/resumes/999999/archive").header("Authorization", auth()).contentType("application/json").content("{\"archived\":true}"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/admin/resumes").header("Authorization", auth()).param("archiveStatus", "INVALID"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(path + "/mail-logs").header("Authorization", auth()).param("page", "0"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(path + "/mail-logs").header("Authorization", auth()).param("pageSize", "51"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/resumes/999999/mail-logs").header("Authorization", auth())).andExpect(status().isNotFound());
    }

    @Test
    void preservesFailureHistoryAfterSuccessfulManualResend() throws Exception {
        var record = savedRecord();
        var failure = new SMTPSendFailedException("DATA", 552, "552 message too large target@example.com password=secret", null, null, null, null);
        doThrow(new MailSendException("发送异常", failure)).when(mail).send(any(), anyString());
        String path = "/api/admin/resumes/" + record.getId();
        mvc.perform(post(path + "/email").header("Authorization", auth())).andExpect(status().isServiceUnavailable());
        doNothing().when(mail).send(any(), anyString());
        mvc.perform(post(path + "/email").header("Authorization", auth())).andExpect(status().isOk());
        mvc.perform(get(path + "/mail-logs").header("Authorization", auth()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.list[0].status").value("ACCEPTED"))
                .andExpect(jsonPath("$.data.list[0].triggerType").value("MANUAL"))
                .andExpect(jsonPath("$.data.list[1].status").value("FAILED"))
                .andExpect(jsonPath("$.data.list[1].smtpCode").value(552));
        var history = service.mailLogs(record.getId(), 1, 10);
        assertThat(history.list().get(1).errorMessage()).contains("message too large").doesNotContain("target@example.com", "secret");
        assertThat(history.list().get(0).durationMs()).isNotNegative();
        assertThat(submissions.selectById(record.getId()).getMailStatus()).isEqualTo(2);
        assertThat(submissions.selectById(record.getId()).getMailLastError()).isNull();
        assertThat(submissions.selectById(record.getId()).getMailSentAt()).isNotNull();
        mvc.perform(get(path + "/mail-logs").header("Authorization", auth()).param("page", "2").param("pageSize", "1"))
                .andExpect(jsonPath("$.data.list[0].status").value("FAILED"));
    }

    @Test
    void preventsParallelSendsAndCommitsAttemptBeforeSmtp() throws Exception {
        var record = savedRecord();
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        doAnswer(invocation -> {
            assertThat(org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
            entered.countDown();
            if (!release.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("测试发送等待超时");
            return null;
        }).when(mail).send(any(), anyString());
        var first = CompletableFuture.runAsync(() -> service.send(record.getId()));
        try {
            assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(logs.selectCount(null)).isEqualTo(1);
            assertThat(logs.selectList(null).get(0).getStatus()).isEqualTo("SENDING");
            assertThatThrownBy(() -> service.send(record.getId())).isInstanceOfSatisfying(ResumeException.class,
                    error -> assertThat(error.getStatus()).isEqualTo(409));
        } finally { release.countDown(); }
        first.get(5, TimeUnit.SECONDS);
        verify(mail, times(1)).send(any(), anyString());
        assertThat(logs.selectCount(null)).isEqualTo(1);
    }

    @Test
    void rollsBackClaimIfTheAttemptCannotBeRecorded() throws Exception {
        var record = savedRecord();
        jdbc.execute("ALTER TABLE resume_mail_log ADD CONSTRAINT ck_test_log_insert CHECK (1 = 0)");
        try {
            assertThatThrownBy(() -> service.send(record.getId())).isInstanceOfSatisfying(ResumeException.class,
                    error -> assertThat(error.getStatus()).isEqualTo(503));
            assertThat(submissions.selectById(record.getId()).getMailStatus()).isEqualTo(3);
            assertThat(submissions.selectById(record.getId()).getMailLastError()).contains("本次未发送");
            assertThat(logs.selectCount(null)).isZero();
            verifyNoInteractions(mail);
        } finally { jdbc.execute("ALTER TABLE resume_mail_log DROP CONSTRAINT ck_test_log_insert"); }
    }

    @Test
    void keepsUncertainResultIfCompletionCannotBePersistedAndRecoversOnRestart() throws Exception {
        var record = savedRecord();
        jdbc.execute("ALTER TABLE resume_mail_log ADD CONSTRAINT ck_test_log_update CHECK (status = 'SENDING')");
        try {
            assertThatThrownBy(() -> service.send(record.getId())).isInstanceOfSatisfying(ResumeException.class,
                    error -> assertThat(error.getStatus()).isEqualTo(503));
            assertThat(submissions.selectById(record.getId()).getMailStatus()).isEqualTo(1);
            assertThat(logs.selectList(null).get(0).getStatus()).isEqualTo("SENDING");
            verify(mail, times(1)).send(any(), anyString());
        } finally { jdbc.execute("ALTER TABLE resume_mail_log DROP CONSTRAINT ck_test_log_update"); }
        implementation.recoverUncertainMail();
        assertThat(submissions.selectById(record.getId()).getMailStatus()).isEqualTo(3);
        var uncertain = logs.selectList(null).get(0);
        assertThat(uncertain.getStatus()).isEqualTo("UNKNOWN");
        assertThat(uncertain.getErrorType()).isEqualTo("PROCESS_RESTART");
        assertThat(uncertain.getFinishedAt()).isNotNull();
    }
}
