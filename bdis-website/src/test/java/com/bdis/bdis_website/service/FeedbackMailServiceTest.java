package com.bdis.bdis_website.service;

import com.bdis.bdis_website.entity.FeedbackImage;
import com.bdis.bdis_website.entity.FeedbackSubmission;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FeedbackMailServiceTest {
    @TempDir
    Path temporary;

    @Test
    void missingPasswordIsReportedAsNotConfigured() {
        var beans = new DefaultListableBeanFactory();
        beans.registerSingleton("mail", mock(JavaMailSender.class));
        var service = new FeedbackMailService(beans.getBeanProvider(JavaMailSender.class), "sender@example.com", "", "18862028557@163.com");
        assertThat(service.configured()).isFalse();
    }

    @Test
    void sendsNotificationEmailWithAttachmentsToConfiguredRecipient() throws Exception {
        var sender = mock(JavaMailSender.class);
        when(sender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));

        var beans = new DefaultListableBeanFactory();
        beans.registerSingleton("mail", sender);

        String recipient = "18862028557@163.com";
        var service = new FeedbackMailService(beans.getBeanProvider(JavaMailSender.class), "sender@163.com", "valid-auth-code", recipient);
        assertThat(service.configured()).isTrue();

        var submission = new FeedbackSubmission();
        submission.setId("test-uuid-1");
        submission.setCategory("join");
        submission.setContent("请问加入工作室的面试时间是什么时候？");
        submission.setCreatedAt(LocalDateTime.of(2026, 10, 28, 18, 30, 0));

        Path imagePath = temporary.resolve("img1.png");
        byte[] imageBytes = new byte[]{1, 2, 3, 4, 5};
        Files.write(imagePath, imageBytes);

        var image = new FeedbackImage();
        image.setId("img-1");
        image.setSubmissionId(submission.getId());
        image.setStorageKey("img1.png");
        image.setByteSize((long) imageBytes.length);

        service.sendNotification(submission, List.of(image), temporary);

        var capture = ArgumentCaptor.forClass(MimeMessage.class);
        verify(sender, times(1)).send(capture.capture());

        var message = capture.getValue();
        message.saveChanges();

        var output = new ByteArrayOutputStream();
        message.writeTo(output);
        message = new MimeMessage(Session.getInstance(new Properties()), new ByteArrayInputStream(output.toByteArray()));

        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(recipient);
        var from = (InternetAddress) message.getFrom()[0];
        assertThat(from.getAddress()).isEqualTo("sender@163.com");
        assertThat(from.getPersonal()).isEqualTo("BDIS官方网站");
        assertThat(message.getSubject()).contains("[BDIS匿名信箱]").contains("了解与加入");

        Multipart multipart = (Multipart) message.getContent();
        // Body part 0 is text, part 1 is image attachment
        assertThat(multipart.getCount()).isEqualTo(2);
        String contentText = extractText(message);
        assertThat(contentText).contains("请问加入工作室的面试时间是什么时候？");
        assertThat(contentText).contains("了解与加入");

        var attachmentPart = multipart.getBodyPart(1);
        assertThat(MimeUtility.decodeText(attachmentPart.getFileName())).isEqualTo("意见图片_1.png");
        assertThat(attachmentPart.getInputStream().readAllBytes()).isEqualTo(imageBytes);
    }

    private String extractText(Part part) throws Exception {
        if (part.isMimeType("text/*")) {
            return (String) part.getContent();
        }
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mp.getCount(); i++) {
                sb.append(extractText(mp.getBodyPart(i)));
            }
            return sb.toString();
        }
        return "";
    }
}
