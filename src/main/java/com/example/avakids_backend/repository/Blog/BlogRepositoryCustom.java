package com.example.avakids_backend.repository.Blog;

import com.example.avakids_backend.DTO.Blog.BlogResponse;
import com.example.avakids_backend.entity.Blog;
import org.springframework.data.domain.Page;

public interface BlogRepositoryCustom {
    Page<Blog> getAll(
            int page,
            int size,
            String keyword
    );
}
