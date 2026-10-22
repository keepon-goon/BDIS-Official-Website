package com.bdis.bdis_website.service;

import com.bdis.bdis_website.config.ResumeProperties;
import com.bdis.bdis_website.entity.ResumeSubmission;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mail.javamail.JavaMailSender;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ResumeMailServiceTest {
    @TempDir Path temporary;

    @Test
    void buildsMailFromPersistedWordWithoutOpeningSmtpConnection() throws Exception {
        var properties = new ResumeProperties();
        properties.setStorageRoot(temporary.resolve("private").toString());
        var storage = new ResumeStorageService(properties, new ObjectMapper(), Clock.systemUTC(), temporary.resolve("public").toString());
        String key = storage.hash(storage.newToken());
        var directory = storage.directory("submissions", key);
        Files.createDirectory(directory);
        byte[] original = new byte[]{80, 75, 3, 4, 9, 8, 7};
        Files.write(directory.resolve("报名表.docx"), original);
        var sender = mock(JavaMailSender.class);
        when(sender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));
        var beans = new DefaultListableBeanFactory();
        beans.registerSingleton("mail", sender);
        var service = new ResumeMailService(beans.getBeanProvider(JavaMailSender.class), "sender@example.com", storage);
        var record = new ResumeSubmission();
        record.setName("测试同学");
        record.setStudentNo("000001");
        record.setAcademicYear("2026-2027");
        record.setDocumentName("测试同学-000001-2026学年-2027学年大数据智能工作室招新报名表");
        record.setWordKey(key + "/报名表.docx");
        service.send(record, "target@example.com");
        var capture = ArgumentCaptor.forClass(MimeMessage.class);
        verify(sender, times(1)).send(capture.capture());
        var message = capture.getValue();
        message.saveChanges();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("target@example.com");
        Multipart multipart = (Multipart) message.getContent();
        var attachment = multipart.getBodyPart(1);
        assertThat(attachment.getFileName()).isEqualTo(record.getDocumentName() + ".docx");
        assertThat(attachment.getInputStream().readAllBytes()).isEqualTo(original);
    }
}
