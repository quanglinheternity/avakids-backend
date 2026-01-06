package com.example.avakids_backend.DTO.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateRequest {

    private Long parentId;

    @NotBlank(message = "CATEGORY_NAME_REQUIRED")
    @Size(max = 255, message = "CATEGORY_TOO_LONG")
    private String name;

    @NotBlank(message = "CATEGORY_SLUG_REQUIRED")
    @Size(max = 255)
    private String slug;

    private Integer displayOrder;

    private Boolean isActive;
}
