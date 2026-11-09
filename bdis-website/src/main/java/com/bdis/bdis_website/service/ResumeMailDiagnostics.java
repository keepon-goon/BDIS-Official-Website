package com.bdis.bdis_website.service;

import jakarta.mail.MessagingException;
import org.eclipse.angus.mail.smtp.SMTPAddressFailedException;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Component
public class ResumeMailDiagnostics {
    private static final Pattern SMTP_CODE = Pattern.compile("^\\s*([245]\\d{2})(?:[\\s-]|$)");
    private final String password;

    public ResumeMailDiagnostics(@Value("${spring.mail.password:}") String password) {
        this.password = password;
    }

    public Failure describe(Throwable error) {
        List<Throwable> causes = new ArrayList<>();
        causes.add(error);
        Throwable detail = error;
        Throwable smtpFailure = null;
        Integer smtpCode = null;
        for (int i = 0; i < causes.size() && i < 32; i++) {
            Throwable current = causes.get(i);
            detail = current;
            if (current instanceof SMTPSendFailedException smtp) {
                smtpCode = smtp.getReturnCode();
                smtpFailure = current;
            } else if (current instanceof SMTPAddressFailedException smtp) {
                smtpCode = smtp.getReturnCode();
                smtpFailure = current;
            }
            addCause(causes, current.getCause());
            if (current instanceof MessagingException messaging) addCause(causes, messaging.getNextException());
            if (current instanceof MailSendException mail) {
                for (Exception nested : mail.getMessageExceptions()) addCause(causes, nested);
            }
        }
        if (smtpFailure != null) detail = smtpFailure;
        String message = detail.getMessage();
        if (smtpCode == null && message != null) {
            var matcher = SMTP_CODE.matcher(message);
            if (matcher.find()) smtpCode = Integer.valueOf(matcher.group(1));
        }
        return new Failure(detail.getClass().getName(), smtpCode,
                safeText(message == null || message.isBlank() ? "邮件发送异常，未提供详细原因" : message));
    }

    private void addCause(List<Throwable> causes, Throwable cause) {
        if (cause != null && !causes.contains(cause)) causes.add(cause);
    }

    public String safeText(String value) {
        String result = value;
        if (password != null && !password.isBlank()) {
            result = result.replace(password, "[凭据已隐藏]")
                    .replace(Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)), "[凭据已隐藏]");
        }
        result = result.replaceAll("(?i)\\bAUTH\\s+(?:PLAIN|LOGIN)\\b[^\\r\\n]*", "AUTH [凭据已隐藏]")
                .replaceAll("(?i)\\b(?:proxy-authorization|authorization)\\s*[:=]\\s*[^\\r\\n]*", "authorization=[凭据已隐藏]")
                .replaceAll("(?i)(password|passwd|pwd|token|authorization|secret|密码|授权码)\\s*[:=]\\s*[^\\s,;]+", "$1=[凭据已隐藏]")
                .replaceAll("(?i)[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", "[邮箱已隐藏]")
                .replaceAll("\\p{Cntrl}", " ").strip();
        return result.length() > 1000 ? result.substring(0, 1000) : result;
    }

    public record Failure(String errorType, Integer smtpCode, String message) { }
}
