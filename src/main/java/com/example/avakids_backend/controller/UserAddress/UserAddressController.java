package com.example.avakids_backend.controller.UserAddress;

import java.util.List;

import com.example.avakids_backend.DTO.UserAddress.UserAddressUpdateRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.UserAddress.UserAddressAddRequest;
import com.example.avakids_backend.DTO.UserAddress.UserAddressResponse;
import com.example.avakids_backend.service.UserAddress.UserAddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/userAddress")
@RequiredArgsConstructor
@Tag(name = "User Address", description = "APIs for managing User Address")
public class UserAddressController {
    private final UserAddressService userAddressService;

    @Operation(summary = "Get all Address by User with pagination")
    @GetMapping("/list")
    public ApiResponse<List<UserAddressResponse>> getAllUsers() {
        return ApiResponse.<List<UserAddressResponse>>builder()
                .message("Lấy danh sách thành công")
                .data(userAddressService.getByUser())
                .build();
    }

    @Operation(summary = "Create or a new Address")
    @PostMapping("/create")
    public ApiResponse<UserAddressResponse> create(@RequestBody @Valid UserAddressAddRequest request) {

        return ApiResponse.<UserAddressResponse>builder()
                .message("Tạo địa chỉ nhận hàng thành công")
                .data(userAddressService.create(request))
                .build();
    }

    @Operation(summary = "Update a Address by ID")
    @PutMapping("/{id}/update")
    public ApiResponse<UserAddressResponse> update(
            @PathVariable Long id, @RequestBody @Valid UserAddressUpdateRequest request) {
        return ApiResponse.<UserAddressResponse>builder()
                .message("Cập nhật địa chỉ nhận hàng thành công")
                .data(userAddressService.update(id, request))
                .build();
    }
    @Operation(summary = "Delete a Address by ID")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userAddressService.delete(id);
        return ApiResponse.<Void>builder().message("Xóa địa chỉ nhận hàng thành công").build();
    }
}
