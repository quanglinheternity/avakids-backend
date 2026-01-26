CREATE TABLE user_vip (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          total_points INT NOT NULL DEFAULT 0,
                          available_points INT NOT NULL DEFAULT 0,
                          total_spent DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
                          total_upgrades DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
                          tier_level VARCHAR(20) NOT NULL DEFAULT 'BRONZE',
                          tier_expires_at TIMESTAMP NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT fk_user_vip_user FOREIGN KEY (user_id)
                              REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_vip_user ON user_vip(user_id);
CREATE INDEX idx_user_vip_tier ON user_vip(tier_level);
CREATE INDEX idx_user_vip_expires ON user_vip(tier_expires_at);
CREATE INDEX idx_user_vip_points ON user_vip(available_points DESC);

CREATE TABLE user_vip_spending_6m (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,

                                      user_id BIGINT NOT NULL,

                                      total_spent_6m DECIMAL(15,2) NOT NULL DEFAULT 0,
                                      order_count_6m INT NOT NULL DEFAULT 0,

                                      period_start DATE NOT NULL,
                                      period_end DATE NOT NULL,

                                      last_order_at TIMESTAMP NULL,

                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                      CONSTRAINT uq_user_6m UNIQUE (user_id),
                                      CONSTRAINT fk_user_6m_user
                                          FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE user_point_redemption_log (
                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           user_id BIGINT NOT NULL,

                                           points_used INT NOT NULL,
                                           vip_tier VARCHAR(50),

                                           action VARCHAR(30) NOT NULL, -- REDEEM, EXPIRE, ADJUST
                                           reference_id VARCHAR(100),   -- orderId,

                                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                           CONSTRAINT chk_points_positive CHECK (points_used > 0)
);

CREATE INDEX idx_point_log_user ON user_point_redemption_log(user_id);
CREATE INDEX idx_point_log_created ON user_point_redemption_log(created_at);
