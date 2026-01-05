package com.example.avakids_backend.controller.User;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.User.UserCreateRequest;
import com.example.avakids_backend.DTO.User.UserResponse;
import com.example.avakids_backend.DTO.User.UserUpdateRequest;
import com.example.avakids_backend.service.User.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "APIs for managing users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get all users with pagination")
    @GetMapping("/list")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .message("Lấy danh sách thành công")
                .data(userService.getAllUsers())
                .build();
    }

    @Operation(summary = "Get user details by ID")
    @GetMapping("/{id}/detail")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .message("Lấy chi tiết thành công")
                .data(userService.getById(id))
                .build();
    }

    @Operation(summary = "Create or a new user")
    @PostMapping("/create")
    public ApiResponse<UserResponse> create(@RequestBody @Valid UserCreateRequest request) {

        return ApiResponse.<UserResponse>builder()
                .message("Tạo người dùng thành công")
                .data(userService.createUser(request))
                .build();
    }

    @Operation(summary = "Update a user by ID")
    @PutMapping(value = "/{id}/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> update(@PathVariable Long id, @ModelAttribute @Valid UserUpdateRequest request,
                                            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return ApiResponse.<UserResponse>builder()
                .message("Cập nhật người dùng thành công")
                .data(userService.updateUser(id, request, avatar))
                .build();
    }

    @Operation(summary = "Delete a user by ID")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder().message("Xóa người dùng thành công").build();
    }
}
