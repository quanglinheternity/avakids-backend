package com.example.avakids_backend.repository.Category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.Entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
