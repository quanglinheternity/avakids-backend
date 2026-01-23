package com.example.avakids_backend.mapper;

import java.util.Collections;
import java.util.Map;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JsonMapper {

    private final ObjectMapper objectMapper;

    @Named("jsonToMap")
    public Map<String, Object> toMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
