package com.example.avakids_backend.service.UserAddress;

import java.util.List;

import com.example.avakids_backend.DTO.UserAddress.UserAddressAddRequest;
import com.example.avakids_backend.DTO.UserAddress.UserAddressResponse;
import com.example.avakids_backend.DTO.UserAddress.UserAddressUpdateRequest;

public interface UserAddressService {

    UserAddressResponse create(UserAddressAddRequest request);

    List<UserAddressResponse> getByUser();

    UserAddressResponse update(Long addressId, UserAddressUpdateRequest request);

    void delete(Long addressId);
}
