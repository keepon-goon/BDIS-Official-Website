CREATE TABLE IF NOT EXISTS feedback_submission (
    id VARCHAR(36) PRIMARY KEY,
    category VARCHAR(24) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'UNREAD',
    reply_requested BOOLEAN NOT NULL DEFAULT FALSE,
    public_reply BOOLEAN NOT NULL DEFAULT FALSE,
    display_name VARCHAR(40) NOT NULL DEFAULT '匿名',
    reply_email VARCHAR(254) NOT NULL DEFAULT '',
    reply_content TEXT,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    replied_at DATETIME,
    reply_emailed_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS feedback_image (
    id VARCHAR(36) PRIMARY KEY,
    submission_id VARCHAR(36) NOT NULL,
    storage_key VARCHAR(64) NOT NULL,
    byte_size BIGINT NOT NULL,
    CONSTRAINT fk_feedback_image_submission FOREIGN KEY (submission_id) REFERENCES feedback_submission(id)
);
