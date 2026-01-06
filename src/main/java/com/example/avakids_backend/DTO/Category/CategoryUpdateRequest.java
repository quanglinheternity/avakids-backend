package com.example.avakids_backend.DTO.Category;

import jakarta.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryUpdateRequest {

    private Long parentId;

    @Size(max = 255, message = "CATEGORY_TOO_LONG")
    private String name;

    @Size(max = 255)
    private String slug;

    private Integer displayOrder;

    private Boolean isActive;
}
