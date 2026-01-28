package com.example.avakids_backend.DTO.Blog;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogResponse {

    private String id;
    private String title;
    private String slug;
    private String content;
    private String thumbnailUrl;
    private Integer viewCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
