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

class ResumeDocumentServiceTest {
    private final ResumeDocumentService service = new ResumeDocumentService(new ResumeProperties(),
            new DefaultResourceLoader(), Clock.fixed(Instant.parse("2026-10-22T04:00:00Z"), ZoneOffset.UTC));

    @Test
    void academicYearUsesSeptemberBoundary() {
        assertThat(ResumeDocumentService.academicYear(LocalDate.of(2027, 8, 31))).isEqualTo("2026-2027");
        assertThat(ResumeDocumentService.academicYear(LocalDate.of(2027, 9, 1))).isEqualTo("2027-2028");
        assertThat(ResumeDocumentService.academicYear(LocalDate.of(2027, 1, 1))).isEqualTo("2026-2027");
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
}
