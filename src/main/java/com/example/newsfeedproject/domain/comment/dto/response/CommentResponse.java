package com.example.newsfeedproject.domain.comment.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final Long commentId;
    private final String content;
    private final Long postId;
    private final String username;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;


    public CommentResponse(Long commentId, String content, Long postId, String username, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.content = content;
        this.postId = postId;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
