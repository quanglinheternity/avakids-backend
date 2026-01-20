INSERT INTO users (email, phone, password_hash, full_name, avatar_url, email_verified_at) VALUES
                                                                                              ('customer1@gmail.com', '0901234567', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Nguyễn Văn A', 'https://example.com/avatar1.jpg', NOW()),
                                                                                              ('customer2@gmail.com', '0912345678', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Trần Thị B', 'https://example.com/avatar2.jpg', NOW()),
                                                                                              ('customer3@gmail.com', '0923456789', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Lê Văn C', NULL, NOW());
INSERT INTO categories (id, parent_id, name, slug, display_order) VALUES
                                                                      (1, NULL, 'Sữa tươi', 'sua-tuoi', 1),
                                                                      (2, NULL, 'Sữa đặc', 'sua-dac', 2),
                                                                      (3, NULL, 'Sữa bột', 'sua-bot', 3),
                                                                      (4, NULL, 'Sữa chua', 'sua-chua', 4);

-- CHILD
INSERT INTO categories (parent_id, name, slug, display_order) VALUES
                                                                  (1, 'Sữa tươi có đường', 'sua-tuoi-co-duong', 1),
                                                                  (1, 'Sữa tươi không đường', 'sua-tuoi-khong-duong', 2),
                                                                  (2, 'Sữa đặc có đường', 'sua-dac-co-duong', 1),
                                                                  (2, 'Sữa đặc ít đường', 'sua-dac-it-duong', 2);
INSERT INTO products (sku, name, slug, category_id, description, price, sale_price, has_variants, min_price, max_price, total_stock, is_featured, avg_rating, review_count, sold_count) VALUES
                                                                                                                                                                                            ('STV001', 'Sữa tươi Vinamilk có đường 180ml', 'sua-tuoi-vinamilk-co-duong-180ml', 5, 'Sữa tươi tiệt trùng Vinamilk có đường, hộp 180ml', 8000, 7500, false, 7500, 8000, 1000, true, 4.5, 120, 500),
                                                                                                                                                                                            ('STV002', 'Sữa tươi Vinamilk không đường 180ml', 'sua-tuoi-vinamilk-khong-duong-180ml', 6, 'Sữa tươi tiệt trùng Vinamilk không đường, hộp 180ml', 8000, NULL, false, 8000, 8000, 800, false, 4.2, 85, 300),
                                                                                                                                                                                            ('SD001', 'Sữa đặc Ông Thọ có đường 380g', 'sua-dac-ong-tho-co-duong-380g', 7, 'Sữa đặc Ông Thọ có đường, lon 380g', 32000, 30000, true, 30000, 32000, 500, true, 4.7, 200, 800),
                                                                                                                                                                                            ('SB001', 'Sữa bột Friso Gold 4 900g', 'sua-bot-friso-gold-4-900g', 3, 'Sữa bột dinh dưỡng cho trẻ 2-6 tuổi', 450000, 420000, true, 420000, 450000, 150, true, 4.8, 150, 300),
                                                                                                                                                                                            ('SC001', 'Sữa chua Vinamilk có đường 100g', 'sua-chua-vinamilk-co-duong-100g', 4, 'Sữa chua ăn Vinamilk có đường, hộp 100g', 5000, 4500, false, 4500, 5000, 2000, false, 4.3, 95, 1200);
INSERT INTO product_images (product_id, image_url, display_order, is_primary) VALUES
                                                                                  (1, 'https://example.com/sua-tuoi-vinamilk-1.jpg', 1, true),
                                                                                  (1, 'https://example.com/sua-tuoi-vinamilk-2.jpg', 2, false),
                                                                                  (2, 'https://example.com/sua-tuoi-khong-duong-1.jpg', 1, true),
                                                                                  (3, 'https://example.com/sua-dac-ong-tho-1.jpg', 1, true),
                                                                                  (4, 'https://example.com/friso-gold-4-1.jpg', 1, true),
                                                                                  (4, 'https://example.com/friso-gold-4-2.jpg', 2, false),
                                                                                  (5, 'https://example.com/sua-chua-vinamilk-1.jpg', 1, true);
INSERT INTO product_options (product_id, name) VALUES
                                                   (3, 'Loại'),
                                                   (3, 'Khối lượng'),
                                                   (4, 'Độ tuổi'),
                                                   (4, 'Khối lượng');
INSERT INTO product_option_values (option_id, value, display_order) VALUES
                                                                        (1, 'Có đường', 1),
                                                                        (1, 'Ít đường', 2),
                                                                        (2, '380g', 1),
                                                                        (2, '170g', 2),
                                                                        (3, '0-6 tháng', 1),
                                                                        (3, '6-12 tháng', 2),
                                                                        (3, '1-2 tuổi', 3),
                                                                        (3, '2-6 tuổi', 4),
                                                                        (4, '400g', 1),
                                                                        (4, '900g', 2),
                                                                        (4, '1800g', 3);
INSERT INTO product_variants (sku, product_id, variant_name, price, sale_price, stock_quantity, weight, is_default) VALUES
                                                                                                                        ('SD001-1', 3, 'Sữa đặc Ông Thọ có đường 380g', 32000, 30000, 300, 0.38, true),
                                                                                                                        ('SD001-2', 3, 'Sữa đặc Ông Thọ ít đường 380g', 33000, 31000, 200, 0.38, false),
                                                                                                                        ('SD001-3', 3, 'Sữa đặc Ông Thọ có đường 170g', 16000, 15000, 150, 0.17, false),
                                                                                                                        ('SB001-1', 4, 'Friso Gold 4 900g', 450000, 420000, 100, 0.9, true),
                                                                                                                        ('SB001-2', 4, 'Friso Gold 4 400g', 220000, 200000, 50, 0.4, false);
INSERT INTO variant_option_values (variant_id, option_value_id) VALUES
                                                                    (1, 1), -- SD001-1: Có đường
                                                                    (1, 3), -- SD001-1: 380g
                                                                    (2, 2), -- SD001-2: Ít đường
                                                                    (2, 3), -- SD001-2: 380g
                                                                    (3, 1), -- SD001-3: Có đường
                                                                    (3, 4), -- SD001-3: 170g
                                                                    (4, 8), -- SB001-1: 2-6 tuổi
                                                                    (4, 11), -- SB001-1: 900g
                                                                    (5, 8), -- SB001-2: 2-6 tuổi
                                                                    (5, 10); -- SB001-2: 400g
INSERT INTO user_addresses (user_id, recipient_name, phone, address, district, city, province, is_default) VALUES
                                                                                                               (1, 'Nguyễn Văn A', '0901234567', '123 Đường Lê Lợi', 'Quận 1', 'TP. Hồ Chí Minh', 'Hồ Chí Minh', true),
                                                                                                               (1, 'Nguyễn Văn A', '0901234567', '456 Đường Nguyễn Huệ', 'Quận 1', 'TP. Hồ Chí Minh', 'Hồ Chí Minh', false),
                                                                                                               (2, 'Trần Thị B', '0912345678', '789 Đường Hai Bà Trưng', 'Hoàn Kiếm', 'Hà Nội', 'Hà Nội', true);
INSERT INTO cart_items (user_id, variant_id, quantity) VALUES
                                                           (1, 1, 3), -- Người dùng 1: Sữa đặc Ông Thọ có đường 380g x3
                                                           (1, 4, 1), -- Người dùng 1: Friso Gold 4 900g x1
                                                           (2, 2, 2); -- Người dùng 2: Sữa đặc Ông Thọ ít đường 380g x2
INSERT INTO vouchers (code, name, discount_type, discount_value, max_discount, min_order_amount, total_quantity, start_at, end_at) VALUES
                                                                                                                                       ('SALE10', 'Giảm 10% đơn hàng', 'PERCENTAGE', 10.00, 50000, 100000, 100, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
                                                                                                                                       ('FREESHIP', 'Miễn phí vận chuyển', 'FIXED', 30000, 30000, 150000, 50, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY)),
                                                                                                                                       ('MILK50K', 'Giảm 50K đơn sữa', 'FIXED', 50000, 50000, 200000, 30, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY));
INSERT INTO orders (order_number, user_id, status, subtotal, discount_amount, shipping_fee, total_amount, shipping_address, confirmed_at) VALUES
                                                                                                                                              ('ORD001', 1, 'DELIVERED', 140000, 14000, 20000, 146000, '{"recipient_name": "Nguyễn Văn A", "phone": "0901234567", "address": "123 Đường Lê Lợi, Quận 1, TP.HCM"}', NOW()),
                                                                                                                                              ('ORD002', 2, 'PROCESSING', 66000, 0, 15000, 81000, '{"recipient_name": "Trần Thị B", "phone": "0912345678", "address": "789 Đường Hai Bà Trưng, Hoàn Kiếm, Hà Nội"}', NOW()),
                                                                                                                                              ('ORD003', 1, 'CONFIRMED', 450000, 45000, 0, 405000, '{"recipient_name": "Nguyễn Văn A", "phone": "0901234567", "address": "456 Đường Nguyễn Huệ, Quận 1, TP.HCM"}', NOW());
INSERT INTO payments (order_id, payment_number, payment_method, amount, status, paid_at) VALUES
                                                                                             (1, 'PAY001', 'BANKING', 146000, 'PAID', NOW()),
                                                                                             (2, 'PAY002', 'COD', 81000, 'PENDING', NULL),
                                                                                             (3, 'PAY003', 'EWALLET', 405000, 'PAID', NOW());
INSERT INTO inventory_transactions (variant_id, order_id, transaction_type, quantity, note) VALUES
                                                                                                (1, 1, 'OUT', 3, 'Bán hàng đơn ORD001'),
                                                                                                (4, 1, 'OUT', 1, 'Bán hàng đơn ORD001'),
                                                                                                (2, 2, 'OUT', 2, 'Bán hàng đơn ORD002'),
                                                                                                (4, 3, 'OUT', 1, 'Bán hàng đơn ORD003'),
                                                                                                (1, NULL, 'IN', 100, 'Nhập hàng từ nhà cung cấp'),
                                                                                                (4, NULL, 'IN', 50, 'Nhập hàng từ nhà cung cấp');