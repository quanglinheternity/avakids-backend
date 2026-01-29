package com.example.avakids_backend.repository.Blog;

import org.springframework.data.domain.Page;

import com.example.avakids_backend.entity.Blog;

public interface BlogRepositoryCustom {
    Page<Blog> getAll(int page, int size, String keyword);
}
