package com.example.avakids_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_point_redemption_log")
@Getter
@Setter
public class UserPointRedemptionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer pointsUsed;

    @Column(length = 50)
    private String vipTier;

    @Column(nullable = false, length = 30)
    private String action; // REDEEM, EXPIRE, ADJUST

    @Column(length = 100)
    private String referenceId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
