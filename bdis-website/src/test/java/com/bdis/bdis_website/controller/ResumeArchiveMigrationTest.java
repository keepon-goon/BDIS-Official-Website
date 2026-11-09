package com.bdis.bdis_website.controller;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import static org.assertj.core.api.Assertions.assertThat;

class ResumeArchiveMigrationTest {
    @Test
    void upgradesLegacyRecordsToUnarchivedAndCreatesMailHistory() {
        var source = new DriverManagerDataSource("jdbc:h2:mem:archive-migration-test;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        var jdbc = new JdbcTemplate(source);
        jdbc.execute("CREATE TABLE resume_submission (id BIGINT PRIMARY KEY, name VARCHAR(30), created_at DATETIME)");
        jdbc.update("INSERT INTO resume_submission VALUES(10, '历史简历', CURRENT_TIMESTAMP)");
        new ResourceDatabasePopulator(new FileSystemResource("sql/resume_archive_mail_log_migration.sql")).execute(source);
        assertThat(jdbc.queryForObject("SELECT archived FROM resume_submission WHERE id = 10", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT name FROM resume_submission WHERE id = 10", String.class)).isEqualTo("历史简历");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM resume_mail_log", Long.class)).isZero();
    }
}
