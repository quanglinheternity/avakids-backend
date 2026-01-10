package com.example.avakids_backend.DTO.Banner;

import java.time.LocalDateTime;

import com.example.avakids_backend.entity.Banner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerSearchRequest {

    private String title;
    private Banner.BannerPosition position;
    private Boolean isActive;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
