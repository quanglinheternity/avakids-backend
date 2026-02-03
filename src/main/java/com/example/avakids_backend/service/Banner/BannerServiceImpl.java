package com.example.avakids_backend.service.Banner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.Banner.BannerCreateRequest;
import com.example.avakids_backend.DTO.Banner.BannerResponse;
import com.example.avakids_backend.DTO.Banner.BannerSearchRequest;
import com.example.avakids_backend.DTO.Banner.BannerUpdateRequest;
import com.example.avakids_backend.entity.Banner;
import com.example.avakids_backend.mapper.BannerMapper;
import com.example.avakids_backend.repository.Banner.BannerRepository;
import com.example.avakids_backend.util.file.sevrice.CloudService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;
    private final BannerMapper bannerMapper;
    private final BannerValidator bannerValidator;
    private final CloudService fileStorageService;
    private static final String BANNER_IMAGE_FOLDER = "banners";

    @Override
    public BannerResponse createBanner(BannerCreateRequest request, MultipartFile file) {
        fileStorageService.validateImage(file);
        Banner banner = bannerMapper.toEntity(request);
        String imageUrl = fileStorageService.uploadFile(file, BANNER_IMAGE_FOLDER);
        Integer displayOrder = bannerValidator.getNextDisplayOrder(request.getPosition());
        banner.setImageUrl(imageUrl);
        banner.setDisplayOrder(displayOrder);
        Banner savedBanner = bannerRepository.save(banner);
        return bannerMapper.toDTO(savedBanner);
    }

    @Override
    public BannerResponse updateBanner(Long id, BannerUpdateRequest request, MultipartFile file) {
        Banner banner = bannerValidator.getBannerById(id);
        bannerMapper.updateEntity(request, banner);

        if (file != null && !file.isEmpty()) {
            fileStorageService.validateImage(file);
            fileStorageService.deleteFile(banner.getImageUrl());
            String imageUrl = fileStorageService.uploadFile(file, BANNER_IMAGE_FOLDER);
            banner.setImageUrl(imageUrl);
        }
        Banner updatedBanner = bannerRepository.save(banner);
        return bannerMapper.toDTO(updatedBanner);
    }

    @Override
    @Transactional(readOnly = true)
    public BannerResponse getBannerById(Long id) {
        Banner banner = bannerValidator.getBannerById(id);
        return bannerMapper.toDTO(banner);
    }

    @Override
    public void deleteBanner(Long id) {
        Banner banner = bannerValidator.getBannerById(id);
        fileStorageService.deleteFile(banner.getImageUrl());
        bannerRepository.delete(banner);
    }

    @Transactional(readOnly = true)
    public Page<BannerResponse> getSearchBanners(BannerSearchRequest request, Pageable pageable) {
        return bannerRepository.searchBanners(request, pageable);
    }
}
