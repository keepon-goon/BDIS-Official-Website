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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import org.springframework.mock.web.MockMultipartFile;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"resume.converter=docker", "resume.storage-root=target/integration-private-resumes"})
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "BDIS_TEST_LIBREOFFICE", matches = "true")
class ResumePreviewIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private ResumeStorageService storage;
    @Autowired private com.bdis.bdis_website.service.ResumeDocumentService documents;

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
        String withPhoto = mvc.perform(multipart("/api/resume/previews/photo")
                        .file(picture(400, 600, Color.RED)).header("X-Resume-Preview-Token", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.hasPhoto").value(true))
                .andReturn().getResponse().getContentAsString();
        String nextToken = json.readTree(withPhoto).at("/data/previewToken").asText();
        mvc.perform(get("/api/resume/previews/pdf").header("X-Resume-Preview-Token", token)).andExpect(status().isGone());
        mvc.perform(multipart("/api/resume/previews/photo").file(picture(400, 400, Color.BLUE))
                .header("X-Resume-Preview-Token", nextToken)).andExpect(status().isBadRequest());
        mvc.perform(get("/api/resume/previews/pdf").header("X-Resume-Preview-Token", nextToken)).andExpect(status().isOk());
        String replaced = mvc.perform(multipart("/api/resume/previews/photo")
                        .file(picture(400, 600, Color.BLUE)).header("X-Resume-Preview-Token", nextToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String finalToken = json.readTree(replaced).at("/data/previewToken").asText();
        var finalSnapshot = storage.load(finalToken);
        Path finalWord = storage.directory("previews", storage.hash(finalToken)).resolve(finalSnapshot.getDocumentName() + ".docx");
        assertThat(finalSnapshot.getDocumentName()).isEqualTo(snapshot.getDocumentName());
        try (var document = new XWPFDocument(Files.newInputStream(finalWord))) {
            assertThat(document.getAllPictures()).hasSize(1);
            var image = ImageIO.read(new java.io.ByteArrayInputStream(document.getAllPictures().get(0).getData()));
            assertThat(image.getRGB(300, 450)).isEqualTo(Color.BLUE.getRGB());
        }
        Files.copy(finalWord, Path.of("target/test-artifacts/photo-resume.docx"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(finalWord.resolveSibling(finalSnapshot.getDocumentName() + ".pdf"),
                Path.of("target/test-artifacts/photo-resume.pdf"), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void rejectsInvalidFormWithoutGeneratingPreview() throws Exception {
        mvc.perform(post("/api/resume/previews").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void uploadsOriginalWordAndRejectsInvalidOrThreePageFiles() throws Exception {
        byte[] original = documents.blankTemplate();
        String response = mvc.perform(multipart("/api/resume/submit")
                        .file(new MockMultipartFile("resume", "../原始文件名.docx", "application/octet-stream", original))
                        .param("name", "离线测试").param("studentNo", "000001").param("phone", "13800000000"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.sourceType").value("UPLOAD"))
                .andReturn().getResponse().getContentAsString();
        String token = json.readTree(response).at("/data/previewToken").asText();
        byte[] downloaded = mvc.perform(get("/api/resume/previews/word").header("X-Resume-Preview-Token", token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
        assertThat(downloaded).isEqualTo(original);
        mvc.perform(multipart("/api/resume/previews/photo").file(picture(400, 600, Color.BLUE))
                .header("X-Resume-Preview-Token", token)).andExpect(status().isBadRequest());
        mvc.perform(multipart("/api/resume/submit")
                        .file(new MockMultipartFile("resume", "假文件.docx", "application/octet-stream", new byte[]{1, 2, 3}))
                        .param("name", "测试").param("studentNo", "001").param("phone", "13800000000"))
                .andExpect(status().isBadRequest());
        try (var document = new XWPFDocument(new java.io.ByteArrayInputStream(original)); var out = new ByteArrayOutputStream()) {
            var run = document.createParagraph().createRun();
            run.addBreak(org.apache.poi.xwpf.usermodel.BreakType.PAGE);
            run.setText("超出的第三页");
            document.write(out);
            mvc.perform(multipart("/api/resume/submit")
                            .file(new MockMultipartFile("resume", "三页.docx", "application/octet-stream", out.toByteArray()))
                            .param("name", "测试").param("studentNo", "001").param("phone", "13800000000"))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    void expiredSourceDoesNotBlockEditedWordAndLongOnlineTextIsRejected() throws Exception {
        mvc.perform(multipart("/api/resume/submit")
                        .file(new MockMultipartFile("resume", "已编辑.docx", "application/octet-stream", documents.blankTemplate()))
                        .param("name", "测试").param("studentNo", "001").param("phone", "13800000000")
                        .header("X-Resume-Preview-Token", "c".repeat(64)))
                .andExpect(status().isOk());
        var form = json.readTree(new ClassPathResource("resume-form.json").getContentAsByteArray());
        ((com.fasterxml.jackson.databind.node.ObjectNode) form).put("intro", "测试内容\n".repeat(100));
        mvc.perform(post("/api/resume/previews").contentType("application/json").content(json.writeValueAsBytes(form)))
                .andExpect(status().isUnprocessableEntity());
    }

    private MockMultipartFile picture(int width, int height, Color color) throws Exception {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();
        var bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);
        return new MockMultipartFile("photo", "照片.png", "image/png", bytes.toByteArray());
    }
}
