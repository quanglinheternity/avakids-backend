package com.example.avakids_backend.service.Blog;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.Blog.BlogCreateRequest;
import com.example.avakids_backend.DTO.Blog.BlogResponse;
import com.example.avakids_backend.DTO.Blog.BlogUpdateRequest;
import com.example.avakids_backend.entity.Blog;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.mapper.BlogMapper;
import com.example.avakids_backend.repository.Blog.BlogRepository;
import com.example.avakids_backend.util.file.sevrice.FileStorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogMapper blogMapper;
    private final FileStorageService fileStorageService;
    private static final String BLOG_IMAGE_FOLDER = "blogs";

    @Override
    public BlogResponse create(BlogCreateRequest request, MultipartFile file) {
        fileStorageService.validateImage(file);
        if (blogRepository.existsBySlug(request.getSlug())) {
            throw new AppException(ErrorCode.BLOG_SLUG_ALREADY_EXISTS);
        }
        Blog blog = blogMapper.toEntity(request);
        String imageUrl = fileStorageService.uploadFile(file, BLOG_IMAGE_FOLDER);
        blog.setThumbnailUrl(imageUrl);
        return blogMapper.toResponse(blogRepository.save(blog));
    }

    @Override
    public BlogResponse update(String id, BlogUpdateRequest request, MultipartFile file) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_NULL));

        blogMapper.updateEntity(request, blog);
        if (file != null && !file.isEmpty()) {
            fileStorageService.validateImage(file);
            fileStorageService.deleteFile(blog.getThumbnailUrl());
            String imageUrl = fileStorageService.uploadFile(file, BLOG_IMAGE_FOLDER);
            blog.setThumbnailUrl(imageUrl);
        }
        return blogMapper.toResponse(blogRepository.save(blog));
    }

    @Override
    public BlogResponse getBySlug(String slug) {
        Blog blog = blogRepository.findBySlug(slug).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_NULL));

        // tăng view count
        blog.setViewCount(blog.getViewCount() + 1);
        blogRepository.save(blog);

        return blogMapper.toResponse(blog);
    }

    @Override
    public Page<BlogResponse> getAll(int page, int size, String keyword) {
        Page<Blog> blogPage = blogRepository.getAll(page, size, keyword);

        return blogPage.map(blogMapper::toResponse);
    }

    @Override
    public void delete(String id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_NULL));
        fileStorageService.deleteFile(blog.getThumbnailUrl());
        blogRepository.delete(blog);
    }
}
