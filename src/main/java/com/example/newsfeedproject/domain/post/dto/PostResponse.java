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
    private final int likeCount;

    public PostResponse(Long postId, String content, String username, LocalDateTime createdAt, LocalDateTime updatedAt, int likeCount) {
        this.postId = postId;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likeCount = likeCount;
    }
}
