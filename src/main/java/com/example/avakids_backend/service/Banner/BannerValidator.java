package com.example.avakids_backend.service.Banner;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.Banner;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.Banner.BannerRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BannerValidator {
    private final BannerRepository bannerRepository;

    public Integer getNextDisplayOrder(Banner.BannerPosition position) {
        Integer maxOrder = bannerRepository.findMaxDisplayOrderByPosition(position);
        return (maxOrder != null ? maxOrder : -1) + 1;
    }

    public Banner getBannerById(Long id) {
        return bannerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));
    }
}
