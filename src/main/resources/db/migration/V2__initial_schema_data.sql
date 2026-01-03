-- 1. Chèn dữ liệu vào bảng users
INSERT INTO users (email, phone, password_hash, full_name, avatar_url, email_verified_at) VALUES
                                                                                              ('john.doe@example.com', '0901234567', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John Doe', 'https://example.com/avatar1.jpg', NOW()),
                                                                                              ('jane.smith@example.com', '0902345678', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane Smith', 'https://example.com/avatar2.jpg', NOW()),
                                                                                              ('bob.wilson@example.com', '0903456789', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Bob Wilson', NULL, NULL),
                                                                                              ('alice.johnson@example.com', '0904567890', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Alice Johnson', 'https://example.com/avatar4.jpg', NOW());

-- 2. Chèn dữ liệu vào bảng categories
INSERT INTO categories (parent_id, name, slug, display_order, is_active) VALUES
                                                                             (NULL, 'Điện thoại', 'dien-thoai', 1, true),
                                                                             (NULL, 'Laptop', 'laptop', 2, true),
                                                                             (NULL, 'Phụ kiện', 'phu-kien', 3, true),
                                                                             (1, 'iPhone', 'iphone', 1, true),
                                                                             (1, 'Samsung', 'samsung', 2, true),
                                                                             (1, 'Xiaomi', 'xiaomi', 3, true),
                                                                             (2, 'MacBook', 'macbook', 1, true),
                                                                             (2, 'Dell', 'dell', 2, true),
                                                                             (2, 'HP', 'hp', 3, true),
                                                                             (3, 'Tai nghe', 'tai-nghe', 1, true),
                                                                             (3, 'Sạc dự phòng', 'sac-du-phong', 2, true),
                                                                             (3, 'Ốp lưng', 'op-lung', 3, true);

-- 3. Chèn dữ liệu vào bảng products
INSERT INTO products (sku, name, slug, category_id, description, price, sale_price, stock_quantity, is_active, is_featured, avg_rating, review_count, sold_count) VALUES
                                                                                                                                                                      ('IPHONE15-128', 'iPhone 15 128GB', 'iphone-15-128gb', 4, 'iPhone 15 mới nhất của Apple', 22990000, 20990000, 50, true, true, 4.8, 125, 300),
                                                                                                                                                                      ('IPHONE15PRO-256', 'iPhone 15 Pro 256GB', 'iphone-15-pro-256gb', 4, 'iPhone 15 Pro với camera chuyên nghiệp', 29990000, 28990000, 30, true, true, 4.9, 89, 150),
                                                                                                                                                                      ('SAMSUNGS23-256', 'Samsung Galaxy S23 256GB', 'samsung-galaxy-s23-256gb', 5, 'Flagship của Samsung', 21990000, 19990000, 45, true, false, 4.7, 210, 450),
                                                                                                                                                                      ('XIAOMI13-256', 'Xiaomi 13 256GB', 'xiaomi-13-256gb', 6, 'Điện thoại flagship của Xiaomi', 17990000, 16990000, 60, true, false, 4.5, 156, 280),
                                                                                                                                                                      ('MACBOOKAIR-M2', 'MacBook Air M2 2023', 'macbook-air-m2-2023', 7, 'Laptop siêu mỏng nhẹ của Apple', 28990000, 27990000, 25, true, true, 4.9, 75, 120),
                                                                                                                                                                      ('DELLXPS13-2023', 'Dell XPS 13 2023', 'dell-xps-13-2023', 8, 'Laptop cao cấp của Dell', 24990000, 23990000, 20, true, false, 4.6, 92, 85),
                                                                                                                                                                      ('AIRPODSPRO2', 'AirPods Pro 2', 'airpods-pro-2', 10, 'Tai nghe không dây Apple', 6990000, 6490000, 100, true, true, 4.8, 312, 600),
                                                                                                                                                                      ('SACPOWER10000', 'Sạc dự phòng 10000mAh', 'sac-du-phong-10000mah', 11, 'Sạc dự phòng dung lượng cao', 890000, 790000, 200, true, false, 4.3, 189, 420),
                                                                                                                                                                      ('OPLUNGIPHONE15', 'Ốp lưng iPhone 15', 'op-lung-iphone-15', 12, 'Ốp lưng cao cấp cho iPhone 15', 450000, 350000, 150, true, false, 4.2, 67, 180),
                                                                                                                                                                      ('SAMSUNGCHARGER', 'Củ sạc nhanh Samsung 25W', 'cu-sac-nhanh-samsung-25w', 11, 'Củ sạc nhanh chính hãng Samsung', 550000, 450000, 80, true, false, 4.4, 45, 95);

-- 4. Chèn dữ liệu vào bảng product_images
INSERT INTO product_images (product_id, image_url, display_order, is_primary) VALUES
                                                                                  (1, 'https://example.com/iphone15-1.jpg', 1, true),
                                                                                  (1, 'https://example.com/iphone15-2.jpg', 2, false),
                                                                                  (1, 'https://example.com/iphone15-3.jpg', 3, false),
                                                                                  (2, 'https://example.com/iphone15pro-1.jpg', 1, true),
                                                                                  (2, 'https://example.com/iphone15pro-2.jpg', 2, false),
                                                                                  (3, 'https://example.com/samsungs23-1.jpg', 1, true),
                                                                                  (4, 'https://example.com/xiaomi13-1.jpg', 1, true),
                                                                                  (5, 'https://example.com/macbookair-1.jpg', 1, true),
                                                                                  (5, 'https://example.com/macbookair-2.jpg', 2, false),
                                                                                  (6, 'https://example.com/dellxps-1.jpg', 1, true),
                                                                                  (7, 'https://example.com/airpodspro-1.jpg', 1, true),
                                                                                  (8, 'https://example.com/sacduphong-1.jpg', 1, true),
                                                                                  (9, 'https://example.com/oplung-1.jpg', 1, true),
                                                                                  (10, 'https://example.com/cusac-1.jpg', 1, true);

-- 5. Chèn dữ liệu vào bảng user_addresses
INSERT INTO user_addresses (user_id, recipient_name, phone, address, district, city, province, is_default) VALUES
                                                                                                               (1, 'John Doe', '0901234567', '123 Đường Lê Lợi', 'Quận 1', 'Hồ Chí Minh', 'TP.HCM', true),
                                                                                                               (1, 'John Doe', '0901234567', '456 Đường Nguyễn Huệ', 'Quận 1', 'Hồ Chí Minh', 'TP.HCM', false),
                                                                                                               (2, 'Jane Smith', '0902345678', '789 Đường Trần Hưng Đạo', 'Quận 5', 'Hồ Chí Minh', 'TP.HCM', true),
                                                                                                               (3, 'Bob Wilson', '0903456789', '101 Đường Lý Thường Kiệt', 'Quận Hoàn Kiếm', 'Hà Nội', 'Hà Nội', true),
                                                                                                               (4, 'Alice Johnson', '0904567890', '202 Đường Hai Bà Trưng', 'Quận 3', 'Hồ Chí Minh', 'TP.HCM', true);

-- 6. Chèn dữ liệu vào bảng orders
INSERT INTO orders (order_number, user_id, status, subtotal, discount_amount, shipping_fee, total_amount, shipping_address, customer_note, created_at, confirmed_at, delivered_at) VALUES
                                                                                                                                                                                       ('ORD001', 1, 'delivered', 28990000, 2000000, 30000, 26990000, '{"recipient_name": "John Doe", "phone": "0901234567", "address": "123 Đường Lê Lợi, Quận 1, TP.HCM"}', 'Giao hàng giờ hành chính', '2024-01-15 10:30:00', '2024-01-15 11:00:00', '2024-01-16 14:20:00'),
                                                                                                                                                                                       ('ORD002', 2, 'processing', 34990000, 1000000, 0, 33990000, '{"recipient_name": "Jane Smith", "phone": "0902345678", "address": "789 Đường Trần Hưng Đạo, Quận 5, TP.HCM"}', 'Để hàng ở cổng', '2024-01-20 14:45:00', '2024-01-20 15:30:00', NULL),
                                                                                                                                                                                       ('ORD003', 1, 'shipped', 7090000, 0, 30000, 7120000, '{"recipient_name": "John Doe", "phone": "0901234567", "address": "456 Đường Nguyễn Huệ, Quận 1, TP.HCM"}', NULL, '2024-01-25 09:15:00', '2024-01-25 10:00:00', NULL),
                                                                                                                                                                                       ('ORD004', 3, 'pending', 16990000, 0, 30000, 17020000, '{"recipient_name": "Bob Wilson", "phone": "0903456789", "address": "101 Đường Lý Thường Kiệt, Quận Hoàn Kiếm, Hà Nội"}', 'Kiểm tra kỹ hàng trước khi nhận', '2024-01-28 16:20:00', NULL, NULL),
                                                                                                                                                                                       ('ORD005', 4, 'delivered', 27990000, 0, 0, 27990000, '{"recipient_name": "Alice Johnson", "phone": "0904567890", "address": "202 Đường Hai Bà Trưng, Quận 3, TP.HCM"}', NULL, '2024-01-10 11:10:00', '2024-01-10 11:30:00', '2024-01-11 15:45:00');

-- 7. Chèn dữ liệu vào bảng order_items
INSERT INTO order_items (order_id, product_id, product_name, sku, quantity, unit_price, subtotal) VALUES
                                                                                                      (1, 2, 'iPhone 15 Pro 256GB', 'IPHONE15PRO-256', 1, 28990000, 28990000),
                                                                                                      (2, 5, 'MacBook Air M2 2023', 'MACBOOKAIR-M2', 1, 27990000, 27990000),
                                                                                                      (2, 7, 'AirPods Pro 2', 'AIRPODSPRO2', 1, 6490000, 6490000),
                                                                                                      (3, 7, 'AirPods Pro 2', 'AIRPODSPRO2', 1, 6490000, 6490000),
                                                                                                      (3, 9, 'Ốp lưng iPhone 15', 'OPLUNGIPHONE15', 2, 350000, 700000),
                                                                                                      (4, 4, 'Xiaomi 13 256GB', 'XIAOMI13-256', 1, 16990000, 16990000),
                                                                                                      (5, 5, 'MacBook Air M2 2023', 'MACBOOKAIR-M2', 1, 27990000, 27990000);

-- 8. Chèn dữ liệu vào bảng payments
INSERT INTO payments (order_id, payment_number, payment_method, amount, status, transaction_id, paid_at) VALUES
                                                                                                             (1, 'PAY001', 'banking', 26990000, 'paid', 'TRX00123456', '2024-01-15 10:35:00'),
                                                                                                             (2, 'PAY002', 'ewallet', 33990000, 'paid', 'TRX00234567', '2024-01-20 14:50:00'),
                                                                                                             (3, 'PAY003', 'cod', 7120000, 'pending', NULL, NULL),
                                                                                                             (4, 'PAY004', 'banking', 17020000, 'pending', NULL, NULL),
                                                                                                             (5, 'PAY005', 'ewallet', 27990000, 'paid', 'TRX00345678', '2024-01-10 11:15:00');

-- 9. Chèn dữ liệu vào bảng vouchers
INSERT INTO vouchers (code, name, discount_type, discount_value, max_discount, min_order_amount, total_quantity, used_quantity, is_active, start_at, end_at) VALUES
                                                                                                                                                                 ('SALE10', 'Giảm 10% tối đa 500K', 'percentage', 10, 500000, 2000000, 1000, 245, true, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
                                                                                                                                                                 ('FIXED50K', 'Giảm thẳng 50K', 'fixed', 50000, 50000, 1000000, 500, 189, true, '2024-01-01 00:00:00', '2024-06-30 23:59:59'),
                                                                                                                                                                 ('TET2024', 'Giảm 15% tối đa 1 triệu', 'percentage', 15, 1000000, 5000000, 200, 56, true, '2024-01-20 00:00:00', '2024-02-20 23:59:59'),
                                                                                                                                                                 ('FREESHIP', 'Miễn phí vận chuyển', 'fixed', 30000, 30000, 500000, 1000, 312, true, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
                                                                                                                                                                 ('VIP20', 'Giảm 20% cho VIP', 'percentage', 20, 2000000, 3000000, 100, 12, true, '2024-01-01 00:00:00', '2024-12-31 23:59:59');

-- 10. Chèn dữ liệu vào bảng cart_items
INSERT INTO cart_items (user_id, product_id, quantity) VALUES
                                                           (1, 1, 1),
                                                           (1, 8, 2),
                                                           (2, 3, 1),
                                                           (2, 10, 1),
                                                           (3, 6, 1),
                                                           (4, 2, 1),
                                                           (4, 7, 1);

-- 11. Chèn dữ liệu vào bảng inventory_transactions
INSERT INTO inventory_transactions (product_id, order_id, transaction_type, quantity, note) VALUES
                                                                                                (2, 1, 'out', 1, 'Bán hàng cho đơn hàng ORD001'),
                                                                                                (5, 2, 'out', 1, 'Bán hàng cho đơn hàng ORD002'),
                                                                                                (7, 2, 'out', 1, 'Bán hàng cho đơn hàng ORD002'),
                                                                                                (7, 3, 'out', 1, 'Bán hàng cho đơn hàng ORD003'),
                                                                                                (9, 3, 'out', 2, 'Bán hàng cho đơn hàng ORD003'),
                                                                                                (4, 4, 'out', 1, 'Bán hàng cho đơn hàng ORD004'),
                                                                                                (5, 5, 'out', 1, 'Bán hàng cho đơn hàng ORD005'),
                                                                                                (1, NULL, 'in', 100, 'Nhập hàng từ nhà cung cấp'),
                                                                                                (2, NULL, 'in', 50, 'Nhập hàng từ nhà cung cấp'),
                                                                                                (3, NULL, 'in', 80, 'Nhập hàng từ nhà cung cấp'),
                                                                                                (8, NULL, 'in', 200, 'Nhập hàng từ nhà cung cấp');