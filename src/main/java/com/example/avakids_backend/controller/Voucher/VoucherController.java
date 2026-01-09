package com.example.avakids_backend.controller.Voucher;

import com.example.avakids_backend.DTO.Order.OrderResponse;
import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.DTO.UserAddress.UserAddressResponse;
import com.example.avakids_backend.DTO.UserAddress.UserAddressUpdateRequest;
import com.example.avakids_backend.DTO.Voucher.*;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.service.Voucher.VoucherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/voucher")
@RequiredArgsConstructor
@Tag(name = "Voucher", description = "APIs for managing Voucher")
public class VoucherController {
    private final VoucherService voucherService;

    @Operation(summary = "Create or a new Voucher")
    @PostMapping("/create")
    public ApiResponse<VoucherResponse> create(@RequestBody @Valid VoucherCreateRequest request) {

        return ApiResponse.<VoucherResponse>builder()
                .message("Tạo mã giảm giá thành công")
                .data(voucherService.createVoucher(request))
                .build();
    }
    @Operation(summary = "Update a voucher by ID")
    @PutMapping("/{id}/update")
    public ApiResponse<VoucherResponse> update(
            @PathVariable Long id, @RequestBody @Valid VoucherUpdateRequest request) {
        return ApiResponse.<VoucherResponse>builder()
                .message("Cập nhật mã giảm giá thành công")
                .data(voucherService.updateVoucher(id, request))
                .build();
    }
    @Operation(summary = "Detaile a voucher by ID")
    @GetMapping("/{id}/detaile")
    public ApiResponse<VoucherResponse> getVoucherById(
            @PathVariable Long id) {
        return ApiResponse.<VoucherResponse>builder()
                .message("Lấy chi tiết mã giảm giá thành công")
                .data(voucherService.getVoucher(id))
                .build();
    }
    @Operation(summary = "Delete a Voucher by ID")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ApiResponse.<Void>builder()
                .message("Xóa mã giảm giá thành công")
                .build();
    }
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
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<VoucherValidationResponse>>validateVoucher(
            @Valid @RequestBody VoucherValidationRequest dto) {
        return ResponseEntity.ok()
                .body(ApiResponse.<VoucherValidationResponse>builder()
                        .message("Kiểm tra mã giảm giá thành công.")
                        .data(voucherService.validateVoucher(dto))
                        .build());
    }
}
