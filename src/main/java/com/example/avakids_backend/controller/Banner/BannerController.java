package com.example.avakids_backend.controller.Banner;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Banner.BannerCreateRequest;
import com.example.avakids_backend.DTO.Banner.BannerResponse;
import com.example.avakids_backend.DTO.Banner.BannerSearchRequest;
import com.example.avakids_backend.DTO.Banner.BannerUpdateRequest;
import com.example.avakids_backend.service.Banner.BannerService;
import com.example.avakids_backend.util.language.I18n;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@Tag(name = "Banner Management", description = "API for managing banners")
public class BannerController {
    private final BannerService bannerService;
    private final I18n i18n;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new banner")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(
            @RequestPart("data") @Valid BannerCreateRequest request, @RequestPart("file") MultipartFile file) {
        BannerResponse banner = bannerService.createBanner(request, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BannerResponse>builder()
                        .message(i18n.t("create.success", "banner"))
                        .data(banner)
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an existing banner")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @PathVariable Long id,
            @RequestPart("data") @Valid BannerUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        BannerResponse banner = bannerService.updateBanner(id, request, file);
        return ResponseEntity.ok(ApiResponse.<BannerResponse>builder()
                .message(i18n.t("update.success", "banner"))
                .data(banner)
                .build());
    }

    @GetMapping("/{id}/detaile")
    @Operation(summary = "Get banner by ID")
    public ResponseEntity<ApiResponse<BannerResponse>> getBannerById(@PathVariable Long id) {
        BannerResponse banner = bannerService.getBannerById(id);
        return ResponseEntity.ok(ApiResponse.<BannerResponse>builder()
                .message(i18n.t("get.success", "banner"))
                .data(banner)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a banner")
    public ResponseEntity<ApiResponse<BannerResponse>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.<BannerResponse>builder()
                .message(i18n.t("delete.success", "banner"))
                .build());
    }

    @GetMapping("/sreach")
    @Operation(summary = "BannerSearchRequest a banner")
    public ResponseEntity<ApiResponse<Page<BannerResponse>>> getAll(
            BannerSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Page<BannerResponse>>builder()
                .message(i18n.t("list.success", "banner"))
                .data(bannerService.getSearchBanners(request, pageable))
                .build());
    }

    @GetMapping("/list")
    @Operation(summary = "Get banners for homepage")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getHomeBanners() {

        return ResponseEntity.ok(ApiResponse.<List<BannerResponse>>builder()
                .message(i18n.t("list.success", "banner"))
                .data(bannerService.getBannerList())
                .build());
    }
}
