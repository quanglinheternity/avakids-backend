package com.example.avakids_backend.controller.Product;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.ProductOption.OptionRequest;
import com.example.avakids_backend.DTO.ProductOption.OptionValueRequest;
import com.example.avakids_backend.DTO.ProductOption.ProductOptionResponse;
import com.example.avakids_backend.service.ProductOption.ProductOptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing e-commerce products")
public class ProductOptionController {
    private final ProductOptionService optionService;

    @Operation(
            summary = "Get product options",
            description = "Retrieve all options and their values of a specific product.")
    @GetMapping("/{productId}/options/list")
    public ResponseEntity<ApiResponse<List<ProductOptionResponse>>> getAllOption(@PathVariable Long productId) {

        List<ProductOptionResponse> options = optionService.getOptionByProduct(productId);
        return ResponseEntity.ok(ApiResponse.<List<ProductOptionResponse>>builder()
                .message("Lấy danh sách option của sản phẩm thành công")
                .data(options)
                .build());
    }

    @Operation(
            summary = "Create product option",
            description = "Create a new option (such as Size or Color) for a product.")
    @PostMapping("/{productId}/options/create")
    public ResponseEntity<ApiResponse<ProductOptionResponse>> addOption(
            @PathVariable Long productId, @RequestBody OptionRequest request) {
        request.setProductId(productId);
        ProductOptionResponse option = optionService.addNewOption(request);
        return ResponseEntity.ok(ApiResponse.<ProductOptionResponse>builder()
                .message("Tạo option cho sản phẩm thành công")
                .data(option)
                .build());
    }

    @Operation(summary = "Add option values", description = "Add one or multiple values to an existing product option.")
    @PostMapping("/options/{optionId}/values/create")
    public ResponseEntity<ApiResponse<ProductOptionResponse>> addOptionValues(
            @PathVariable Long optionId, @RequestBody List<OptionValueRequest> optionValues) {
        ProductOptionResponse option = optionService.addValuesToOption(optionId, optionValues);
        return ResponseEntity.ok(ApiResponse.<ProductOptionResponse>builder()
                .message("Thêm giá trị cho option thành công")
                .data(option)
                .build());
    }

    @Operation(summary = "Update product option", description = "Update information of an existing product option.")
    @PutMapping("options/{optionId}/update")
    public ResponseEntity<ApiResponse<ProductOptionResponse>> updateOption(
            @PathVariable Long optionId, @RequestBody @Valid OptionRequest request) {
        ProductOptionResponse response = optionService.updateOption(optionId, request);

        return ResponseEntity.ok(ApiResponse.<ProductOptionResponse>builder()
                .message("Cập nhật option thành công")
                .data(response)
                .build());
    }
}
