package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminResumeTest {
    @Autowired private MockMvc mvc;
    @Autowired private JwtUtil jwt;

    private String auth() { return "Bearer " + jwt.generateToken(1L, "resume-test"); }

    @Test
    void settingsRequiresAdminAndDoesNotExposeCredentials() throws Exception {
        mvc.perform(get("/api/admin/resumes/settings")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/resumes/settings").header("Authorization", auth()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.sendMode").exists())
                .andExpect(jsonPath("$.data.mailConfigured").value(false))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(header().string("Cache-Control", "no-store"));
    }
}
