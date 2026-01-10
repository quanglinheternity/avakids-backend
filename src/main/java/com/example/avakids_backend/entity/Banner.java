package com.example.avakids_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "banners")
@Getter
@Setter
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BannerPosition position;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BannerPosition {
        TOP,
        MIDDLE,
        BOTTOM,
        SIDEBAR;

        @JsonCreator
        public static BannerPosition from(String value) {
            return BannerPosition.valueOf(value.toUpperCase());
        }
    }
}
