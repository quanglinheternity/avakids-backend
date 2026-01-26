CREATE TABLE user_fcm_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    platform ENUM('WEB','ANDROID','IOS') NOT NULL,
    device_id VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_used_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE (token),
    UNIQUE (user_id, device_id),
    INDEX idx_user_id (user_id)
);

CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    reference_id BIGINT,
    data JSON,
    is_push BOOLEAN DEFAULT FALSE,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    clicked_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_id (user_id),
    INDEX idx_user_read (user_id, is_read)
);
CREATE TABLE follow (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,

                        target_type VARCHAR(50) NOT NULL,   -- PRODUCT, ORDER, SHOP, CATEGORY...
                        target_id BIGINT NOT NULL,

                        notify BOOLEAN DEFAULT TRUE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                        UNIQUE (user_id, target_type, target_id),
                        INDEX idx_target (target_type, target_id),
                        INDEX idx_user (user_id)
);

