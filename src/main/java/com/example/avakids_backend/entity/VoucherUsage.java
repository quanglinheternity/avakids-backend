package com.example.avakids_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.*;

@Entity
@Table(
        name = "voucher_usage",
        indexes = {
            @Index(name = "idx_voucher_usage_user", columnList = "user_id"),
            @Index(name = "idx_voucher_usage_voucher", columnList = "voucher_id"),
            @Index(name = "idx_voucher_usage_order", columnList = "order_id")
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_voucher_order",
                    columnNames = {"order_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Số tiền đơn hàng trước khi giảm
    @Column(name = "order_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal orderAmount;

    // Số tiền được giảm
    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @CreationTimestamp
    @Column(name = "used_at", nullable = false, updatable = false)
    private LocalDateTime usedAt;
}
