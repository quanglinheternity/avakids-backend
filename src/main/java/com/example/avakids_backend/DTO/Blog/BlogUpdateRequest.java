package com.example.avakids_backend.DTO.Blog;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogUpdateRequest {

    private String title;
    private String content;
}
