package com.example.avakids_backend.service.Order;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.enums.OrderStatus;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.Order.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderValidator {
    private final OrderRepository orderRepository;

    public void validateStatusFinal(OrderStatus currentStatus) {
        if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED) {
            throw new AppException(ErrorCode.ORDER_STATUS_FINAL);
        }
    }

    public void validateStatusNew(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!currentStatus.canChangeTo(newStatus)) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
    }

    public Order getOrderByIdAndUser(Long orderId, Long userId) {
        return orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_ALREADY_EXISTS));
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_ALREADY_EXISTS));
    }
}
