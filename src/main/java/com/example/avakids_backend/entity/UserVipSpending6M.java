package com.example.avakids_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_vip_spending_6m")
@Getter
@Setter
public class UserVipSpending6M {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(name = "total_spent_6m", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSpent6m = BigDecimal.ZERO;

    @Column(name = "order_count_6m", nullable = false)
    private int orderCount6m = 0;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    private LocalDateTime lastOrderAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
