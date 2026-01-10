-- Table: banners
CREATE TABLE banners (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(255) NOT NULL,
                         image_url VARCHAR(500) NOT NULL,
                         link_url VARCHAR(500),
                         position ENUM('TOP', 'MIDDLE', 'BOTTOM', 'SIDEBAR') NOT NULL,
                         display_order INT NOT NULL DEFAULT 0,
                         start_at TIMESTAMP NULL,
                         end_at TIMESTAMP NULL,
                         is_active BOOLEAN NOT NULL DEFAULT TRUE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_banners_position ON banners(position);
CREATE INDEX idx_banners_active ON banners(is_active);
CREATE INDEX idx_banners_dates ON banners(start_at, end_at);

-- Table: blogs
CREATE TABLE blogs (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       title VARCHAR(255) NOT NULL,
                       slug VARCHAR(255) NOT NULL UNIQUE,
                       content TEXT NOT NULL,
                       thumbnail_url VARCHAR(500),
                       view_count INT NOT NULL DEFAULT 0,
                       published_at TIMESTAMP NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_blogs_slug ON blogs(slug);
CREATE INDEX idx_blogs_published ON blogs(published_at);
CREATE INDEX idx_blogs_created ON blogs(created_at);

-- Table: wishlists
CREATE TABLE wishlists (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           user_id BIGINT NOT NULL,
                           product_id BIGINT NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           UNIQUE KEY uq_user_product (user_id, product_id),
                           CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX idx_wishlists_user ON wishlists(user_id);
CREATE INDEX idx_wishlists_product ON wishlists(product_id);

-- Table: product_reviews
CREATE TABLE product_reviews (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 product_id BIGINT NOT NULL,
                                 user_id BIGINT NOT NULL,
                                 order_id BIGINT NOT NULL,
                                 rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                                 content TEXT,
                                 image_url VARCHAR(500),
                                 is_verified_purchase BOOLEAN NOT NULL DEFAULT FALSE,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_reviews_product ON product_reviews(product_id);
CREATE INDEX idx_product_reviews_user ON product_reviews(user_id);
CREATE INDEX idx_product_reviews_order ON product_reviews(order_id);
CREATE INDEX idx_product_reviews_rating ON product_reviews(rating);
CREATE INDEX idx_product_reviews_verified ON product_reviews(is_verified_purchase);
