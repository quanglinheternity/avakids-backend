CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       phone VARCHAR(20) UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255),
                       avatar_url TEXT,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',
                       email_verified_at TIMESTAMP,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP
                           DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);

CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            parent_id BIGINT,
                            name VARCHAR(255) NOT NULL,
                            slug VARCHAR(255) UNIQUE NOT NULL,
                            display_order INT DEFAULT 0,
                            is_active BOOLEAN DEFAULT true,
                            FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_parent ON categories(parent_id);

CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          sku VARCHAR(100) UNIQUE NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          slug VARCHAR(255) UNIQUE NOT NULL,
                          category_id BIGINT,
                          description TEXT,
                          price DECIMAL(10,2),
                          sale_price DECIMAL(10,2),
                          has_variants BOOLEAN DEFAULT true,
                          min_price DECIMAL(10,2),
                          max_price DECIMAL(10,2),
                          total_stock INT DEFAULT 0,
                          is_active BOOLEAN DEFAULT true,
                          is_featured BOOLEAN DEFAULT false,
                          avg_rating DECIMAL(3,2) DEFAULT 0,
                          review_count INT DEFAULT 0,
                          sold_count INT DEFAULT 0,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP
                              DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_sku ON products(sku);

CREATE TABLE product_images (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                product_id BIGINT NOT NULL,
                                image_url TEXT NOT NULL,
                                display_order INT DEFAULT 0,
                                is_primary BOOLEAN DEFAULT false,
                                FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_images_product ON product_images(product_id);

CREATE TABLE product_options (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 name VARCHAR(255) NOT NULL,
                                 product_id BIGINT,
                                 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_product_options_product
                                     FOREIGN KEY (product_id)
                                         REFERENCES products(id)
                                         ON DELETE CASCADE
);

CREATE INDEX idx_product_options_product_id
    ON product_options(product_id);

CREATE TABLE product_option_values (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       value VARCHAR(255) NOT NULL,
                                       display_order INT DEFAULT 0,
                                       option_id BIGINT,
                                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_option_values_option
                                           FOREIGN KEY (option_id)
                                               REFERENCES product_options(id)
                                               ON DELETE CASCADE
);

CREATE INDEX idx_option_values_option_id
    ON product_option_values(option_id);

CREATE INDEX idx_option_values_order
    ON product_option_values(option_id, display_order);

CREATE TABLE product_variants (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  sku VARCHAR(100) NOT NULL,
                                  product_id BIGINT NOT NULL,
                                  variant_name VARCHAR(255),
                                  price DECIMAL(10,2) NOT NULL,
                                  sale_price DECIMAL(10,2),
                                  stock_quantity INT DEFAULT 0,
                                  sold_count INT DEFAULT 0,
                                  weight DECIMAL(10,2),
                                  dimensions VARCHAR(255),
                                  barcode VARCHAR(255),
                                  is_default BOOLEAN DEFAULT FALSE,
                                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                  CONSTRAINT uk_product_variant_sku UNIQUE (product_id, sku),

                                  CONSTRAINT fk_variant_product
                                      FOREIGN KEY (product_id)
                                          REFERENCES products(id)
                                          ON DELETE CASCADE
);

CREATE INDEX idx_variants_product_id
    ON product_variants(product_id);

CREATE INDEX idx_variants_is_default
    ON product_variants(product_id, is_default);

CREATE INDEX idx_variants_sku
    ON product_variants(sku);

CREATE INDEX idx_variants_stock
    ON product_variants(product_id, stock_quantity);

CREATE TABLE variant_option_values (
                                       variant_id BIGINT NOT NULL,
                                       option_value_id BIGINT NOT NULL,

                                       PRIMARY KEY (variant_id, option_value_id),

                                       CONSTRAINT fk_vov_variant
                                           FOREIGN KEY (variant_id)
                                               REFERENCES product_variants(id)
                                               ON DELETE CASCADE,

                                       CONSTRAINT fk_vov_option_value
                                           FOREIGN KEY (option_value_id)
                                               REFERENCES product_option_values(id)
                                               ON DELETE CASCADE
);

CREATE INDEX idx_vov_option_value ON variant_option_values(option_value_id);

CREATE INDEX idx_vov_variant ON variant_option_values(variant_id);

CREATE TABLE product_variant_images (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        image_url VARCHAR(500) NOT NULL,
                                        display_order INT DEFAULT 0,
                                        is_primary BOOLEAN DEFAULT FALSE,
                                        variant_id BIGINT,

                                        CONSTRAINT fk_variant_images_variant
                                            FOREIGN KEY (variant_id)
                                                REFERENCES product_variants(id)
                                                ON DELETE CASCADE
);

CREATE INDEX idx_variant_images_variant
    ON product_variant_images(variant_id);

CREATE INDEX idx_variant_images_primary
    ON product_variant_images(variant_id, is_primary);
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_number VARCHAR(50) UNIQUE NOT NULL,
                        user_id BIGINT,
                        status ENUM('PENDING','CONFIRMED','PROCESSING','SHIPPED','DELIVERED','COMPLETED','CANCELLED','REFUNDED') DEFAULT 'PENDING',
                        subtotal DECIMAL(10,2) NOT NULL,
                        discount_amount DECIMAL(10,2) DEFAULT 0,
                        point_amount DECIMAL(10,2) DEFAULT 0,
                        shipping_fee DECIMAL(10,2) DEFAULT 0,
                        total_amount DECIMAL(10,2) NOT NULL,
                        shipping_address JSON,
                        customer_note TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP
                            DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,
                        confirmed_at TIMESTAMP,
                        delivered_at TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_orders_status ON orders(status);

CREATE TABLE order_items (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             variant_id BIGINT,
                             product_name VARCHAR(255) NOT NULL,
                             sku VARCHAR(100) NOT NULL,
                             quantity INT NOT NULL,
                             unit_price DECIMAL(10,2) NOT NULL,
                             subtotal DECIMAL(10,2) NOT NULL,
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (variant_id) REFERENCES product_variants(id)
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(variant_id);

CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          order_id BIGINT NOT NULL,
                          payment_number VARCHAR(50) UNIQUE NOT NULL,
                          payment_method ENUM( 'COD','BANKING','EWALLET') NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          status ENUM('PENDING','PAID', 'FAILED','REFUNDED') DEFAULT 'PENDING',
                          transaction_id VARCHAR(255),
                          paid_at TIMESTAMP,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP
                              DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_number ON payments(payment_number);

CREATE TABLE vouchers (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          code VARCHAR(50) UNIQUE NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          discount_type ENUM(   'PERCENTAGE','FIXED') NOT NULL,
                          discount_value DECIMAL(10,2) NOT NULL,
                          max_discount DECIMAL(10,2),
                          min_order_amount DECIMAL(10,2) DEFAULT 0,
                          total_quantity INT NOT NULL,
                          used_quantity INT DEFAULT 0,
                          usage_limit_per_user INT DEFAULT 0,
                          is_active BOOLEAN DEFAULT true,
                          start_at TIMESTAMP NOT NULL,
                          end_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_vouchers_code ON vouchers(code);
CREATE INDEX idx_vouchers_active ON vouchers(is_active);

CREATE TABLE cart_items (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT,
                            variant_id BIGINT NOT NULL,
                            quantity INT NOT NULL DEFAULT 1,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP
                                DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP,
                            UNIQUE(user_id, variant_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE
);

CREATE INDEX idx_cart_items_user ON cart_items(user_id);

CREATE TABLE user_addresses (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                recipient_name VARCHAR(255) NOT NULL,
                                phone VARCHAR(20) NOT NULL,
                                address TEXT NOT NULL,
                                district VARCHAR(100),
                                city VARCHAR(100),
                                province VARCHAR(100),
                                is_default BOOLEAN DEFAULT false,
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_addresses_user ON user_addresses(user_id);

CREATE TABLE inventory_transactions (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        variant_id BIGINT NOT NULL,
                                        order_id BIGINT,
                                        transaction_type ENUM( 'IN', 'OUT','ADJUSTMENT_IN','ADJUSTMENT_OUT') NOT NULL,
                                        quantity INT NOT NULL,
                                        after_quantity INT,
                                        before_quantity INT ,
                                        note TEXT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
                                        FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_inventory_product ON inventory_transactions(variant_id);
CREATE INDEX idx_inventory_order ON inventory_transactions(order_id);

CREATE TABLE invalidated_tokens (
                                    token_id VARCHAR(36) PRIMARY KEY,
                                    expiry_time TIMESTAMP NOT NULL
);