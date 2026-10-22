package com.bdis.bdis_website.service;

import com.bdis.bdis_website.entity.ResumeSubmission;
import com.bdis.bdis_website.exception.ResumeException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ResumeMailService {
    private final ObjectProvider<JavaMailSender> sender;
    private final String from;
    private final String password;
    private final ResumeStorageService storage;

    public ResumeMailService(ObjectProvider<JavaMailSender> sender,
                              @Value("${spring.mail.username:}") String from,
                              @Value("${spring.mail.password:}") String password, ResumeStorageService storage) {
        this.sender = sender;
        this.from = from;
        this.password = password;
        this.storage = storage;
    }

    public boolean configured() {
        return !from.isBlank() && !password.isBlank() && sender.getIfAvailable() != null;
    }

    public void send(ResumeSubmission submission, String recipient) throws Exception {
        byte[] bytes = storage.savedFile(submission.getWordKey());
        if (!configured()) throw new ResumeException(503, "邮件服务未配置，管理员可配置后补发");
        if (recipient == null || recipient.isBlank()) throw new ResumeException(503, "尚未设置目标邮箱，管理员可设置后补发");
        var mailSender = sender.getObject();
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(recipient);
        helper.setSubject("[BDIS招新] " + submission.getName() + "-" + submission.getStudentNo());
        helper.setText("收到一份已确认保存的报名简历。\n姓名：" + submission.getName()
                + "\n学号：" + submission.getStudentNo() + "\n学年：" + submission.getAcademicYear());
        helper.addAttachment(submission.getDocumentName() + ".docx", new ByteArrayResource(bytes),
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mailSender.send(message);
    }
}
