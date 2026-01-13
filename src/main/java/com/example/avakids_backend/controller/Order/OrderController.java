package com.example.avakids_backend.controller.Order;

import static com.example.avakids_backend.entity.QOrder.order;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Order.CreateOrderRequest;
import com.example.avakids_backend.DTO.Order.OrderResponse;
import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.enums.OrderStatus;
import com.example.avakids_backend.service.Order.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing customer orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "Create a new order",
            description = "Create a new order from cart items with customer information and shipping details")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<OrderResponse>builder()
                        .message("Đặt hàng thành công.")
                        .data(order)
                        .build());
    }

    @Operation(
            summary = "Get user's orders",
            description = "Retrieve paginated list of orders for the currently authenticated user")
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<OrderResponse> orders = orderService.getUserOrders(pageable);
        return ResponseEntity.ok()
                .body(ApiResponse.<Page<OrderResponse>>builder()
                        .message("Lấy danh sách đơn thành công.")
                        .data(orders)
                        .build());
    }

    @Operation(summary = "Get order details", description = "Get detailed information of a specific order by ID")
    @GetMapping("{orderId}/detail")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrder(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok()
                .body(ApiResponse.<OrderResponse>builder()
                        .message("Lấy đơn chi tiết thành công.")
                        .data(order)
                        .build());
    }

    @Operation(
            summary = "Get all orders",
            description = "Retrieve paginated list of all orders with search and filter capabilities")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            OrderSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.<Page<OrderResponse>>builder()
                        .message("Lấy danh sách đơn thành công.")
                        .data(orderService.getAllOrders(request, pageable))
                        .build());
    }

    @Operation(
            summary = "Update order status",
            description =
                    "Update the status of an existing order (e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId, @RequestParam OrderStatus status) {
        return ResponseEntity.ok()
                .body(ApiResponse.<OrderResponse>builder()
                        .message("Thay đổi trạng thái thành công.")
                        .data(orderService.updateOrderStatus(orderId, status))
                        .build());
    }
}
