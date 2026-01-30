package com.example.avakids_backend.controller.UserAddress;

import java.util.List;

import com.example.avakids_backend.util.language.I18n;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.UserAddress.UserAddressAddRequest;
import com.example.avakids_backend.DTO.UserAddress.UserAddressResponse;
import com.example.avakids_backend.DTO.UserAddress.UserAddressUpdateRequest;
import com.example.avakids_backend.service.UserAddress.UserAddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/userAddress")
@RequiredArgsConstructor
@Tag(name = "User Address Management", description = "APIs for managing user shipping addresses")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class UserAddressController {
    private final UserAddressService userAddressService;
    private final I18n i18n;
    

    @Operation(
            summary = "Get all addresses for current user",
            description = "Retrieve all shipping addresses for the currently authenticated user")
    @GetMapping("/list")
    public ApiResponse<List<UserAddressResponse>> getAllUsers() {
        return ApiResponse.<List<UserAddressResponse>>builder()
                .message(i18n.t("user.address.list.success"))
                .data(userAddressService.getByUser())
                .build();
    }

    @Operation(
            summary = "Create a new shipping address",
            description =
                    "Add a new shipping address for the current user including recipient name, phone, and detailed address")
    @PostMapping("/create")
    public ApiResponse<UserAddressResponse> create(@RequestBody @Valid UserAddressAddRequest request) {

        return ApiResponse.<UserAddressResponse>builder()
                .message(i18n.t("user.address.create.success"))
                .data(userAddressService.create(request))
                .build();
    }

    @Operation(
            summary = "Update an existing address",
            description = "Update an existing shipping address by ID for the current user")
    @PutMapping("/{id}/update")
    public ApiResponse<UserAddressResponse> update(
            @PathVariable Long id, @RequestBody @Valid UserAddressUpdateRequest request) {
        return ApiResponse.<UserAddressResponse>builder()
                .message(i18n.t("user.address.update.success"))
                .data(userAddressService.update(id, request))
                .build();
    }

    @Operation(
            summary = "Delete a shipping address",
            description = "Delete a specific shipping address by ID for the current user")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userAddressService.delete(id);
        return ApiResponse.<Void>builder()
                .message(i18n.t("user.address.delete.success"))
                .build();
    }
}
