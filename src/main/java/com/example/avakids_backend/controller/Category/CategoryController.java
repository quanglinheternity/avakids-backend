package com.example.avakids_backend.controller.Category;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Category.CategoryCreateRequest;
import com.example.avakids_backend.DTO.Category.CategoryResponse;
import com.example.avakids_backend.DTO.Category.CategoryUpdateRequest;
import com.example.avakids_backend.service.Category.CategoryService;
import com.example.avakids_backend.util.language.I18n;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "APIs for managing product categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final I18n i18n;

    @Operation(summary = "Get all categories", description = "Retrieve a list of all product categories in the system")
    @GetMapping("/list")
    public ApiResponse<List<CategoryResponse>> getAllUsers() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .message(i18n.t("category.list.success"))
                .data(categoryService.getAll())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Create a new category",
            description = "Create a new product category with the provided information")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CategoryResponse> create(
            @RequestPart("data") @Valid CategoryCreateRequest request, @RequestPart("file") MultipartFile file) {

        return ApiResponse.<CategoryResponse>builder()
                .message(i18n.t("category.create.success"))
                .data(categoryService.create(request, file))
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Update a category by ID",
            description = "Update an existing product category with the specified ID")
    @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CategoryResponse> update(
            @PathVariable Long id,
            @RequestPart("data") @Valid CategoryUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ApiResponse.<CategoryResponse>builder()
                .message(i18n.t("category.update.success"))
                .data(categoryService.update(id, request, file))
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Delete a category by ID",
            description = "Delete a product category with the specified ID from the system")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder()
                .message(i18n.t("category.delete.success"))
                .build();
    }
}
