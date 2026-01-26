CREATE TABLE voucher_usage (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,

                               voucher_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               order_id BIGINT NOT NULL,

                               order_amount DECIMAL(10, 2) NOT NULL,
                               discount_amount DECIMAL(10, 2) NOT NULL,

                               used_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT uk_voucher_order UNIQUE (order_id),

                               CONSTRAINT fk_voucher_usage_voucher
                                   FOREIGN KEY (voucher_id) REFERENCES vouchers(id),

                               CONSTRAINT fk_voucher_usage_user
                                   FOREIGN KEY (user_id) REFERENCES users(id),

                               CONSTRAINT fk_voucher_usage_order
                                   FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_voucher_usage_user
    ON voucher_usage(user_id);

CREATE INDEX idx_voucher_usage_voucher
    ON voucher_usage(voucher_id);

CREATE INDEX idx_voucher_usage_order
    ON voucher_usage(order_id);
