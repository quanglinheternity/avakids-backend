package com.example.avakids_backend.DTO.Category;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Integer displayOrder;
    private Boolean isActive;
    private Long parentId;
    private List<CategoryResponse> children;
}
