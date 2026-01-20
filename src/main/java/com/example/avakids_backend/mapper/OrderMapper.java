package com.example.avakids_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.example.avakids_backend.DTO.Order.OrderItemResponse;
import com.example.avakids_backend.DTO.Order.OrderResponse;
import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.OrderItem;
import com.example.avakids_backend.enums.OrderStatus;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "orderItems", source = "orderItems")
    @Mapping(target = "statusName", source = "status", qualifiedByName = "mapStatusToDescription")
    OrderResponse toDTO(Order order);

    @Mapping(target = "variantId", source = "variant.id")
    OrderItemResponse toOrderItemDTO(OrderItem orderItem);

    @Named("mapStatusToDescription")
    default String mapStatusToDescription(OrderStatus status) {
        return status != null ? status.getDescription() : null;
    }
}
