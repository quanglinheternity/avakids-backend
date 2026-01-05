package com.example.avakids_backend.DTO.UserAddress;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAddressResponse {

    private Long id;
    private String recipientName;
    private String phone;
    private String address;
    private String district;
    private String city;
    private String province;
    private Boolean isDefault;
}
