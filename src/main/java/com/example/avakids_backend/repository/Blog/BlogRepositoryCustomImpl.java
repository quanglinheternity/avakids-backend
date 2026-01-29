package com.example.avakids_backend.repository.Blog;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.Blog;
import com.example.avakids_backend.mapper.BlogMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BlogRepositoryCustomImpl implements BlogRepositoryCustom {
    private final MongoTemplate mongoTemplate;
    private final BlogMapper blogMapper;

    @Override
    public Page<Blog> getAll(int page, int size, String keyword) {
        Query query = new Query();

        // 🔍 Search
        if (keyword != null && !keyword.isBlank()) {
            query.addCriteria(new Criteria()
                    .orOperator(
                            Criteria.where("title").regex(keyword, "i"),
                            Criteria.where("content").regex(keyword, "i")));
        }

        // 📊 Total
        long total = mongoTemplate.count(query, Blog.class);

        // 📄 Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        query.with(pageable);

        List<Blog> blogs = mongoTemplate.find(query, Blog.class);

        return new PageImpl<>(blogs, pageable, total);
    }
}
