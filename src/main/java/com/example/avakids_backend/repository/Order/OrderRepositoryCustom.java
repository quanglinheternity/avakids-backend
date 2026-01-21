package com.example.avakids_backend.repository.Order;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.entity.Order;

public interface OrderRepositoryCustom {
    Page<Order> searchOrders(OrderSearchRequest request, Pageable pageable);

    Set<Long> findPurchasedCategoryIds(Long customerId);

    boolean hasPurchasedAnyVariantOfProduct(Long customerId, Long productId);

    int countPurchasesInCategory(Long customerId, Long categoryId);

    BigDecimal getAverageOrderValue(Long customerId);
}
