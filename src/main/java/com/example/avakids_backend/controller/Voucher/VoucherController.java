package com.example.avakids_backend.controller.Voucher;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Voucher.*;
import com.example.avakids_backend.service.Voucher.VoucherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/voucher")
@RequiredArgsConstructor
@Tag(name = "Voucher Management", description = "APIs for managing discount vouchers and promo codes")
public class VoucherController {
    private final VoucherService voucherService;

    @Operation(
            summary = "Create a new voucher",
            description = "Create a new discount voucher with code, discount type, value, and usage rules")
    @PostMapping("/create")
    public ApiResponse<VoucherResponse> create(@RequestBody @Valid VoucherCreateRequest request) {

        return ApiResponse.<VoucherResponse>builder()
                .message("Tạo mã giảm giá thành công")
                .data(voucherService.createVoucher(request))
                .build();
    }

    @Operation(
            summary = "Update a voucher by ID",
            description = "Update an existing voucher's details including discount value and expiration date")
    @PutMapping("/{id}/update")
    public ApiResponse<VoucherResponse> update(
            @PathVariable Long id, @RequestBody @Valid VoucherUpdateRequest request) {
        return ApiResponse.<VoucherResponse>builder()
                .message("Cập nhật mã giảm giá thành công")
                .data(voucherService.updateVoucher(id, request))
                .build();
    }

    @Operation(
            summary = "Get voucher details by ID",
            description = "Retrieve detailed information of a specific voucher")
    @GetMapping("/{id}/detaile")
    public ApiResponse<VoucherResponse> getVoucherById(@PathVariable Long id) {
        return ApiResponse.<VoucherResponse>builder()
                .message("Lấy chi tiết mã giảm giá thành công")
                .data(voucherService.getVoucher(id))
                .build();
    }

    @Operation(summary = "Delete a voucher by ID", description = "Delete a voucher from the system")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ApiResponse.<Void>builder().message("Xóa mã giảm giá thành công").build();
    }

    @Operation(
            summary = "Get all vouchers with pagination",
            description = "Retrieve paginated list of vouchers with search and filter capabilities")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<VoucherResponse>>> getAllOrders(
            VoucherSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.<Page<VoucherResponse>>builder()
                        .message("Lấy mã giảm giá thành công.")
                        .data(voucherService.getAllVoucher(request, pageable))
                        .build());
    }

    @Operation(
            summary = "Validate voucher code",
            description = "Validate a voucher code for a specific order to check eligibility and calculate discount")
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<VoucherValidationResponse>> validateVoucher(
            @Valid @RequestBody VoucherValidationRequest dto) {
        return ResponseEntity.ok()
                .body(ApiResponse.<VoucherValidationResponse>builder()
                        .message("Kiểm tra mã giảm giá thành công.")
                        .data(voucherService.validateVoucher(dto))
                        .build());
    }
}
