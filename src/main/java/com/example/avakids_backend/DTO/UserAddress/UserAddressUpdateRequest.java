package com.example.avakids_backend.DTO.UserAddress;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressUpdateRequest {

    @Size(max = 100, message = "RECIPIENT_NAME_TOO_LONG")
    private String recipientName;

    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "PHONE_INVALID")
    private String phone;

    @Size(max = 255, message = "ADDRESS_TOO_LONG")
    private String address;

    private String district;

    private String city;

    private String province;

    private Boolean isDefault;
}
