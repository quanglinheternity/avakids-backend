package com.example.avakids_backend.service.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Order.CreateOrderRequest;
import com.example.avakids_backend.DTO.Order.OrderResponse;
import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.entity.*;
import com.example.avakids_backend.enums.OrderStatus;
import com.example.avakids_backend.enums.PaymentMethod;
import com.example.avakids_backend.enums.PaymentStatus;
import com.example.avakids_backend.mapper.OrderMapper;
import com.example.avakids_backend.repository.Order.OrderRepository;
import com.example.avakids_backend.repository.Payment.PaymentRepository;
import com.example.avakids_backend.repository.Product.ProductRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import com.example.avakids_backend.service.CartItem.CartItemValidator;
import com.example.avakids_backend.service.Product.ProductValidator;
import com.example.avakids_backend.util.codeGenerator.CodeGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final AuthenticationService authenticationService;
    private final ProductValidator productValidator;
    private final CartItemValidator cartItemValidator;
    private final OrderValidator orderValidator;
    private final PaymentRepository paymentRepository;
    private static final String ORDER_CODE_NAME = "OVD";
    private static final String PAYMENT_CODE_NAME = "PAY";

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = authenticationService.getCurrentUser();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            Product product = productValidator.getProductById(itemRequest.getProductId());
            cartItemValidator.validateStockQuantity(product.getStockQuantity(), itemRequest.getQuantity());

            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .sku(product.getSku())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(itemSubtotal)
                    .build();

            orderItems.add(orderItem);
            subtotal = subtotal.add(itemSubtotal);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;

        BigDecimal shippingFee = calculateShippingFee(subtotal);

        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(shippingFee);

        Order order = Order.builder()
                .orderNumber(CodeGenerator.generateCode(ORDER_CODE_NAME))
                .user(user)
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .customerNote(request.getCustomerNote())
                .orderItems(orderItems)
                .build();

        orderItems.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.save(order);

        Payment payment = Payment.builder()
                .order(savedOrder)
                .paymentNumber(CodeGenerator.generateCode(PAYMENT_CODE_NAME))
                .paymentMethod(request.getPaymentMethod())
                .amount(totalAmount)
                .status(PaymentStatus.PENDING)
                .paidAt(request.getPaymentMethod() == PaymentMethod.COD ? null : LocalDateTime.now())
                .build();
        paymentRepository.save(payment);
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        return orderMapper.toDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Long userId = authenticationService.getCurrentUser().getId();
        Order order = orderValidator.getOrderByIdAndUser(orderId, userId);
        log.info("order{}", order);
        return orderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Pageable pageable) {
        Long userId = authenticationService.getCurrentUser().getId();
        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(OrderSearchRequest request, Pageable pageable) {
        return orderRepository.searchOrders(request, pageable).map(orderMapper::toDTO);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderValidator.getOrderById(orderId);

        OrderStatus currentStatus = order.getStatus();

        orderValidator.validateStatusFinal(currentStatus);
        orderValidator.validateStatusNew(currentStatus, newStatus);
        order.setStatus(newStatus);

        switch (newStatus) {
            case CONFIRMED:
                order.setConfirmedAt(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredAt(LocalDateTime.now());
                break;
            case CANCELLED:
                restoreStock(order);
                break;
            default:
                break;
        }

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDTO(savedOrder);
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private BigDecimal calculateShippingFee(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("500000")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("30000");
    }
}
