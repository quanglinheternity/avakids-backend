package com.example.avakids_backend.mapper;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.Notification.NotificationResponse;
import com.example.avakids_backend.entity.Notification;

@Mapper(componentModel = "spring", uses = JsonMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(source = "data", target = "data", qualifiedByName = "jsonToMap")
    NotificationResponse toResponse(Notification notification);
}
