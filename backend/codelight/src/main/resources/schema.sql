CREATE TABLE IF NOT EXISTS users
(
    user_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name  VARCHAR(255),
    email      VARCHAR(255)            NOT NULL,
    password   VARCHAR(255),
    user_role  VARCHAR(50)             NOT NULL,
    login_type ENUM ('OAUTH', 'LOCAL') NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    is_deleted BOOLEAN DEFAULT FALSE,

    UNIQUE KEY uk_email_login_type (email, login_type),

    INDEX idx_user_role (user_role),
    INDEX idx_deleted (is_deleted)
);