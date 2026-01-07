package com.example.avakids_backend.service.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Order.CreateOrderRequest;
import com.example.avakids_backend.DTO.Order.OrderResponse;
import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.enums.OrderStatus;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(Long orderId);

    Page<OrderResponse> getUserOrders(Pageable pageable);

    Page<OrderResponse> getAllOrders(OrderSearchRequest request, Pageable pageable);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
}
