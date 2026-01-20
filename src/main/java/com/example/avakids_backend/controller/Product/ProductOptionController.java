package com.example.avakids_backend.controller.Product;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ProductOption.OptionRequest;
import com.example.avakids_backend.DTO.ProductOption.OptionValueRequest;
import com.example.avakids_backend.DTO.ProductOption.ProductOptionResponse;
import com.example.avakids_backend.service.ProductOption.ProductOptionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing e-commerce products")
public class ProductOptionController {
    private final ProductOptionService optionService;

    @GetMapping("/{productId}/options/list")
    public ResponseEntity<List<ProductOptionResponse>> getAllOption(@PathVariable Long productId) {

        List<ProductOptionResponse> option = optionService.getOptionByProduct(productId);
        return ResponseEntity.ok(option);
    }

    @PostMapping("/{productId}/options/create")
    public ResponseEntity<ProductOptionResponse> addOption(
            @PathVariable Long productId, @RequestBody OptionRequest request) {
        request.setProductId(productId);
        ProductOptionResponse option = optionService.addNewOption(request);
        return ResponseEntity.ok(option);
    }

    @PostMapping("/options/{optionId}/values/create")
    public ResponseEntity<ProductOptionResponse> addOptionValues(
            @PathVariable Long optionId, @RequestBody List<OptionValueRequest> optionValues) {
        ProductOptionResponse option = optionService.addValuesToOption(optionId, optionValues);
        return ResponseEntity.ok(option);
    }

    @PutMapping("options/{optionId}/update")
    public ResponseEntity<ProductOptionResponse> updateOption(
            @PathVariable Long optionId, @RequestBody @Valid OptionRequest request) {
        ProductOptionResponse response = optionService.updateOption(optionId, request);

        return ResponseEntity.ok(response);
    }
}
