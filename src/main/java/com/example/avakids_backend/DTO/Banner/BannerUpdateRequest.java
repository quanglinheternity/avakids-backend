package com.example.avakids_backend.DTO.Banner;

import java.time.LocalDateTime;

import com.example.avakids_backend.entity.Banner.BannerPosition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerUpdateRequest {

    private String title;

    private String imageUrl;

    private String linkUrl;

    private BannerPosition position;
    private Integer displayOrder;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Boolean isActive;
}
