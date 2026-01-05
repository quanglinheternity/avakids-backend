package com.example.avakids_backend.DTO.UserAddress;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressAddRequest {

    @NotBlank(message = "RECIPIENT_NAME_REQUIRED")
    @Size(max = 100, message = "RECIPIENT_NAME_TOO_LONG")
    private String recipientName;

    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "PHONE_INVALID")
    private String phone;

    @NotBlank(message = "ADDRESS_REQUIRED")
    @Size(max = 255, message = "ADDRESS_TOO_LONG")
    private String address;

    @NotBlank(message = "DISTRICT_REQUIRED")
    private String district;

    @NotBlank(message = "CITY_REQUIRED")
    private String city;

    @NotBlank(message = "PROVINCE_REQUIRED")
    private String province;

    private Boolean isDefault;
}
