package com.example.avakids_backend.DTO.Banner;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.avakids_backend.entity.Banner.BannerPosition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerCreateRequest {

    @NotBlank(message = "BANNER_TITLE_BLANK")
    private String title;

    private String linkUrl;

    @NotNull(message = "BANNER_POSITION_NULL")
    private BannerPosition position;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Boolean isActive;
}
