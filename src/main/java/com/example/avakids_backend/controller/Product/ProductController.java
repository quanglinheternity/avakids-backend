package com.example.avakids_backend.controller.Product;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Product.ProductCreateRequest;
import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductUpdateRequest;
import com.example.avakids_backend.service.Product.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "product", description = "APIs for managing product")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all product with pagination")
    @GetMapping("/list")
    public ApiResponse<List<ProductResponse>> getAllUsers() {
        return ApiResponse.<List<ProductResponse>>builder()
                .message("Lấy danh sách thành công")
                .data(productService.getAll())
                .build();
    }

    @Operation(summary = "Create or a new Product")
    @PostMapping("/create")
    public ApiResponse<ProductResponse> create(@RequestBody @Valid ProductCreateRequest request) {

        return ApiResponse.<ProductResponse>builder()
                .message("Tạo địa chỉ nhận hàng thành công")
                .data(productService.create(request))
                .build();
    }

    @Operation(summary = "Update a Product by ID")
    @PutMapping("/{id}/update")
    public ApiResponse<ProductResponse> update(
            @PathVariable Long id, @RequestBody @Valid ProductUpdateRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .message("Cập nhật sản phẩm thành công")
                .data(productService.update(id, request))
                .build();
    }

    @Operation(summary = "Delete a Product by ID")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.<Void>builder().message("Xóa sản phẩm thành công").build();
    }
}
