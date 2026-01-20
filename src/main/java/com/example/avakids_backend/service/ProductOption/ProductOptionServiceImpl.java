package com.example.avakids_backend.service.ProductOption;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.avakids_backend.DTO.ProductOption.OptionRequest;
import com.example.avakids_backend.DTO.ProductOption.OptionValueRequest;
import com.example.avakids_backend.DTO.ProductOption.ProductOptionResponse;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.ProductOption;
import com.example.avakids_backend.entity.ProductOptionValue;
import com.example.avakids_backend.mapper.ProductOptionMapper;
import com.example.avakids_backend.repository.ProductOption.ProductOptionRepository;
import com.example.avakids_backend.service.Product.ProductValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOptionServiceImpl implements ProductOptionService {
    private final ProductOptionRepository optionRepo;
    private final ProductOptionValidator productOptionValidator;
    private final ProductOptionMapper optionMapper;
    private final ProductValidator productValidator;

    @Override
    @Transactional
    public List<ProductOptionResponse> getOptionByProduct(Long productId) {
        productValidator.getProductById(productId);

        return optionRepo.findByProductId(productId).stream()
                .map(optionMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductOptionResponse addNewOption(OptionRequest request) {
        Product product = productValidator.getProductById(request.getProductId());
        productOptionValidator.validateAddOptionExists(product, request.getName());

        ProductOption productOption = optionMapper.toEntity(request, product);

        addValuesInternal(productOption, request.getOptionValues(), 0);

        ProductOption saved = optionRepo.save(productOption);

        return optionMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ProductOptionResponse addValuesToOption(Long optionId, List<OptionValueRequest> optionValues) {
        ProductOption option = productOptionValidator.getById(optionId);

        int currentMaxOrder = option.getValues().stream()
                .mapToInt(ProductOptionValue::getDisplayOrder)
                .max()
                .orElse(-1);

        addValuesInternal(option, optionValues, currentMaxOrder + 1);
        ProductOption saved = optionRepo.save(option);
        return optionMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ProductOptionResponse updateOption(Long optionId, OptionRequest request) {
        ProductOption option = productOptionValidator.getById(optionId);

        if (request.getName() != null && !request.getName().isBlank()) {
            productOptionValidator.validateUpdateOptionExists(option.getProduct(), request.getName(), option.getId());
            option.setName(request.getName());
        }

        if (request.getOptionValues() != null) {
            syncOptionValues(option, request.getOptionValues());
        }

        ProductOption saved = optionRepo.save(option);
        return optionMapper.toResponseDTO(saved);
    }

    private void addValuesInternal(ProductOption option, List<OptionValueRequest> values, int startOrder) {
        Set<String> existingValues =
                option.getValues().stream().map(v -> v.getValue().toLowerCase()).collect(Collectors.toSet());

        int order = startOrder;

        for (OptionValueRequest req : values) {
            String normalized = req.getValue().toLowerCase();

            if (existingValues.add(normalized)) {
                option.addValue(ProductOptionValue.builder()
                        .value(req.getValue())
                        .displayOrder(order++)
                        .build());
            }
        }
    }

    private void syncOptionValues(ProductOption option, List<OptionValueRequest> requests) {
        Map<Long, ProductOptionValue> currentMap =
                option.getValues().stream().collect(Collectors.toMap(ProductOptionValue::getId, v -> v));

        Map<String, ProductOptionValue> existingNameMap = option.getValues().stream()
                .collect(Collectors.toMap(v -> v.getValue().toLowerCase(), v -> v, (a, b) -> a));

        int maxOrder = option.getValues().stream()
                .mapToInt(ProductOptionValue::getDisplayOrder)
                .max()
                .orElse(-1);

        for (OptionValueRequest req : requests) {
            String normalized = req.getValue().trim().toLowerCase();

            if (existingNameMap.containsKey(normalized)) {
                ProductOptionValue existByName = existingNameMap.get(normalized);

                if (req.getId() == null || !existByName.getId().equals(req.getId())) {
                    continue;
                }
            }

            if (req.getId() != null && currentMap.containsKey(req.getId())) {
                ProductOptionValue existing = currentMap.get(req.getId());

                existing.setValue(req.getValue());

                if (req.getDisplayOrder() != null) {
                    existing.setDisplayOrder(req.getDisplayOrder());
                }

                existingNameMap.put(normalized, existing);

            } else {
                int order = req.getDisplayOrder() != null ? req.getDisplayOrder() : ++maxOrder;

                ProductOptionValue newValue = ProductOptionValue.builder()
                        .value(req.getValue())
                        .displayOrder(order)
                        .option(option)
                        .build();

                option.addValue(newValue);
                existingNameMap.put(normalized, newValue);
                maxOrder = Math.max(maxOrder, order);
            }
        }
    }
}
