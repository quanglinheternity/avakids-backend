package com.example.avakids_backend.service.UserAddress;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.Entity.UserAddress;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.UserAddress.UserAddressRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAddressValidator {
    private final UserAddressRepository userAddressRepository;

    public UserAddress getAddressByIdAndUser(Long addressId, Long id) {
        return userAddressRepository
                .findByIdAndUserId(addressId, id)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
    }
}
