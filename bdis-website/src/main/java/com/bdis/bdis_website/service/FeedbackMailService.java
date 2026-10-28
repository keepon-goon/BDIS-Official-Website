package com.bdis.bdis_website.service;

import com.bdis.bdis_website.entity.FeedbackImage;
import com.bdis.bdis_website.entity.FeedbackSubmission;
import com.bdis.bdis_website.exception.FeedbackException;
import jakarta.mail.internet.MimeUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class FeedbackMailService {
    private static final Logger log = LoggerFactory.getLogger(FeedbackMailService.class);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<String, String> CATEGORY_NAMES = Map.of(
            "join", "了解与加入",
            "website", "网站与体验",
            "equipment", "设备与环境",
            "operation", "运转与制度",
            "other", "探索与杂谈",
            "suggestion", "意见与建议",
            "activity", "活动与交流"
    );

    private final ObjectProvider<JavaMailSender> sender;
    private final String from;
    private final String password;
    private final String recipient;

    public FeedbackMailService(ObjectProvider<JavaMailSender> sender,
                               @Value("${spring.mail.username:}") String from,
                               @Value("${spring.mail.password:}") String password,
                               @Value("${feedback.recipient:18862028557@163.com}") String recipient) {
        this.sender = sender;
        this.from = from;
        this.password = password;
        this.recipient = recipient;
    }

    public boolean configured() {
        return !from.isBlank() && !password.isBlank() && sender.getIfAvailable() != null;
    }

    public void sendNotification(FeedbackSubmission submission, List<FeedbackImage> attachments, Path storageRoot) {
        if (!configured()) {
            log.info("邮件服务未配置（spring.mail.username 或 password 未设置），跳过匿名意见邮件转发。");
            return;
        }
        if (recipient == null || recipient.isBlank()) {
            log.warn("尚未设置反馈接收邮箱（feedback.recipient），跳过发送。");
            return;
        }

        try {
            var mailSender = sender.getObject();
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from, "BDIS实验室");
            helper.setTo(recipient);

            String catLabel = CATEGORY_NAMES.getOrDefault(submission.getCategory(), submission.getCategory());
            helper.setSubject("[BDIS匿名信箱] 收到新的意见反馈 (" + catLabel + ")");

            StringBuilder body = new StringBuilder();
            body.append("BDIS 匿名信箱收到一条新的意见投递：\n\n");
            body.append("【投递时间】：").append(submission.getCreatedAt() != null ? submission.getCreatedAt().format(TIME_FMT) : "").append("\n");
            body.append("【意见分类】：").append(catLabel).append("\n\n");
            if (Boolean.TRUE.equals(submission.getReplyRequested())) {
                if (Boolean.TRUE.equals(submission.getPublicReply())) {
                    body.append("【回复意愿】：希望公开回复，署名：").append(submission.getDisplayName()).append("\n\n");
                } else {
                    body.append("【回复意愿】：仅邮箱私密回信，不同意公开\n");
                    body.append("【回信邮箱】：").append(submission.getReplyEmail()).append("\n\n");
                }
            } else {
                body.append("【回复意愿】：未选择接收回复，请勿公开\n\n");
            }
            body.append("【意见内容】：\n");
            if (submission.getContent() != null && !submission.getContent().isBlank()) {
                body.append(submission.getContent()).append("\n\n");
            } else {
                body.append("(用户未填写文字，仅提交了图片)\n\n");
            }

            if (attachments != null && !attachments.isEmpty()) {
                body.append("【附件图片】：共 ").append(attachments.size()).append(" 张（见邮件附件）\n");
            }
            body.append("\n--------------------------------\n");
            body.append("可登录 BDIS 官网管理员后台处理：/admin/feedback\n");

            helper.setText(body.toString());

            // 附带图片附件
            if (attachments != null && !attachments.isEmpty() && storageRoot != null) {
                int index = 1;
                for (FeedbackImage img : attachments) {
                    Path imgPath = storageRoot.resolve(img.getStorageKey());
                    if (Files.exists(imgPath)) {
                        byte[] bytes = Files.readAllBytes(imgPath);
                        String filename = "意见图片_" + index + ".png";
                        helper.addAttachment(filename, new ByteArrayResource(bytes), "image/png");
                        var mimeMultipart = helper.getRootMimeMultipart();
                        var part = mimeMultipart.getBodyPart(mimeMultipart.getCount() - 1);
                        String encodedFilename = MimeUtility.encodeText(filename, "UTF-8", "B");
                        part.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"");
                        part.setHeader("Content-Type", "image/png; name=\"" + encodedFilename + "\"");
                        index++;
                    }
                }
            }

            mailSender.send(message);
            log.info("匿名意见邮件已成功发送至 {}", recipient);
        } catch (Exception e) {
            log.error("发送匿名意见通知邮件失败: {}", e.getMessage(), e);
        }
    }

    public void sendReply(FeedbackSubmission submission) {
        if (!configured()) throw new FeedbackException(503, "邮件服务尚未配置，回复已保存，请配置后重新发送");
        try {
            var mailSender = sender.getObject();
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from, "BDIS实验室");
            helper.setTo(submission.getReplyEmail());
            helper.setSubject("[BDIS匿名信箱] 实验室朋友给你的回信");
            helper.setText("你好，实验室朋友收到你的留言了。\n\n【你的留言】\n" + submission.getContent()
                    + "\n\n【实验室朋友的回答】\n" + submission.getReplyContent()
                    + "\n\n这是一封私密回信，本次问答不会在官网公开。\nBDIS 实验室");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("匿名信箱回信发送失败", e);
            throw new FeedbackException(503, "回信发送失败，回复已保存，请稍后重试发送");
        }
    }
}
