package com.example.avakids_backend.service.ProductVariant;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.ProductVariant.*;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.ProductOptionValue;
import com.example.avakids_backend.entity.ProductVariant;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.mapper.ProductVariantMapper;
import com.example.avakids_backend.repository.ProductOption.ProductOptionValueRepository;
import com.example.avakids_backend.repository.ProductVariant.ProductVariantRepository;
import com.example.avakids_backend.service.Product.ProductValidator;
import com.example.avakids_backend.util.codeGenerator.CodeGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository variantRepository;
    private final ProductVariantMapper variantMapper;
    private final ProductVariantValidator variantValidator;
    private final ProductOptionValueRepository optionValueRepository;
    private final ProductValidator productValidator;
    private static final String PRODUCT_VARIANT = "PV";

    @Override
    @Transactional
    public ProductVariantResponse createProductVariant(Long productId, AddProductVariantRequest dto) {

        Product product = productValidator.getProductById(productId);

        ProductVariant variant = variantMapper.toEntity(dto);
        variant.setProduct(product);

        variant.setSku(CodeGenerator.generateCode(PRODUCT_VARIANT));

        if (dto.getOptionValueIds() == null || dto.getOptionValueIds().isEmpty()) {
            throw new AppException(ErrorCode.OPTION_VALUE_REQUIRED);
        }
        variantValidator.existsVariantWithExactOptions(productId, dto.getOptionValueIds());
        List<ProductOptionValue> optionValues =
                optionValueRepository.findAllByIdAndProductId(dto.getOptionValueIds(), productId);

        if (optionValues.size() != dto.getOptionValueIds().size()) {
            throw new AppException(ErrorCode.OPTION_VALUE_NOT_BELONG_TO_PRODUCT);
        }

        variantValidator.validateNoDuplicateOption(optionValues);

        variant.setOptionValues(optionValues);

        handleDefaultVariant(productId, variant);

        if (variant.getImages() != null) {
            variant.getImages().forEach(img -> img.setVariant(variant));
        }
        ProductVariantResponse response = variantMapper.toResponse(variantRepository.save(variant));
        updateFullAggregate(productId);
        return response;
    }

    @Override
    @Transactional
    public ProductVariantResponse updateProductVariant(
            Long productId, Long variantId, UpdateProductVariantRequest dto) {
        ProductVariant variant = variantValidator.getVariantById(variantId);
        variantValidator.validateVariantBelongsToProduct(variant, productId);

        variantMapper.updateEntityFromDto(dto, variant);
        if (dto.getOptionValueIds() != null) {

            if (dto.getOptionValueIds().isEmpty()) {
                throw new AppException(ErrorCode.OPTION_VALUE_REQUIRED);
            }
            variantValidator.validateNoDuplicateVariantForUpdate(productId, variantId, dto.getOptionValueIds());
            List<ProductOptionValue> optionValues =
                    optionValueRepository.findAllByIdAndProductId(dto.getOptionValueIds(), productId);

            if (optionValues.size() != dto.getOptionValueIds().size()) {
                throw new AppException(ErrorCode.OPTION_VALUE_NOT_BELONG_TO_PRODUCT);
            }

            variantValidator.validateNoDuplicateOption(optionValues);

            variant.setOptionValues(optionValues);
        }

        handleDefaultVariant(productId, variant);

        if (variant.getImages() != null) {
            variant.getImages().forEach(img -> img.setVariant(variant));
        }
        ProductVariantResponse response = variantMapper.toResponse(variantRepository.save(variant));
        updateFullAggregate(productId);
        return response;
    }

    @Override
    public ProductVariantResponse getVariantById(Long productId, Long variantId) {

        ProductVariant variant = variantValidator.getVariantById(variantId);

        variantValidator.validateVariantBelongsToProduct(variant, productId);
        return variantMapper.toResponse(variant);
    }

    @Override
    public List<ProductVariantResponse> getVariantsByProduct(Long productId) {

        productValidator.getProductById(productId);

        return variantRepository.findAllByProduct_Id(productId).stream()
                .map(variantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteProductVariant(Long productId, Long variantId) {

        ProductVariant variant =
                variantRepository.findById(variantId).orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        if (!variant.getProduct().getId().equals(productId)) {
            throw new AppException(ErrorCode.VARIANT_NOT_BELONG_TO_PRODUCT);
        }

        boolean wasDefault = Boolean.TRUE.equals(variant.getIsDefault());

        variantRepository.delete(variant);

        if (wasDefault) {
            variantRepository.findFirstByProduct_IdOrderByIdAsc(productId).ifPresent(v -> {
                v.setIsDefault(true);
                variantRepository.save(v);
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantDetailResponse findVariantByOptions(Long productId, List<Long> optionValueIds) {

        return variantRepository
                .findExactVariant(productId, optionValueIds)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantDetailResponse getVariantBySku(Long productId, String sku) {

        return variantRepository
                .findVariantBySku(productId, sku)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
    }

    private void handleDefaultVariant(Long productId, ProductVariant variant) {
        if (Boolean.TRUE.equals(variant.getIsDefault())) {
            variantRepository.resetDefaultVariant(productId);

            variant.setIsDefault(true);
        }
    }

    @Transactional
    public void updateFullAggregate(Long productId) {

        ProductAggregateResult agg = variantRepository.aggregateByProductId(productId);

        Product product = productValidator.getProductById(productId);

        if (agg == null || agg.getVariantCount() == 0) {
            if (!product.getHasVariants() && product.getTotalStock() == 0 && product.getMinPrice() == null) {
                return;
            }

            product.setHasVariants(false);
            product.setMinPrice(null);
            product.setMaxPrice(null);
            product.setTotalStock(0);
        } else {
            product.setHasVariants(true);
            product.setMinPrice(agg.getMinPrice());
            product.setMaxPrice(agg.getMaxPrice());
            product.setTotalStock(agg.getTotalStock());
        }
    }

    @Transactional
    public void updateStockOnly(Long variantId) {
        ProductVariant variant = variantValidator.getVariantById(variantId);
        Product product = variant.getProduct();

        Integer totalStock = variantRepository.sumStockByProductId(product.getId());

        if (!Objects.equals(product.getTotalStock(), totalStock)) {
            product.setTotalStock(totalStock);
        }
    }
}
