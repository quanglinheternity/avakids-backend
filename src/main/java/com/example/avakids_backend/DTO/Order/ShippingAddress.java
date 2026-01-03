package com.example.avakids_backend.DTO.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {

    private String recipientName;
    private String phone;
    private String address;
}