package com.example.avakids_backend.repository.Banner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Banner.BannerResponse;
import com.example.avakids_backend.DTO.Banner.BannerSearchRequest;
import com.example.avakids_backend.entity.Banner;

public interface BannerRepositoryCustom {
    Integer findMaxDisplayOrderByPosition(Banner.BannerPosition position);

    Page<BannerResponse> searchBanners(BannerSearchRequest request, Pageable pageable);
}
