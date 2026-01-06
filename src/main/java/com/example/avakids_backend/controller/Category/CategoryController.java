package com.example.avakids_backend.controller.Category;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Category.CategoryCreateRequest;
import com.example.avakids_backend.DTO.Category.CategoryResponse;
import com.example.avakids_backend.DTO.Category.CategoryUpdateRequest;
import com.example.avakids_backend.service.Category.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
@Tag(name = "Category", description = "APIs for managing Category Address")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get all Address by User with pagination")
    @GetMapping("/list")
    public ApiResponse<List<CategoryResponse>> getAllUsers() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Lấy danh sách thành công")
                .data(categoryService.getAll())
                .build();
    }

    @Operation(summary = "Create or a new danh ")
    @PostMapping("/create")
    public ApiResponse<CategoryResponse> create(@RequestBody @Valid CategoryCreateRequest request) {

        return ApiResponse.<CategoryResponse>builder()
                .message("Tạo danh mục thành công")
                .data(categoryService.create(request))
                .build();
    }

    @Operation(summary = "Update a danh mục by ID")
    @PutMapping("/{id}/update")
    public ApiResponse<CategoryResponse> update(
            @PathVariable Long id, @RequestBody @Valid CategoryUpdateRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .message("Cập nhật danh mục thành công")
                .data(categoryService.update(id, request))
                .build();
    }

    @Operation(summary = "Delete a danh mục by ID")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder().message("Xóa danh mục thành công").build();
    }
}
