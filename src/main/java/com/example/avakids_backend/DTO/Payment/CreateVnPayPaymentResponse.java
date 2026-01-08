package com.example.avakids_backend.DTO.Payment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVnPayPaymentResponse {

    private String paymentUrl;
    private String vnpTxnRef;
}
