package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.util.JwtUtil;
import com.bdis.bdis_website.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:admin-auth-state-tests;MODE=MySQL;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class AdminAuthStateTest {
    @Autowired MockMvc mvc;
    @Autowired JwtUtil jwt;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void prepareAccount() {
        jdbc.update("DELETE FROM admin_user WHERE id = 2");
        jdbc.update("INSERT INTO admin_user(id, username, password, nickname, status) VALUES(2, ?, ?, ?, 1)",
                "state-user", PasswordUtil.encodePassword("Test-Password-20261109"), "状态测试管理员");
    }

    @Test
    void rejectsAnExistingTokenImmediatelyAfterAccountIsDisabled() throws Exception {
        String auth = auth();
        mvc.perform(get("/api/admin/info").header("Authorization", auth))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.username").value("state-user"));
        jdbc.update("UPDATE admin_user SET status = 0 WHERE id = 2");
        mvc.perform(get("/api/admin/info").header("Authorization", auth))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(1));
        mvc.perform(get("/api/admin/resumes").header("Authorization", auth)).andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsDeletedAccountsAndTokensWithoutAnAccountId() throws Exception {
        String auth = auth();
        jdbc.update("DELETE FROM admin_user WHERE id = 2");
        mvc.perform(get("/api/admin/info").header("Authorization", auth)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/info").header("Authorization", "Bearer " + jwt.generateToken(null, "state-user")))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(1));
    }

    private String auth() {
        return "Bearer " + jwt.generateToken(2L, "state-user");
    }
}
