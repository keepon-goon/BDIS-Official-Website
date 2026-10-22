package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.service.ResumeStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.Loader;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"resume.converter=docker", "resume.storage-root=target/integration-private-resumes"})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "BDIS_TEST_LIBREOFFICE", matches = "true")
class ResumePreviewIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private ResumeStorageService storage;

    @Test
    void createsRealTwoPageWordAndPdfWithPrivateSnapshot() throws Exception {
        byte[] form = new ClassPathResource("resume-form.json").getContentAsByteArray();
        String response = mvc.perform(post("/api/resume/previews").contentType("application/json").content(form))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.hasPhoto").value(false)).andReturn().getResponse().getContentAsString();
        String token = json.readTree(response).at("/data/previewToken").asText();
        assertThat(token).matches("[a-f0-9]{64}");
        var snapshot = storage.load(token);
        Path directory = storage.directory("previews", storage.hash(token));
        Path word = directory.resolve(snapshot.getDocumentName() + ".docx");
        Path pdf = directory.resolve(snapshot.getDocumentName() + ".pdf");
        try (var document = Loader.loadPDF(pdf.toFile())) {
            assertThat(document.getNumberOfPages()).isEqualTo(2);
        }
        try (var document = new XWPFDocument(Files.newInputStream(word)); var extractor = new XWPFWordExtractor(document)) {
            assertThat(extractor.getText()).contains("测试同学", "000020260001", "A&B、<测试>")
                    .doesNotContain("202X");
            assertThat(document.getAllPictures()).isEmpty();
        }
        Files.createDirectories(Path.of("target/test-artifacts"));
        Files.copy(word, Path.of("target/test-artifacts/generated-resume.docx"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(pdf, Path.of("target/test-artifacts/generated-resume.pdf"), StandardCopyOption.REPLACE_EXISTING);
        assertThat(response).doesNotContain(directory.toString());
    }

    @Test
    void rejectsInvalidFormWithoutGeneratingPreview() throws Exception {
        mvc.perform(post("/api/resume/previews").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(1));
    }
}
