package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.controller.admin.AdminFileController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminFileAccessTest {
    @TempDir Path temporary;
    private MockMvc mvc;

    @BeforeEach
    void prepareFiles() throws Exception {
        Path uploads = Files.createDirectory(temporary.resolve("uploads"));
        Files.writeString(uploads.resolve("public.png"), "public-image");
        Files.writeString(temporary.resolve("outside.txt"), "private-sentinel");
        Files.createDirectory(uploads.resolve("directory"));
        var controller = new AdminFileController();
        ReflectionTestUtils.setField(controller, "uploadPath", uploads.toString());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void servesOnlyRegularFilesInsideTheUploadDirectory() throws Exception {
        mvc.perform(get("/api/uploads/public.png"))
                .andExpect(status().isOk()).andExpect(content().contentType("image/png"))
                .andExpect(content().string("public-image"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
        mvc.perform(get("/api/uploads/missing.png")).andExpect(status().isNotFound());
        mvc.perform(get("/api/uploads/directory")).andExpect(status().isNotFound());
    }

    @Test
    void rejectsWindowsAndUnixParentDirectoryPaths() throws Exception {
        mvc.perform(get("/api/uploads/{filename}", "..\\outside.txt")).andExpect(status().isNotFound());
        mvc.perform(get("/api/uploads/{filename}", "../outside.txt")).andExpect(status().isNotFound());
    }
}
