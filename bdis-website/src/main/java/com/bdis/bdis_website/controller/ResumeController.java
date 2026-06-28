package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.ResumeSubmission;
import com.bdis.bdis_website.mapper.ResumeSubmissionMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeSubmissionMapper mapper;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${mail.recipient:18862028557@163.com}")
    private String recipient;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    @PostMapping("/submit")
    public Result<Void> submit(
            @RequestParam("name") String name,
            @RequestParam(value = "grade", required = false) String grade,
            @RequestParam(value = "major", required = false) String major,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "intro", required = false) String intro,
            @RequestParam(value = "resume", required = false) MultipartFile resume
    ) {
        if (name == null || name.trim().isEmpty()) {
            return Result.fail("姓名不能为空");
        }

        String resumeUrl = null;
        if (resume != null && !resume.isEmpty()) {
            try {
                resumeUrl = "/uploads/resumes/" + resume.getOriginalFilename();
                java.io.File dir = new java.io.File("./uploads/resumes");
                if (!dir.exists()) dir.mkdirs();
                resume.transferTo(new java.io.File(dir, resume.getOriginalFilename()));
            } catch (IOException e) {
                return Result.fail("简历上传失败: " + e.getMessage());
            }
        }

        ResumeSubmission submission = new ResumeSubmission();
        submission.setName(name.trim());
        submission.setGrade(grade);
        submission.setMajor(major);
        submission.setPhone(phone);
        submission.setEmail(email);
        submission.setDirection(direction);
        submission.setIntro(intro);
        submission.setResumeUrl(resumeUrl);
        submission.setStatus(0);
        submission.setCreatedAt(LocalDateTime.now());
        mapper.insert(submission);

        try {
            sendEmail(submission, resume);
        } catch (Exception e) {
            System.err.println("邮件发送失败: " + e.getMessage());
        }

        return Result.success();
    }

    private void sendEmail(ResumeSubmission sub, MultipartFile resume) throws MessagingException, IOException {
        if (mailSender == null || mailFrom == null || mailFrom.isEmpty()) return;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(mailFrom);
        helper.setTo(recipient);
        helper.setSubject("[BDIS招新] " + sub.getName() + " 提交了简历");

        StringBuilder sb = new StringBuilder();
        sb.append("<h3>收到新的简历投递</h3>");
        sb.append("<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse;'>");
        sb.append("<tr><td><b>姓名</b></td><td>").append(sub.getName()).append("</td></tr>");
        sb.append("<tr><td><b>年级</b></td><td>").append(nvl(sub.getGrade())).append("</td></tr>");
        sb.append("<tr><td><b>专业</b></td><td>").append(nvl(sub.getMajor())).append("</td></tr>");
        sb.append("<tr><td><b>手机</b></td><td>").append(nvl(sub.getPhone())).append("</td></tr>");
        sb.append("<tr><td><b>邮箱</b></td><td>").append(nvl(sub.getEmail())).append("</td></tr>");
        sb.append("<tr><td><b>意向方向</b></td><td>").append(nvl(sub.getDirection())).append("</td></tr>");
        sb.append("<tr><td><b>自我介绍</b></td><td>").append(nvl(sub.getIntro())).append("</td></tr>");
        sb.append("</table>");

        helper.setText(sb.toString(), true);

        if (resume != null && !resume.isEmpty()) {
            helper.addAttachment(resume.getOriginalFilename(),
                    new ByteArrayDataSource(resume.getBytes(), resume.getContentType()));
        }

        mailSender.send(message);
    }

    private String nvl(String s) {
        return (s == null || s.isEmpty()) ? "-" : s;
    }
}
