package com.example.avakids_backend.DTO.Banner;

import java.time.LocalDateTime;

import com.example.avakids_backend.entity.Banner.BannerPosition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannerResponse {

    private Long id;

    private String title;

    private String imageUrl;

    private String linkUrl;

    private BannerPosition position;

    private Integer displayOrder;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
