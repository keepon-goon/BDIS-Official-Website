CREATE TABLE IF NOT EXISTS resume_submission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    grade VARCHAR(80),
    major VARCHAR(80),
    phone VARCHAR(30),
    email VARCHAR(254),
    direction VARCHAR(80),
    intro TEXT,
    resume_url VARCHAR(512),
    status INT DEFAULT 0,
    created_at DATETIME,
    gender VARCHAR(10),
    ethnicity VARCHAR(30),
    college VARCHAR(80),
    student_no VARCHAR(40),
    birth_month VARCHAR(7),
    specialties VARCHAR(120),
    gpa VARCHAR(20),
    has_competition_experience TINYINT,
    awards TEXT,
    showcase TEXT,
    academic_year VARCHAR(20),
    document_name VARCHAR(255),
    template_version VARCHAR(64),
    submission_source VARCHAR(16),
    submission_key CHAR(64),
    word_key VARCHAR(512),
    pdf_key VARCHAR(512),
    mail_status INT DEFAULT 0,
    submit_mail_mode VARCHAR(10),
    mail_recipient VARCHAR(254),
    mail_sent_at DATETIME,
    mail_last_error VARCHAR(1000),
    CONSTRAINT uk_resume_submission_key UNIQUE (submission_key)
);

CREATE TABLE IF NOT EXISTS resume_settings (
    id BIGINT PRIMARY KEY,
    send_mode VARCHAR(10) NOT NULL,
    recipient VARCHAR(254) NOT NULL DEFAULT '',
    updated_at DATETIME
);

INSERT IGNORE INTO resume_settings (id, send_mode, recipient, updated_at)
VALUES (1, 'AUTO', '', CURRENT_TIMESTAMP);
