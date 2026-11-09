package com.bdis.bdis_website.service;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;

import java.net.SocketTimeoutException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResumeMailDiagnosticsTest {
    private final ResumeMailDiagnostics diagnostics = new ResumeMailDiagnostics("test-secret-value");

    @Test
    void extractsSmtpCodeFromNestedFailedMessages() {
        var smtp = new SMTPSendFailedException("DATA", 552, "552 message too large for target@example.com", null, null, null, null);
        var failure = diagnostics.describe(new MailSendException(Map.of("message", smtp)));
        assertThat(failure.smtpCode()).isEqualTo(552);
        assertThat(failure.errorType()).isEqualTo(SMTPSendFailedException.class.getName());
        assertThat(failure.message()).contains("552", "message too large").doesNotContain("target@example.com");
    }

    @Test
    void redactsCredentialsAndControlCharactersWhileKeepingAuthenticationCode() {
        var cause = new AuthenticationFailedException("535 auth failed password=test-secret-value user=sender@example.com\r\nAUTH PLAIN opaque-credential\r\nAuthorization: Bearer another-secret");
        var failure = diagnostics.describe(new MailAuthenticationException("登录失败", cause));
        assertThat(failure.smtpCode()).isEqualTo(535);
        assertThat(failure.message()).contains("535", "auth failed")
                .doesNotContain("test-secret-value", "sender@example.com", "opaque-credential", "another-secret", "\r", "\n");
    }

    @Test
    void redactsBase64EncodedSmtpPassword() {
        String encoded = java.util.Base64.getEncoder().encodeToString("test-secret-value".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertThat(diagnostics.safeText("535 rejected credential " + encoded))
                .contains("535 rejected credential").doesNotContain(encoded, "test-secret-value");
    }

    @Test
    void keepsTimeoutCauseAndLimitsTheStoredMessage() {
        var failure = diagnostics.describe(new MessagingException("发送异常", new SocketTimeoutException("Read timed out")));
        assertThat(failure.errorType()).isEqualTo(SocketTimeoutException.class.getName());
        assertThat(failure.message()).isEqualTo("Read timed out");
        assertThat(failure.smtpCode()).isNull();
        assertThat(diagnostics.safeText("x".repeat(1500))).hasSize(1000);
    }
}
