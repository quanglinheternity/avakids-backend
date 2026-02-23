package com.example.avakids_backend.repository.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Dashboard.DashboardOverviewResponse;
import com.example.avakids_backend.DTO.Order.OrderResponse;
import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.entity.Order;

public interface OrderRepositoryCustom {
    Page<Order> searchOrders(OrderSearchRequest request, Pageable pageable);

    Set<Long> findPurchasedCategoryIds(Long customerId);

    boolean hasPurchasedAnyVariantOfProduct(Long customerId, Long productId);

    int countPurchasesInCategory(Long customerId, Long categoryId);

    BigDecimal getAverageOrderValue(Long customerId);

    /**
     * Dashboard
     */
    Long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);

    DashboardOverviewResponse.OrderCompletionRate getCompletionRate();

    Long countPendingOrders();

    List<DashboardOverviewResponse.RevenueByDate> getRevenueByDate(int days);

    List<DashboardOverviewResponse.OrderByStatus> getOrdersByStatus();

    List<DashboardOverviewResponse.TopProduct> getTopProducts(int limit);

    Page<OrderResponse> findByUserIdWithPayment(Long userId, Pageable pageable);
}
