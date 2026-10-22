package com.bdis.bdis_website.service;

import com.bdis.bdis_website.config.ResumeProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResumeDocumentServiceTest {
    private final ResumeDocumentService service = new ResumeDocumentService(new ResumeProperties(),
            new DefaultResourceLoader(), Clock.fixed(Instant.parse("2026-10-22T04:00:00Z"), ZoneOffset.UTC));

    @Test
    void academicYearUsesSeptemberBoundary() {
        assertThat(ResumeDocumentService.academicYear(LocalDate.of(2027, 8, 31))).isEqualTo("2026-2027");
        assertThat(ResumeDocumentService.academicYear(LocalDate.of(2027, 9, 1))).isEqualTo("2027-2028");
        assertThat(ResumeDocumentService.academicYear(LocalDate.of(2027, 1, 1))).isEqualTo("2026-2027");
        var beforeMidnight = new ResumeDocumentService(new ResumeProperties(), new DefaultResourceLoader(),
                Clock.fixed(Instant.parse("2027-08-31T15:59:00Z"), ZoneOffset.UTC));
        var afterMidnight = new ResumeDocumentService(new ResumeProperties(), new DefaultResourceLoader(),
                Clock.fixed(Instant.parse("2027-08-31T16:00:00Z"), ZoneOffset.UTC));
        assertThat(beforeMidnight.currentAcademicYear()).isEqualTo("2026-2027");
        assertThat(afterMidnight.currentAcademicYear()).isEqualTo("2027-2028");
    }

    @Test
    void generatesFilledTemplateForLayoutVerification() throws Exception {
        var form = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                new org.springframework.core.io.ClassPathResource("resume-form.json").getInputStream(),
                com.bdis.bdis_website.dto.ResumeFormDTO.class);
        java.nio.file.Path output = java.nio.file.Path.of("target/test-artifacts/filled-layout.docx");
        java.nio.file.Files.createDirectories(output.getParent());
        service.generate(form, "2026-2027", null, output);
        try (var document = new XWPFDocument(java.nio.file.Files.newInputStream(output)); var extractor = new XWPFWordExtractor(document)) {
            assertThat(extractor.getText()).contains("000020260001", "A&B、<测试>", "测试学院");
        }
    }

    @Test
    void blankTemplateContainsActualYearAndPreservesTwoTables() throws Exception {
        try (var doc = new XWPFDocument(new ByteArrayInputStream(service.blankTemplate()));
             var extractor = new XWPFWordExtractor(doc)) {
            assertThat(extractor.getText().replaceAll("\\s", "")).contains("2026-2027学年", "个人简历", "本学年获奖情况")
                    .doesNotContain("202X", "{{");
            assertThat(doc.getTables()).hasSize(2);
            assertThat(doc.getAllPictures()).isEmpty();
        }
        assertThat(service.documentName("张三", "000123", "2026-2027"))
                .isEqualTo("张三-000123-2026学年-2027学年大数据智能工作室招新报名表");
    }

    @Test
    void uploadedWordChecksFiftyMegabyteBoundary() {
        byte[] bytes = service.blankTemplate();
        var exact = new org.springframework.mock.web.MockMultipartFile("resume", "手工.docx", "application/octet-stream", bytes) {
            @Override public long getSize() { return ResumeDocumentService.MAX_UPLOAD_BYTES; }
        };
        assertThat(service.validateUpload(exact)).isEqualTo(bytes);
        var tooLarge = new org.springframework.mock.web.MockMultipartFile("resume", "手工.docx", "application/octet-stream", bytes) {
            @Override public long getSize() { return ResumeDocumentService.MAX_UPLOAD_BYTES + 1; }
        };
        assertThatThrownBy(() -> service.validateUpload(tooLarge)).isInstanceOfSatisfying(
                com.bdis.bdis_website.exception.ResumeException.class, e -> assertThat(e.getStatus()).isEqualTo(413));
    }
}
