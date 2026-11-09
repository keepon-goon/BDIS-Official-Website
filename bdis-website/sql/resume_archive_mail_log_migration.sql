ALTER TABLE resume_submission
    ADD COLUMN archived TINYINT NOT NULL DEFAULT 0;

CREATE INDEX idx_resume_archive_created ON resume_submission (archived, created_at, id);

CREATE TABLE IF NOT EXISTS resume_mail_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL,
    trigger_type VARCHAR(10) NOT NULL,
    recipient VARCHAR(254),
    status VARCHAR(16) NOT NULL,
    started_at DATETIME NOT NULL,
    finished_at DATETIME,
    duration_ms BIGINT,
    error_type VARCHAR(200),
    smtp_code INT,
    error_message VARCHAR(1000),
    INDEX idx_resume_mail_log_submission (submission_id, started_at, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
