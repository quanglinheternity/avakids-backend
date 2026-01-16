package com.example.avakids_backend.service.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Order.CreateOrderRequest;
import com.example.avakids_backend.DTO.Order.OrderResponse;
import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.DTO.Payment.CreateVnPayPaymentResponse;
import com.example.avakids_backend.entity.*;
import com.example.avakids_backend.enums.OrderStatus;
import com.example.avakids_backend.enums.PaymentMethod;
import com.example.avakids_backend.enums.PaymentStatus;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.mapper.OrderMapper;
import com.example.avakids_backend.repository.CartItem.CartItemRepository;
import com.example.avakids_backend.repository.Order.OrderRepository;
import com.example.avakids_backend.repository.Payment.PaymentRepository;
import com.example.avakids_backend.repository.Product.ProductRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import com.example.avakids_backend.service.CartItem.CartItemValidator;
import com.example.avakids_backend.service.PaymentVnPay.PaymentVnPayService;
import com.example.avakids_backend.service.Product.ProductValidator;
import com.example.avakids_backend.service.UserVip.UserVipService;
import com.example.avakids_backend.service.Voucher.VoucherService;
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
    private final PaymentVnPayService PaymentVnPayService;
    private final VoucherService voucherService;
    private final UserVipService userVipService;
    private final CartItemRepository cartItemRepository;
    private static final String ORDER_CODE_NAME = "OVD";
    private static final String PAYMENT_CODE_NAME = "PAY";

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = authenticationService.getCurrentUser();
        Order order = createOrderEntity(request, user);

        Order savedOrder = orderRepository.save(order);

        Payment payment = createPayment(savedOrder, request.getPaymentMethod());
        String paymentUrl = null;
        if (request.getPaymentMethod() == PaymentMethod.BANKING) {

            CreateVnPayPaymentResponse vnPayResponse = PaymentVnPayService.createVnPayPayment(payment, savedOrder);

            paymentUrl = vnPayResponse.getPaymentUrl();

            payment.setTransactionId(vnPayResponse.getVnpTxnRef());
            paymentRepository.save(payment);

            paymentRepository.save(payment);
        }

        updateProductStock(savedOrder.getOrderItems());

        removeOrderedItemsFromCart(user, savedOrder.getOrderItems());
        OrderResponse orderResponse = orderMapper.toDTO(savedOrder);
        orderResponse.setPaymentURL(paymentUrl);
        return orderResponse;
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
        User user = authenticationService.getCurrentUser();
        Order order = orderValidator.getOrderById(orderId);

        OrderStatus currentStatus = order.getStatus();

        orderValidator.validateStatusFinal(currentStatus);
        orderValidator.validateStatusNew(currentStatus, newStatus);
        order.setStatus(newStatus);
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow();
        if (newStatus == OrderStatus.COMPLETED && payment.getStatus() != PaymentStatus.PAID) {
            throw new AppException(ErrorCode.ORDER_PAYMENT_REQUIRED);
        }
        switch (newStatus) {
            case CONFIRMED:
                order.setConfirmedAt(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredAt(LocalDateTime.now());
                break;
            case COMPLETED:
                userVipService.processOrderCompletion(user.getId(), orderId, order.getSubtotal());

                break;
            case CANCELLED:
                restoreStock(order);
                BigDecimal refundAmount = BigDecimal.ZERO;
                if (payment.getStatus() == PaymentStatus.PAID) {
                    refundAmount = order.getTotalAmount();
                }

                userVipService.refundPoints(user.getId(), refundAmount, order.getOrderNumber());
                break;
            default:
                break;
        }

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDTO(savedOrder);
    }

    private BigDecimal calculateShippingFee(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("500000")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("30000");
    }

    private Order createOrderEntity(CreateOrderRequest request, User user) {
        String numberCode = CodeGenerator.generateCode(ORDER_CODE_NAME);
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

        // ======================
        // 1. Voucher
        // ======================
        BigDecimal discountVoucher = BigDecimal.ZERO;

        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            VoucherUsage usage = voucherService.applyVoucherToOrder(user, request.getVoucherCode(), null, subtotal);
            discountVoucher = usage.getDiscountAmount();
        }

        // ======================
        // 2. Trừ POINT (chỉ discount)
        // ======================
        BigDecimal pointDiscount = BigDecimal.ZERO;

        if (request.isUseUserVipPoint()) {
            pointDiscount = userVipService.redeemPoints(user.getId(), subtotal, numberCode);
        }
        BigDecimal totalDiscountAmount = discountVoucher.add(pointDiscount);
        // ======================
        // 3. Shipping + Total
        // ======================
        BigDecimal shippingFee = calculateShippingFee(subtotal);

        BigDecimal totalAmount =
                subtotal.subtract(totalDiscountAmount).subtract(pointDiscount).add(shippingFee);

        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        // ======================
        // 4. Create Order
        // ======================
        Order order = Order.builder()
                .orderNumber(numberCode)
                .user(user)
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .discountAmount(discountVoucher)
                .pointAmount(pointDiscount)
                .totalAmount(totalAmount)
                .shippingFee(shippingFee)
                .shippingAddress(request.getShippingAddress())
                .customerNote(request.getCustomerNote())
                .orderItems(orderItems)
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        orderRepository.save(order);

        return order;
    }

    private Payment createPayment(Order order, PaymentMethod paymentMethod) {
        Payment payment = Payment.builder()
                .order(order)
                .paymentNumber(CodeGenerator.generateCode(PAYMENT_CODE_NAME))
                .paymentMethod(paymentMethod)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .paidAt(paymentMethod == PaymentMethod.COD ? null : LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    private void updateProductStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }
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

    @Transactional
    public void removeOrderedItemsFromCart(User user, List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            Optional<CartItem> cartItemOpt = cartItemRepository.findByUserAndProduct(user, product);
            cartItemOpt.ifPresent(cartItemRepository::delete);
        }
    }
}
