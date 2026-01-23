package com.example.avakids_backend.controller.Payment;

import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.avakids_backend.DTO.Payment.PaymentResponse;
import com.example.avakids_backend.service.PaymentVnPay.PaymentVnPayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/payment/vnpay/")
@RequiredArgsConstructor
@Tag(name = "Payment Gateway", description = "APIs for handling payment gateway callbacks and transactions")
public class PaymentController {
    private final PaymentVnPayService PaymentVnPayService;

    @Operation(
            summary = "VNPay return callback",
            description =
                    "Handle return URL callback from VNPay payment gateway after payment completion. This endpoint processes payment results and redirects users back to the application.")
    @GetMapping("/return")
    public ResponseEntity<?> handleVnPayReturn(HttpServletRequest request) {
        try {
            log.info("Receiving VNPay return callback");

            // Lấy tất cả parameters từ VNPay
            Map<String, String> vnpParams = PaymentVnPayService.getVnPayParams(request);

            // Xử lý payment response
            PaymentResponse response = PaymentVnPayService.processVnPayReturn(vnpParams);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing VNPay return: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing payment: " + e.getMessage());
        }
    }

    @Operation(
            summary = "VNPay IPN (Instant Payment Notification) callback",
            description =
                    "Handle IPN callback from VNPay for server-to-server payment verification. This endpoint receives real-time payment notifications from VNPay.")
    @GetMapping("/ipn")
    public ResponseEntity<?> handleVnPayIPN(HttpServletRequest request) {
        try {
            log.info("Receiving VNPay IPN callback");

            Map<String, String> vnpParams = PaymentVnPayService.getVnPayParams(request);
            PaymentResponse response = PaymentVnPayService.processVnPayReturn(vnpParams);

            if (response.isSuccess()) {
                return ResponseEntity.ok(Map.of(
                        "RspCode", "00",
                        "Message", "Success"));
            } else {
                return ResponseEntity.ok(Map.of(
                        "RspCode", "99",
                        "Message", "Failed"));
            }

        } catch (Exception e) {
            log.error("Error processing VNPay IPN: ", e);
            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Error: " + e.getMessage()));
        }
    }
}
