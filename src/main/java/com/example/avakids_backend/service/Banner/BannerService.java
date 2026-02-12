package com.example.avakids_backend.service.Banner;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.Banner.BannerCreateRequest;
import com.example.avakids_backend.DTO.Banner.BannerResponse;
import com.example.avakids_backend.DTO.Banner.BannerSearchRequest;
import com.example.avakids_backend.DTO.Banner.BannerUpdateRequest;

public interface BannerService {
    BannerResponse createBanner(BannerCreateRequest request, MultipartFile file);

    BannerResponse updateBanner(Long id, BannerUpdateRequest request, MultipartFile file);

    BannerResponse getBannerById(Long id);

    void deleteBanner(Long id);

    Page<BannerResponse> getSearchBanners(BannerSearchRequest request, Pageable pageable);

    List<BannerResponse> getBannerList();
}
