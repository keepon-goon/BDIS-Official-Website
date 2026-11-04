package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:lab-info-tests;MODE=MySQL;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class LabInfoTest {
    private static final String ADMIN_PATH = "/api/admin/lab-info";
    private static final String PUBLIC_PATH = "/api/lab/info";

    @Autowired MockMvc mvc;
    @Autowired JwtUtil jwt;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void migrateLegacyTable() {
        jdbc.execute("DROP TABLE IF EXISTS lab_info");
        jdbc.execute("""
                CREATE TABLE lab_info (
                    id BIGINT PRIMARY KEY, lab_name VARCHAR(200), lab_desc TEXT, name_en VARCHAR(200),
                    intro_short TEXT, address VARCHAR(200), email VARCHAR(100), qq_group VARCHAR(50),
                    logo_url VARCHAR(500), picture VARCHAR(500)
                )
                """);
        jdbc.update("INSERT INTO lab_info (id, lab_name, intro_short, address) VALUES (1, '工作室', '已有简介', '原地址')");
        new ResourceDatabasePopulator(new FileSystemResource("sql/lab_join_intro_migration.sql"))
                .execute(jdbc.getDataSource());
    }

    @Test
    void sharesEditableContentAndKeepsSiteSettingsIndependent() throws Exception {
        mvc.perform(get(PUBLIC_PATH))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.data.introShort").value("已有简介"))
                .andExpect(jsonPath("$.data.joinIntro").isNotEmpty())
                .andExpect(jsonPath("$.data.address").value("原地址"));
        mvc.perform(get(ADMIN_PATH).header("Authorization", auth()))
                .andExpect(jsonPath("$.data.joinIntro").isNotEmpty());

        var content = Map.of("introShort", "新版简介\n第二段", "joinIntro", "新版招新\n第二段",
                "address", "新地址", "email", "bdis@example.com", "qqGroup", "012345678");
        mvc.perform(put(ADMIN_PATH).header("Authorization", auth()).contentType(APPLICATION_JSON)
                        .content(json.writeValueAsBytes(content)))
                .andExpect(jsonPath("$.code").value(0));
        mvc.perform(get(PUBLIC_PATH))
                .andExpect(jsonPath("$.data.name").value("工作室"))
                .andExpect(jsonPath("$.data.introShort").value(content.get("introShort")))
                .andExpect(jsonPath("$.data.joinIntro").value(content.get("joinIntro")))
                .andExpect(jsonPath("$.data.address").value(content.get("address")))
                .andExpect(jsonPath("$.data.email").value(content.get("email")))
                .andExpect(jsonPath("$.data.qqGroup").value(content.get("qqGroup")));

        mvc.perform(put(ADMIN_PATH).header("Authorization", auth()).contentType(APPLICATION_JSON)
                        .content(json.writeValueAsBytes(Map.of("name", "新名称"))))
                .andExpect(jsonPath("$.code").value(0));
        mvc.perform(get(PUBLIC_PATH))
                .andExpect(jsonPath("$.data.name").value("新名称"))
                .andExpect(jsonPath("$.data.joinIntro").value(content.get("joinIntro")))
                .andExpect(jsonPath("$.data.email").value(content.get("email")));
    }

    @Test
    void protectsWritesAndKeepsExplicitlyClearedValues() throws Exception {
        var payload = put(ADMIN_PATH).contentType(APPLICATION_JSON)
                .content(json.writeValueAsBytes(Map.of("joinIntro", "", "address", "", "email", "", "qqGroup", "")));
        mvc.perform(get(ADMIN_PATH)).andExpect(status().isUnauthorized());
        mvc.perform(payload).andExpect(status().isUnauthorized());
        mvc.perform(payload.header("Authorization", auth())).andExpect(jsonPath("$.code").value(0));
        mvc.perform(get(PUBLIC_PATH))
                .andExpect(jsonPath("$.data.joinIntro").value(""))
                .andExpect(jsonPath("$.data.address").value(""))
                .andExpect(jsonPath("$.data.email").value(""))
                .andExpect(jsonPath("$.data.qqGroup").value(""));
    }

    private String auth() {
        return "Bearer " + jwt.generateToken(1L, "lab-info-test");
    }
}
