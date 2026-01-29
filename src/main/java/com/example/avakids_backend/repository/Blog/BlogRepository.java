package com.example.avakids_backend.repository.Blog;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.avakids_backend.entity.Blog;

public interface BlogRepository extends MongoRepository<Blog, String>, BlogRepositoryCustom {

    Optional<Blog> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
