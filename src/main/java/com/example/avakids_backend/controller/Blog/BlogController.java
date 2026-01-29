package com.example.avakids_backend.controller.Blog;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Blog.BlogCreateRequest;
import com.example.avakids_backend.DTO.Blog.BlogResponse;
import com.example.avakids_backend.DTO.Blog.BlogUpdateRequest;
import com.example.avakids_backend.service.Blog.BlogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/blogs")
@RequiredArgsConstructor
@Tag(name = "Blog Management", description = "API for managing blogs")
public class BlogController {

    private final BlogService blogService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new blog")
    public ResponseEntity<ApiResponse<BlogResponse>> create(
            @RequestPart("data") @Valid BlogCreateRequest request, @RequestPart("file") MultipartFile file) {

        BlogResponse blog = blogService.create(request, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BlogResponse>builder()
                        .message("Tạo blog thành công")
                        .data(blog)
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a blog")
    public ResponseEntity<ApiResponse<BlogResponse>> update(
            @PathVariable String id,
            @RequestPart("data") @Valid BlogUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        BlogResponse blog = blogService.update(id, request, file);

        return ResponseEntity.ok(ApiResponse.<BlogResponse>builder()
                .message("Cập nhật blog thành công")
                .data(blog)
                .build());
    }

    @GetMapping("/slug/{slug}/view")
    @Operation(summary = "View blog by slug")
    public ResponseEntity<ApiResponse<BlogResponse>> getBySlug(@PathVariable String slug) {

        BlogResponse blog = blogService.getBySlug(slug);

        return ResponseEntity.ok(ApiResponse.<BlogResponse>builder()
                .message("Lấy blog thành công")
                .data(blog)
                .build());
    }

    @GetMapping("/list")
    @Operation(summary = "Get all blogs page")
    public ResponseEntity<ApiResponse<Page<BlogResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Page<BlogResponse> blogs = blogService.getAll(page, size, keyword);

        return ResponseEntity.ok(ApiResponse.<Page<BlogResponse>>builder()
                .message("Lấy danh sách blog thành công")
                .data(blogs)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a blog")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {

        blogService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder().message("Xóa blog thành công").build());
    }
}
