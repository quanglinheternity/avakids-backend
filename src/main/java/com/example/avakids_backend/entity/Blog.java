package com.example.avakids_backend.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "blogs")
@Getter
@Setter
public class Blog {

    @Id
    private String id;

    @TextIndexed(weight = 5)
    private String title;

    @Indexed(unique = true)
    private String slug;

    @TextIndexed
    private String content;

    @Field("thumbnail_url")
    private String thumbnailUrl;

    @Field("view_count")
    private Integer viewCount = 0;

    @Field("published_at")
    private LocalDateTime publishedAt;

    @CreatedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
