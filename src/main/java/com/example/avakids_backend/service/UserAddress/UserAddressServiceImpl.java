package com.example.avakids_backend.service.UserAddress;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.avakids_backend.DTO.UserAddress.UserAddressAddRequest;
import com.example.avakids_backend.DTO.UserAddress.UserAddressResponse;
import com.example.avakids_backend.DTO.UserAddress.UserAddressUpdateRequest;
import com.example.avakids_backend.Entity.User;
import com.example.avakids_backend.Entity.UserAddress;
import com.example.avakids_backend.mapper.UserAddressMapper;
import com.example.avakids_backend.repository.UserAddress.UserAddressRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {
    private final UserAddressRepository addressRepository;
    private final UserAddressValidator userAddressValidator;
    private final UserAddressMapper userAddressMapper;
    private final AuthenticationService authenticationService;

    @Override
    public UserAddressResponse create(UserAddressAddRequest request) {
        User user = authenticationService.getCurrentUser();

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(user.getId()).ifPresent(a -> {
                a.setIsDefault(false);
                addressRepository.save(a);
            });
        }

        UserAddress address = userAddressMapper.toEntity(request);
        address.setUser(user);

        return userAddressMapper.toResponseDTO(addressRepository.save(address));
    }

    @Override
    public UserAddressResponse update(Long addressId, UserAddressUpdateRequest request) {
        User user = authenticationService.getCurrentUser();
        UserAddress address = userAddressValidator.getAddressByIdAndUser(addressId, user.getId());
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(user.getId()).ifPresent(a -> {
                if (!a.getId().equals(address.getId())) {
                    a.setIsDefault(false);
                    addressRepository.save(a);
                }
            });
        }

        userAddressMapper.updateAddressFromDTO(request, address);

        return userAddressMapper.toResponseDTO(addressRepository.save(address));
    }

    @Override
    public List<UserAddressResponse> getByUser() {
        Long userId = authenticationService.getCurrentUser().getId();
        return addressRepository.findByUserId(userId).stream()
                .map(userAddressMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long addressId) {
        User user = authenticationService.getCurrentUser();
        UserAddress address = userAddressValidator.getAddressByIdAndUser(addressId, user.getId());
        addressRepository.delete(address);
    }
}
