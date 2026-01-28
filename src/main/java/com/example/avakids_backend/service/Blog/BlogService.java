package com.example.avakids_backend.service.Blog;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.Blog.BlogCreateRequest;
import com.example.avakids_backend.DTO.Blog.BlogResponse;
import com.example.avakids_backend.DTO.Blog.BlogUpdateRequest;

public interface BlogService {

    BlogResponse create(BlogCreateRequest request, MultipartFile file);

    BlogResponse update(String id, BlogUpdateRequest request, MultipartFile file);

    BlogResponse getBySlug(String slug);

    Page<BlogResponse> getAll(int page,
                              int size,
                              String keyword);

    void delete(String id);
}
