CREATE TABLE IF NOT EXISTS admin_user (
    id BIGINT PRIMARY KEY, username VARCHAR(50), password VARCHAR(255), nickname VARCHAR(50),
    status INT DEFAULT 1, created_at TIMESTAMP, updated_at TIMESTAMP
);
MERGE INTO admin_user (id, username, password, nickname, status)
KEY (id) VALUES (1, 'test-admin', 'unused', '测试管理员', 1);
