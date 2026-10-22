package com.bdis.bdis_website.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ResumeTemplateTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void downloadsWordWithoutLoginAndDisablesCaching() throws Exception {
        byte[] body = mvc.perform(get("/api/resume/template"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .andReturn().getResponse().getContentAsByteArray();
        Files.createDirectories(Path.of("target/test-artifacts"));
        Files.write(Path.of("target/test-artifacts/blank-template.docx"), body);
    }
}
