package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.entity.FeedbackImage;
import com.bdis.bdis_website.mapper.FeedbackImageMapper;
import com.bdis.bdis_website.mapper.FeedbackSubmissionMapper;
import com.bdis.bdis_website.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:feedback-tests;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.sql.init.schema-locations=file:sql/resume_submission_migration.sql,file:sql/feedback_migration.sql"
})
@AutoConfigureMockMvc
class FeedbackTest {
    @TempDir static Path storage;
    @DynamicPropertySource static void properties(DynamicPropertyRegistry registry) {
        registry.add("feedback.storage-root", () -> storage.toString());
    }
    @Autowired MockMvc mvc;
    @Autowired JwtUtil jwt;
    @Autowired ObjectMapper json;
    @Autowired FeedbackSubmissionMapper submissions;
    @Autowired FeedbackImageMapper images;

    @BeforeEach void reset() throws Exception {
        images.delete(null);
        submissions.delete(null);
        try (var files = Files.list(storage)) {
            for (Path file : files.toList()) Files.delete(file);
        }
    }

    private String auth() { return "Bearer " + jwt.generateToken(1L, "feedback-test"); }
    private byte[] png() throws Exception {
        var output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(8, 6, BufferedImage.TYPE_INT_RGB), "png", output);
        output.write("private-metadata-marker".getBytes(StandardCharsets.UTF_8));
        return output.toByteArray();
    }
    private MockMultipartFile picture() throws Exception {
        return new MockMultipartFile("images", "private-name.png", "image/png", png());
    }
    private JsonNode list() throws Exception {
        String body = mvc.perform(get("/api/admin/feedback").header("Authorization", auth()))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("data");
    }

    @Test void acceptsAnonymousTextAndDoesNotOfferAPublicInbox() throws Exception {
        mvc.perform(multipart("/api/feedback").param("content", "  希望增加项目交流活动  ").param("category", "activity"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.received").value(true))
                .andExpect(jsonPath("$.data.id").doesNotExist());
        var entry = submissions.selectList(null).get(0);
        assertEquals("希望增加项目交流活动", entry.getContent());
        assertEquals("UNREAD", entry.getStatus());
        mvc.perform(get("/api/feedback")).andExpect(status().isMethodNotAllowed());
        mvc.perform(get("/api/feedback/" + entry.getId())).andExpect(status().isNotFound());
        mvc.perform(get("/api/admin/feedback")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/feedback").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/feedback/" + entry.getId())).andExpect(status().isUnauthorized());
    }

    @Test void acceptsImageOnlyAndServesNormalizedImagesOnlyToAdmins() throws Exception {
        mvc.perform(multipart("/api/feedback").file(picture())).andExpect(status().isOk());
        FeedbackImage image = images.selectList(null).get(0);
        assertTrue(Files.exists(storage.resolve(image.getStorageKey())));
        JsonNode data = list();
        assertEquals(1, data.get("counts").get("UNREAD").asInt());
        JsonNode record = data.get("list").get(0);
        assertFalse(record.toString().contains("storageKey"));
        assertFalse(record.toString().contains("private-name"));
        assertFalse(record.toString().contains("ipAddress"));
        String url = record.get("images").get(0).get("url").asText();
        mvc.perform(get(url)).andExpect(status().isUnauthorized());
        byte[] bytes = mvc.perform(get(url).header("Authorization", auth()))
                .andExpect(status().isOk()).andExpect(content().contentType("image/png"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andReturn().getResponse().getContentAsByteArray();
        assertNotNull(ImageIO.read(new ByteArrayInputStream(bytes)));
        assertFalse(new String(bytes, StandardCharsets.ISO_8859_1).contains("private-metadata-marker"));
        mvc.perform(get("/api/admin/feedback/other/images/" + image.getId()).header("Authorization", auth()))
                .andExpect(status().isNotFound());
        mvc.perform(get("/uploads/" + image.getStorageKey())).andExpect(status().isNotFound());
    }

    @Test void validatesEmptyInvalidOversizedAndTooManyAttachments() throws Exception {
        mvc.perform(multipart("/api/feedback").param("content", "  ")).andExpect(status().isBadRequest());
        mvc.perform(multipart("/api/feedback").param("content", "a".repeat(3001))).andExpect(status().isBadRequest());
        mvc.perform(multipart("/api/feedback").param("content", "test").param("category", "unknown"))
                .andExpect(status().isBadRequest());
        mvc.perform(multipart("/api/feedback").file(new MockMultipartFile("images", "fake.png", "image/png", "not an image".getBytes())))
                .andExpect(status().isBadRequest());
        mvc.perform(multipart("/api/feedback").file(new MockMultipartFile("images", "huge.png", "image/png", new byte[5 * 1024 * 1024 + 1])))
                .andExpect(status().isPayloadTooLarge());
        var tooMany = multipart("/api/feedback");
        for (int index = 0; index < 7; index++) tooMany.file(picture());
        mvc.perform(tooMany).andExpect(status().isBadRequest());
        assertEquals(0, submissions.selectCount(null));
        assertEquals(0, images.selectCount(null));
    }

    @Test void normalizesMultipartLineEndingsBeforeApplyingTheCharacterLimit() throws Exception {
        String canonical = "a".repeat(1499) + "\n" + "b".repeat(1500);
        mvc.perform(multipart("/api/feedback").param("content", canonical.replace("\n", "\r\n")))
                .andExpect(status().isOk());
        assertEquals(canonical, submissions.selectList(null).get(0).getContent());
    }

    @Test void removesStagedFilesWhenALaterAttachmentFails() throws Exception {
        mvc.perform(multipart("/api/feedback").file(picture())
                        .file(new MockMultipartFile("images", "fake.jpg", "image/jpeg", new byte[] { 1, 2, 3 })))
                .andExpect(status().isBadRequest());
        assertEquals(0, submissions.selectCount(null));
        assertEquals(0, images.selectCount(null));
        try (var files = Files.list(storage)) { assertEquals(0, files.count()); }
    }

    @Test void supportsMultiplePicturesAndAdminFilteringAndStatusChanges() throws Exception {
        mvc.perform(multipart("/api/feedback").file(picture()).file(picture())
                .param("category", "equipment").param("content", "设备无法使用\n第二行情况说明"))
                .andExpect(status().isOk());
        String id = submissions.selectList(null).get(0).getId();
        mvc.perform(put("/api/admin/feedback/" + id + "/status").contentType("application/json").content("{\"status\":\"PROCESSING\"}"))
                .andExpect(status().isUnauthorized());
        for (String state : Arrays.asList("PROCESSING", "RESOLVED")) {
            mvc.perform(put("/api/admin/feedback/" + id + "/status").header("Authorization", auth())
                            .contentType("application/json").content("{\"status\":\"" + state + "\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(0));
        }
        mvc.perform(get("/api/admin/feedback").header("Authorization", auth()).param("status", "RESOLVED").param("category", "equipment"))
                .andExpect(jsonPath("$.data.total").value(1)).andExpect(jsonPath("$.data.list[0].images.length()").value(2));
        mvc.perform(get("/api/admin/feedback").header("Authorization", auth()).param("status", "UNREAD"))
                .andExpect(jsonPath("$.data.total").value(0)).andExpect(jsonPath("$.data.counts.RESOLVED").value(1));
        mvc.perform(get("/api/admin/feedback/" + id).header("Authorization", auth()))
                .andExpect(jsonPath("$.data.status").value("RESOLVED"));
        mvc.perform(put("/api/admin/feedback/" + id + "/status").header("Authorization", auth())
                        .contentType("application/json").content("{\"status\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/feedback").header("Authorization", auth()).param("pageSize", "999"))
                .andExpect(status().isBadRequest());
    }
}
