package com.example.avakids_backend.DTO.Blog;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogCreateRequest {

    private String title;
    private String slug;
    private String content;
}
