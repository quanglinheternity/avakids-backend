package com.example.avakids_backend.service.PaymentVnPay;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.example.avakids_backend.DTO.Payment.CreateVnPayPaymentResponse;
import com.example.avakids_backend.DTO.Payment.PaymentResponse;
import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.Payment;

public interface PaymentVnPayService {
    CreateVnPayPaymentResponse createVnPayPayment(Payment payment, Order order);

    Map<String, String> getVnPayParams(HttpServletRequest request);

    PaymentResponse processVnPayReturn(Map<String, String> vnpParams);
}
