package com.example.avakids_backend.controller.UserVip;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.UserVip.RedeemPreviewResponse;
import com.example.avakids_backend.service.UserVip.UserVipService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing customer orders")
public class VipPointController {
    private final UserVipService vipPointService;

    @Operation(
            summary = "Preview redeem points",
            description = "Xem trước số point sẽ được sử dụng cho đơn hàng (không trừ point)")
    @GetMapping("/preview")
    public ResponseEntity<ApiResponse<RedeemPreviewResponse>> previewRedeemPoints(
            @RequestParam BigDecimal orderAmount) {

        return ResponseEntity.ok()
                .body(ApiResponse.<RedeemPreviewResponse>builder()
                        .message("Xem trước số point sử dụng thành công.")
                        .data(vipPointService.previewRedeemPoints(orderAmount))
                        .build());
    }

    @Operation(
            summary = "Process VIP tier renewal",
            description = "Kiểm tra và gia hạn / downgrade VIP nếu đủ điều kiện")
    @PostMapping("/vip/process")
    public ResponseEntity<ApiResponse<Void>> processVipTier(@RequestParam Long userId) {

        vipPointService.checkAndRenewVipTier(userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Gói VIP được xử lý thành công.")
                .build());
    }
}
