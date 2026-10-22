DROP PROCEDURE IF EXISTS _bdis_resume_add_column;
DROP PROCEDURE IF EXISTS _bdis_resume_finalize;

DELIMITER $$
CREATE PROCEDURE _bdis_resume_add_column(IN p_name VARCHAR(64), IN p_definition VARCHAR(200))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = 'resume_submission' AND column_name = p_name
    ) THEN
        SET @bdis_resume_column_ddl = CONCAT('ALTER TABLE resume_submission ADD COLUMN `', p_name, '` ', p_definition);
        PREPARE bdis_resume_column_statement FROM @bdis_resume_column_ddl;
        EXECUTE bdis_resume_column_statement;
        DEALLOCATE PREPARE bdis_resume_column_statement;
    END IF;
END$$

CREATE PROCEDURE _bdis_resume_finalize()
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = 'resume_submission'
          AND column_name = 'intro' AND data_type IN ('varchar', 'char', 'tinytext')
    ) THEN
        ALTER TABLE resume_submission MODIFY COLUMN intro TEXT NULL;
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE() AND table_name = 'resume_submission' AND index_name = 'uk_resume_submission_key'
    ) THEN
        ALTER TABLE resume_submission ADD UNIQUE KEY uk_resume_submission_key (submission_key);
    END IF;
END$$
DELIMITER ;

CALL _bdis_resume_add_column('gender', 'VARCHAR(10) NULL');
CALL _bdis_resume_add_column('ethnicity', 'VARCHAR(30) NULL');
CALL _bdis_resume_add_column('college', 'VARCHAR(80) NULL');
CALL _bdis_resume_add_column('student_no', 'VARCHAR(40) NULL');
CALL _bdis_resume_add_column('birth_month', 'VARCHAR(7) NULL');
CALL _bdis_resume_add_column('specialties', 'VARCHAR(120) NULL');
CALL _bdis_resume_add_column('gpa', 'VARCHAR(20) NULL');
CALL _bdis_resume_add_column('has_competition_experience', 'TINYINT NULL');
CALL _bdis_resume_add_column('awards', 'TEXT NULL');
CALL _bdis_resume_add_column('showcase', 'TEXT NULL');
CALL _bdis_resume_add_column('academic_year', 'VARCHAR(20) NULL');
CALL _bdis_resume_add_column('document_name', 'VARCHAR(255) NULL');
CALL _bdis_resume_add_column('template_version', 'VARCHAR(64) NULL');
CALL _bdis_resume_add_column('submission_source', 'VARCHAR(16) NULL');
CALL _bdis_resume_add_column('submission_key', 'CHAR(64) NULL');
CALL _bdis_resume_add_column('word_key', 'VARCHAR(512) NULL');
CALL _bdis_resume_add_column('pdf_key', 'VARCHAR(512) NULL');
CALL _bdis_resume_add_column('mail_status', 'INT DEFAULT 0');
CALL _bdis_resume_add_column('submit_mail_mode', 'VARCHAR(10) NULL');
CALL _bdis_resume_add_column('mail_recipient', 'VARCHAR(254) NULL');
CALL _bdis_resume_add_column('mail_sent_at', 'DATETIME NULL');
CALL _bdis_resume_add_column('mail_last_error', 'VARCHAR(1000) NULL');
CALL _bdis_resume_finalize();

DROP PROCEDURE _bdis_resume_add_column;
DROP PROCEDURE _bdis_resume_finalize;

CREATE TABLE IF NOT EXISTS resume_settings (
    id BIGINT PRIMARY KEY,
    send_mode VARCHAR(10) NOT NULL,
    recipient VARCHAR(254) NOT NULL DEFAULT '',
    updated_at DATETIME
);
INSERT IGNORE INTO resume_settings (id, send_mode, recipient, updated_at)
VALUES (1, 'AUTO', '', CURRENT_TIMESTAMP);
