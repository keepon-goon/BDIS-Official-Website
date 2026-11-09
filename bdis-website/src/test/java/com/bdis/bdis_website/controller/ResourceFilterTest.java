package com.bdis.bdis_website.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:resource-filter-tests;MODE=MySQL;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class ResourceFilterTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void prepareResources() {
        jdbc.execute("DROP TABLE IF EXISTS education_resource");
        jdbc.execute("CREATE TABLE education_resource (id BIGINT PRIMARY KEY, title VARCHAR(200), description TEXT, "
                + "sort_order INT, category VARCHAR(30), link VARCHAR(500))");
        jdbc.update("INSERT INTO education_resource VALUES(1, 'Java教程', '基础课程', 0, 'backend', NULL), "
                + "(2, '页面教程', 'Java相关介绍', 1, 'frontend', NULL), "
                + "(3, '进阶课程', 'Java实战', 2, 'backend', NULL)");
    }

    @Test
    void appliesCategoryToBothTitleAndDescriptionMatches() throws Exception {
        mvc.perform(get("/api/resources").param("category", "backend").param("keyword", "Java"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[1].id").value(3));
        mvc.perform(get("/api/resources").param("keyword", "Java"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(3));
    }
}
