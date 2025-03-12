package com.example.newsfeedproject.domain.post.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {
    private final Long postId;
    private final String content;
    private final String username;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponse(Long postId, String content, String username, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
