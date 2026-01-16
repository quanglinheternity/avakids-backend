package com.example.avakids_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.avakids_backend.enums.TierLevel;

import lombok.*;

@Entity
@Table(name = "user_vip")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "available_points", nullable = false)
    private Integer availablePoints = 0;

    @Column(name = "total_spent", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "total_upgrades", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalUpgrades = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier_level", nullable = false, length = 20)
    private TierLevel tierLevel = TierLevel.BRONZE;

    @Column(name = "tier_expires_at")
    private LocalDateTime tierExpiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
