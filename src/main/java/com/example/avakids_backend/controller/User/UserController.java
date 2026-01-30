package com.example.avakids_backend.controller.User;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.User.UserCreateRequest;
import com.example.avakids_backend.DTO.User.UserResponse;
import com.example.avakids_backend.DTO.User.UserUpdateRequest;
import com.example.avakids_backend.service.User.UserService;
import com.example.avakids_backend.util.language.I18n;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing system users and profiles")
public class UserController {
    private final UserService userService;
    private final I18n i18n;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Retrieve a list of all users in the system (typically for admin use)")
    @GetMapping("/list")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .message(i18n.t("user.list.success"))
                .data(userService.getAllUsers())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Get user details by ID",
            description = "Retrieve detailed information of a specific user by their ID")
    @GetMapping("/{id}/detail")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .message(i18n.t("user.detail.success"))
                .data(userService.getById(id))
                .build();
    }

    @Operation(
            summary = "Get current user information",
            description = "Retrieve profile information of the currently authenticated user")
    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .message(i18n.t("user.my.info.success"))
                .data(userService.getByToken())
                .build();
    }

    @Operation(summary = "Create a new user", description = "Register a new user account in the system")
    @PostMapping("/create")
    public ApiResponse<UserResponse> create(@RequestBody @Valid UserCreateRequest request) {

        return ApiResponse.<UserResponse>builder()
                .message(i18n.t("user.create.success"))
                .data(userService.createUser(request))
                .build();
    }

    @Operation(
            summary = "Update user profile",
            description = "Update user information including optional avatar upload")
    @PutMapping(value = "/update/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> update(
            @ModelAttribute @Valid UserUpdateRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return ApiResponse.<UserResponse>builder()
                .message(i18n.t("user.update.success"))
                .data(userService.updateUser(request, avatar))
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Delete a user",
            description = "Delete a user account from the system (soft delete or permanent)")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .message(i18n.t("user.delete.success"))
                .build();
    }
}
