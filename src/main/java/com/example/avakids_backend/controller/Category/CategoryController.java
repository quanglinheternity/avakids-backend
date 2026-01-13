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
@Tag(name = "Category Management", description = "APIs for managing product categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Retrieve a list of all product categories in the system")
    @GetMapping("/list")
    public ApiResponse<List<CategoryResponse>> getAllUsers() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Lấy danh sách thành công")
                .data(categoryService.getAll())
                .build();
    }

    @Operation(
            summary = "Create a new category",
            description = "Create a new product category with the provided information")
    @PostMapping("/create")
    public ApiResponse<CategoryResponse> create(@RequestBody @Valid CategoryCreateRequest request) {

        return ApiResponse.<CategoryResponse>builder()
                .message("Tạo danh mục thành công")
                .data(categoryService.create(request))
                .build();
    }

    @Operation(
            summary = "Update a category by ID",
            description = "Update an existing product category with the specified ID")
    @PutMapping("/{id}/update")
    public ApiResponse<CategoryResponse> update(
            @PathVariable Long id, @RequestBody @Valid CategoryUpdateRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .message("Cập nhật danh mục thành công")
                .data(categoryService.update(id, request))
                .build();
    }

    @Operation(
            summary = "Delete a category by ID",
            description = "Delete a product category with the specified ID from the system")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder().message("Xóa danh mục thành công").build();
    }
}
